package use_case.dataset;

import entity.DataSet;

public interface CurrentTableGateway {
    void save(DataSet table);
    DataSet load();
}
