package lr;

import java.util.*;
import java.util.function.Function;

/**
 * Created by luca on 24/02/16.
 */

public class ConsistentHash<T> {
    private TreeMap<Long, T> _map;
    private int _replication;
    private Function<String,Long> _hash;
    private final static int default_replication = 3;

    public ConsistentHash() {
        this(default_replication, Helper::MD5ToLong);
    }

    public ConsistentHash(int replication) {
        this(replication, Helper::MD5ToLong);
    }

    public ConsistentHash(int replication, Function<String,Long> f) {
        _replication = replication;
        _map = new TreeMap<>();
        _hash = f;
    }

    public ConsistentHash(Collection<T> list, int replication) {
        this(replication);
        for (T item : list) {
            add(item);
        }
    }

    public ConsistentHash(Collection<T> list) {
        this(list, default_replication);
    }

    synchronized public boolean add(T node) {
        boolean insert = true;

        getHashesForKey(node.toString()).forEach(aLong -> _map.put(aLong,node));
//        for (int i = 0; i < _replication; i++) {
//            long hash = _hash.apply(node.toString() + i);
//            if (!_map.containsKey(hash))
//                _map.put(hash, node);
//            else
//                insert = false;
//        }
//        return insert;
        return true;
    }

//    public boolean add(String key, T node) {
//        if (!_map.containsKey(MurmurHash.hash32(key))) {
//            _map.add(MurmurHash.hash32(key), node);
//            return true;
//        } else
//            return false;
//    }

    synchronized public TreeMap<Long,T> getMap() {
    //    System.out.println("MAP " + _map);
        return _map;
    }

    synchronized public void remove(T node) {
        getHashesForKey(node.toString()).forEach(aLong -> _map.remove(aLong));
//        for (int i = 0; i < _replication; i++)
//            _map.remove(_hash.apply(node.toString() + i));
    }

    synchronized public T get(String key) {
        System.out.println("MAP "+_map);
        long hash = _hash.apply(key);
        Long res = _map.ceilingKey(hash);
        if (res != null) {
            T n = _map.get(res);
            System.out.println("consistent hashtable map "+ key +" to node "+n);
            return n;
        } else {
            return _map.firstEntry().getValue();
        }
    }

    synchronized public List<T> getNext(String key, int n) {
        List<T> list = new ArrayList<>();
        if (n > (_map.size()/_replication) -1 ) return list;
        List<Long> hashs = getHashesForKey(key);
        long hash = hashs.get(0);
        int i=0;
        while (i<n) {
            Map.Entry<Long, T> entry = _map.ceilingEntry(hash);
            if (entry == null) entry = _map.firstEntry();
            if (!hashs.contains(entry.getKey()) && !list.contains(entry.getValue())) {
                list.add(entry.getValue());
                i++;
            }
            hash = entry.getKey()+1;
        }
        System.out.println("getNext "+list);
        return list;
    }

    public List<Long> getHashesForKey (String key){
        List<Long> hashes = new ArrayList<>();
        for (int i=0; i<_replication; i++){
            hashes.add(_hash.apply(_hash.apply(Math.pow(3,i)+"") + key + _hash.apply(Math.pow(2,i)+"")));
        }
        return hashes;
    }

    synchronized public List<T> getPrev(String key, int n) {
        List<T> list = new ArrayList<>();
        List<Long> hashes = getHashesForKey(key);
        long hash = hashes.get(0);
        int i=0;
        while (i < n) {
            Map.Entry<Long, T> entry = _map.floorEntry(hash);
            if (entry == null) entry = _map.lastEntry();
            if (!hashes.contains(entry.getKey()) && !list.contains(entry.getValue())) {
                list.add(entry.getValue());
                i++;
            }
            hash = entry.getKey()-1;
        }
        System.out.println("getPrev "+list);
        return list;
    }

    public T getPrev(String key){
        return getPrev(key,1).get(0);
    }


    public T getNext(String key){
        return getNext(key,1).get(0);
    }

    @Override
    public String toString() {
        String res = "";
        for (long key : _map.descendingKeySet()) {
            res += "\tkey: " + key + " value: " + _map.get(key) + "\n\t";
        }
        return res;
    }

    synchronized public int size() {
        return _map.size();
    }
}
