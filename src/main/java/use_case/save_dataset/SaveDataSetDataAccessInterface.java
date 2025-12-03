package use_case.save_dataset;

import entity.DataSet;

/**
 * Data access boundary for persisting datasets.
 */
public interface SaveDataSetDataAccessInterface {
    /**
     * Persists the provided dataset with the given identifier.
     *
     * @param id      target identifier or path for the dataset
     * @param dataSet dataset contents to persist
     */
    void save(final String id, final DataSet dataSet) throws java.io.IOException;
}
