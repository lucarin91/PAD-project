package lr.core;

import java.util.*;

/**
 * Created by luca on 24/02/16.
 */

public class ConsistentHash<T> {
    private TreeMap<Long, T> _map;
    private int _replication;
    private Helper.IHash _hash;
    private final static int default_replication = 3;

    public ConsistentHash() {
        this(default_replication, Helper::MD5ToLong);
    }

    public ConsistentHash(int replication) {
        this(replication, Helper::MD5ToLong);
    }

    public ConsistentHash(int replication, Helper.IHash f) {
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

        getReplicaForKey(node.toString()).forEach(aLong -> _map.put(aLong, node));

        return true;
    }

    public Long doHash(String key) {
        return _hash.hash(key);
    }

    synchronized public TreeMap<Long, T> getMap() {
        //    System.out.println("MAP " + _map);
        return _map;
    }

    synchronized public void remove(T node) {
        getReplicaForKey(node.toString()).forEach(aLong -> _map.remove(aLong));
    }

    synchronized public T get(String key) {
        long hash = _hash.hash(key);
        Long res = _map.ceilingKey(hash);
        if (res != null) return _map.get(res);
        else {
            return _map.firstEntry().getValue();
        }
    }

    synchronized public ArrayList<Map.Entry<Long, T>> getNext(String key, int n) {
        long hash = _hash.hash(key);
        return getNext(hash, n);
    }

    synchronized public ArrayList<Map.Entry<Long, T>> getNext(long hash, int n) {
        ArrayList<Map.Entry<Long, T>> res = new ArrayList<>();
        int index = 0;
        NavigableMap<Long, T> orderMap = _map.tailMap(hash, false);
        for (int num = 0; num < 2 && index < n; num++) {

            for (Map.Entry<Long, T> item : orderMap.entrySet()) {
                if (!res.contains(item)) {
                    res.add(item);
                    index++;
                }
                if (index == n) break;
            }
            orderMap = _map.headMap(hash, false);
        }

        System.out.println("getNext " + res);

        return res;
    }

    public List<Long> getReplicaForKey(String key) {
        List<Long> hashes = new ArrayList<>();
        for (int i = 0; i < _replication; i++) {
            hashes.add(_hash.hash(_hash.hash(Math.pow(3, i) + "") + key + _hash.hash(Math.pow(2, i) + "")));
        }
        return hashes;
    }

    synchronized public ArrayList<Map.Entry<Long, T>> getPrev(String key, int n) {
        long hash = _hash.hash(key);
        return getNext(hash, n);
    }

    synchronized public ArrayList<Map.Entry<Long, T>> getPrev(long hash, int n) {
        ArrayList<Map.Entry<Long, T>> res = new ArrayList<>();

        int index = 0;
        NavigableMap<Long, T> orderMap = _map.headMap(hash, false);
        for (int num = 0; num < 2 && index < n; num++) {

            for (Map.Entry<Long, T> item : orderMap.descendingMap().entrySet()) {
                if (!res.contains(item)) {
                    res.add(item);
                    index++;
                }
                if (index == n) break;
            }
            orderMap = _map.tailMap(hash, false);
        }

        System.out.println("getPrev " + res);

        return res;
    }

    public Map.Entry<Long, T> getPrev(String key) {
        return getPrev(key, 1).get(0);
    }

    public Map.Entry<Long, T> getPrev(long hash) {
        return getPrev(hash, 1).get(0);
    }

    public Map.Entry<Long, T> getNext(String key) {
        return getNext(key, 1).get(0);
    }

    public Map.Entry<Long, T> getNext(long hash) {
        return getNext(hash, 1).get(0);
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
