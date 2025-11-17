package use_case.dataset;

import entity.DataSet

public interface DataSetGateway {
    void save(String id, DataSet dataSet);
    DataSet load(String id);
    boolean exists(String id);
}
