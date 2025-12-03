package data_access;

import entity.DataSet;
import use_case.dataset.CurrentTableGateway;

public class InMemoryTableGateway implements CurrentTableGateway {
    private DataSet table;

    @Override
    public void save(DataSet table_data) {
        this.table = table_data;
    }

    @Override
    public DataSet load() {
        return table;
    }
}
