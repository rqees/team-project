package data_access;

import entity.DataSet;
import use_case.dataset.CurrentTableGateway;

public class InMemoryTableGateway implements CurrentTableGateway {
    private DataSet table;

    @Override
    public void save(DataSet table) {
        this.table = table;
    }

    @Override
    public DataSet load() {
        return table;
    }
}
