package use_case.save_dataset;

import entity.DataSet;

public interface CurrentTableGateway {
    DataSet load();
}
