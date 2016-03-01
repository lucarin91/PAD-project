package lr;

import ie.ucd.murmur.MurmurHash;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by luca on 24/02/16.
 */

public class ConsistentHash<T> {

    private TreeMap<Integer, T> _map;
    private int _replication;
    private final static int default_replication = 3;

    public ConsistentHash() {
        this(default_replication);
    }

    public ConsistentHash(int replication) {
        _replication = replication;
        _map = new TreeMap<>();
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

    public boolean add(T node) {
        boolean insert = true;
        for (int i = 0; i < _replication; i++) {
            int hash = MurmurHash.hash32(node.toString() + i);
            if (!_map.containsKey(hash))
                _map.put(hash, node);
            else
                insert = false;
        }
        return insert;
    }

//    public boolean add(String key, T node) {
//        if (!_map.containsKey(MurmurHash.hash32(key))) {
//            _map.put(MurmurHash.hash32(key), node);
//            return true;
//        } else
//            return false;
//    }

    public void remove(T node) {
        for (int i = 0; i < _replication; i++)
            _map.remove(MurmurHash.hash32(node.toString() + i));
    }

    public T get(int key) {
        Integer res = _map.ceilingKey(key);
        if (res != null) {
            T n = _map.get(res);
            //System.out.println(n);
            return n;
        } else {
            return _map.firstEntry().getValue();
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (int key : _map.descendingKeySet()) {
            res += "\tkey: " + key + " value: " + _map.get(key) + "\n\t";
        }
        return res;
    }

    public int size() {
        return _map.size();
    }
}
