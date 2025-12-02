package use_case.dataset;

import entity.DataSet;

/**
 * Data access interface for saving and loading DataSet entities.
 * Implementations live in the data_access layer (e.g., FileDataAccessObject).
 */
public interface DataSetDataAccessInterface {

    /**
     * Save the given DataSet under the provided id.
     * @param id       identifier for this dataset (e.g., project name)
     * @param dataSet  the DataSet entity to persist
     */
    void saveDataSet(String id, DataSet dataSet);

    /**
     * Load the DataSet associated with the given id.
     * @param id identifier of the dataset
     * @return the corresponding DataSet
     * @throws RuntimeException if not found or on low-level storage error
     */
    DataSet loadDataSet(String id);

    /**
     * Check whether a dataset with this id already exists in storage.
     * @param id identifier to check
     * @return true if a dataset with this id exists, false otherwise
     */
    boolean exists(String id);
}

