package lr;

import ie.ucd.murmur.MurmurHash;

import java.util.TreeMap;

/**
 * Created by luca on 24/02/16.
 */
public class ConsistentHash<T> {

    TreeMap<Integer, T> map;

    public ConsistentHash() {
        map = new TreeMap();
    }

    public void add(T node) {
        map.put(MurmurHash.hash32(node.getClass().toString()), node);
    }

    public void remove(T node) {
        map.remove(MurmurHash.hash32(node.toString()));
    }

    public T get(int key) {
        Integer res = map.ceilingKey(key);
        if (res != null) {
            return map.get(key);
        } else {
            return map.firstEntry().getValue();
        }
    }


}
