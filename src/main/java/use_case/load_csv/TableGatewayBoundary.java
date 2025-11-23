package use_case.load_csv;

import entity.DataSet;

public interface TableGatewayBoundary {
    void save(DataSet table);
    DataSet load();
}
