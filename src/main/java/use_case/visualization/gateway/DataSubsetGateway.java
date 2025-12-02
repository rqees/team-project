package use_case.visualization.gateway;


import entity.DataSubsetSpec;
import use_case.visualization.data.DataSubsetData;

/**
 * Gateway for loading the actual numeric data for a given DataSubsetSpec.
 * Implementations live in the data_access layer.
 */
public interface DataSubsetGateway {

    /**
     * Load numeric columns for the given subset specification.
     *
     * @param spec subset (dataset id, column names, row indices)
     * @return numeric data for those columns/rows
     */
    DataSubsetData loadSubset(DataSubsetSpec spec);
}
