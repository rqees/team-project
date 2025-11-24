package data_access;

import entity.DataSet;
import use_case.dataset.DataSetDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of DataSetDataAccessInterface.
 * Stores DataSet objects in a HashMap instead of writing to disk.
 * This is useful for when we want to run the app without touching the filesystem.
 */
public class InMemoryDataAccessObject implements DataSetDataAccessInterface {
    private final Map<String, DataSet> storage = new HashMap<>();

    @Override
    public void saveDataSet(String id, DataSet dataSet) {
        storage.put(id, dataSet);
    }

    @Override
    public DataSet loadDataSet(String id) {
        if (!exists(id)) {
            throw new RuntimeException("No DataSet stored with id " + id);
        }
        return storage.get(id);
    }

    @Override
    public boolean exists(String id) {
        return storage.containsKey(id);
    }
}
