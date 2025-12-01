package use_case.visualization.gateway;


import entity.DataSubsetSpec;
import use_case.visualization.data.DataSubsetData;

public interface DataSubsetGateway {
    DataSubsetData loadSubset(DataSubsetSpec spec);
}