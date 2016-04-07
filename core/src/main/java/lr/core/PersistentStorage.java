package lr.core;

import org.mapdb.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by luca on 05/03/16.
 */
public class PersistentStorage {
    private BTreeMap<Long, Data<?>> _map;
    private DB _db;
    private Helper.IHash _hash;

    public PersistentStorage(String fileName, Helper.IHash hash, boolean clear) {
        this._hash = hash;

        String dir = System.getProperty("java.io.tmpdir");
        File f = new File(dir + '/' + fileName + ".data");

        if (clear) {
            try {
                Files.deleteIfExists(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        _db = DBMaker.newFileDB(f)
                .snapshotEnable()
                .closeOnJvmShutdown()
                .make();
        _map = _db.getTreeMap("map");
//        _map = _db.getHashMap("storage");
        _db.commit();
    }

    public PersistentStorage(String fileName, boolean clear) {
        this(fileName, Helper::MD5ToLong, clear);
    }

    public PersistentStorage(String fileName) {
        this(fileName, Helper::MD5ToLong, false);
    }

    synchronized public void close() {
        if (!_db.isClosed()) _db.close();
    }

    synchronized public boolean add(Data<?> data) {
        long hash = _hash.hash(data.getKey());
        if (!_map.containsKey(hash)) {
            _map.put(hash, data);
            _db.commit();
            return true;
        }
        return false;
    }

    synchronized public Optional<Data<?>> get(String key) {
        Data<?> d = _map.get(_hash.hash(key));
        return d != null ? Optional.of(d) : Optional.empty();
    }

    synchronized public boolean update(Data<?> data) {
        long hash = _hash.hash(data.getKey());
        if (_map.containsKey(hash)) {
            _map.put(hash, data);
            _db.commit();
            return true;
        } else
            return false;
    }

    synchronized public boolean remove(String key) {
        long hash = _hash.hash(key);
        if (_map.containsKey(hash)) {
            _map.remove(hash);
            _db.commit();
            return true;
        } else return false;
    }

    synchronized public Set<Data<?>> getHead(String key) {
        long hash = _hash.hash(key);
        ConcurrentNavigableMap<Long, Data<?>> before = _map.headMap(hash, true);
        return new HashSet<>(before.values());
    }

    synchronized public Set<Data<?>> getTail(String key) {
        long hash = _hash.hash(key);
        ConcurrentNavigableMap<Long, Data<?>> after = _map.tailMap(hash, false);
        return new HashSet<>(after.values());
    }

    synchronized public Set<Data<?>> getInterval(Long hash1, Long hash2) {
        Set<Data<?>> res = new HashSet<>();

        if (hash1 < hash2) {
            res.addAll(_map.subMap(hash1, false, hash2, true).values());
        } else {
            res.addAll(_map.tailMap(hash1, false).values());
            res.addAll(_map.headMap(hash2, true).values());
        }

        return res;
    }
//
//    synchronized public Set<Data<?>> getInterval(TreeSet<Long> hash1, TreeSet<Long> hash2) {
//        Set<Data<?>> res = new HashSet<>();
//
//        Iterator<Long> navHash1 = hash1.descendingIterator();
//        Iterator<Long> navHash2 = hash2.descendingIterator();
//        long h1 = navHash1.next();
//        long h2 = navHash2.next();
//        while (navHash2.hasNext()) {
//            if (!navHash1.hasNext()) {
//                res.addAll(getInterval(hash1.last(), h2));
//                break;
//            }
//            if (h2 > h1) {
//                //get interval
//                res.addAll(getInterval(h1, h2));
//                h1 = navHash1.next();
//                h2 = navHash2.next();
//            } else if (h2 <= h1) {
//                h1 = navHash1.next();
//            }
//        }
////        if (hash1 < hash2) {
////            res.addAll(_map.subMap(hash1, false, hash2, true).values());
////        } else {
////            res.addAll(_map.tailMap(hash1, false).values());
////            res.addAll(_map.headMap(hash2, true).values());
////        }
//
//        return res;
//    }

    synchronized public Map<String, Data<?>> getMap() {
        Map<String,Data<?>> res = new HashMap<>();
        for (Map.Entry<Long,Data<?>> item: _map.snapshot().entrySet()){
            res.put(item.getValue().getKey(), item.getValue());
        }
        return res;
    }
}
