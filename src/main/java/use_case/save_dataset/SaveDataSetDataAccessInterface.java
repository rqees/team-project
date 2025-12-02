package use_case.save_dataset;

import entity.DataSet;

public interface SaveDataSetDataAccessInterface {
    void save(String id, DataSet dataSet);
}
