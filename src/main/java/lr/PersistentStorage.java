package lr;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Created by luca on 05/03/16.
 */
public class PersistentStorage {

    private HTreeMap<String, Data<?>> _map;
    private DB _db;

    public PersistentStorage(Node n) {
        _db = DBMaker.newFileDB(new File("src/main/resources/storage/" + n.getId() + ".data"))
                .snapshotEnable()
                .closeOnJvmShutdown()
                .make();

        _map = _db.getHashMap("storage");
        _db.commit();
    }

    synchronized public void close() {
        _db.close();
    }

    synchronized public boolean add(Data<?> data) {
        if (!_map.containsKey(data.getKey())) {
            _map.put(data.getKey(), data);
            _db.commit();
            return true;
        }
        return false;
    }

    synchronized public Optional<Data<?>> get(String key) {
        Data<?> d = _map.get(key);
        return d != null ? Optional.of(d) : Optional.empty();
    }

    synchronized public boolean update(Data<?> data) {
        if (_map.containsKey(data.getKey())) {
            _map.put(data.getKey(), data);
            _db.commit();
            return true;
        } else
            return false;
    }

    synchronized public boolean remove(String key) {
        if (_map.containsKey(key)) {
            _map.remove(key);
            _db.commit();
            return true;
        } else return false;
    }

    synchronized public Map<String, Data<?>> getMap() {
        return _map.snapshot();
    }
}
