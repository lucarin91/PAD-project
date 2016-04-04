package lr.core;

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
        list.forEach(this::add);
    }

    public ConsistentHash(Collection<T> list) {
        this(list, default_replication);
    }

    synchronized public boolean add(T node) {
        boolean insert = true;

        getHashesForKey(node.toString()).forEach(aLong -> _map.put(aLong,node));

        return true;
    }

    public Long doHash(String key){
        return _hash.apply(key);
    }

    synchronized public TreeMap<Long,T> getMap() {
    //    System.out.println("MAP " + _map);
        return _map;
    }

    synchronized public void remove(T node) {
        getHashesForKey(node.toString()).forEach(aLong -> _map.remove(aLong));
    }

    synchronized public T get(String key) {
        long hash = _hash.apply(key);
        Long res = _map.ceilingKey(hash);
        if (res != null) return _map.get(res);
        else {
            return _map.firstEntry().getValue();
        }
    }

    synchronized public List<T> getNext(String key, int n) {
        //TODO: fix this method...
        List<T> list = new ArrayList<>();
        if (n > (_map.size() / _replication) - 1 ) return list;
        List<Long> hashs = getHashesForKey(key);
        long hash = hashs.get(0);
        int i=0;
        while (i < n) {
            Map.Entry<Long, T> entry = _map.ceilingEntry(hash);
            if (entry == null) entry = _map.firstEntry();
            if (!hashs.contains(entry.getKey()) && !list.contains(entry.getValue())) {
                list.add(entry.getValue());
                i++;
            }
            hash = entry.getKey()+1;
        }
        //System.out.println("getNext "+list);
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
        //System.out.println("getPrev "+list);
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
        return "ConsistentHash{" +
                "_map=" + _map +
                ", _replication=" + _replication +
                ", _hash=" + _hash +
                '}';
    }

    synchronized public int size() {
        return _map.size();
    }

}
