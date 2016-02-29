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

    public ConsistentHash() {
        _map = new TreeMap<>();
    }

    public ConsistentHash(Collection<T> list) {
        this();

        for ( T item : list){
            add(item);
        }
    }


    public boolean add(T node) {
        if (!_map.containsKey(MurmurHash.hash32(node.toString()))) {
            _map.put(MurmurHash.hash32(node.toString()), node);
            return true;
        } else
            return false;
    }

//    public boolean add(String key, T node) {
//        if (!_map.containsKey(MurmurHash.hash32(key))) {
//            _map.put(MurmurHash.hash32(key), node);
//            return true;
//        } else
//            return false;
//    }

    public void remove(T node) {
        _map.remove(MurmurHash.hash32(node.toString()));
    }

    public T get(int key) {
        Integer res = _map.ceilingKey(key);
        if (res != null) {
            return _map.get(key);
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
