// src/main/java/data_access/InMemoryDataSubsetGateway.java
package data_access;

import entity.DataRow;
import entity.DataSet;
import entity.DataSubsetSpec;
import use_case.dataset.CurrentTableGateway;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.gateway.DataSubsetGateway;

import java.util. *;

/**
 * DataSubsetGateway implementation for the single "current" DataSet in memory.
 */
        public class InMemoryDataSubsetGateway implements DataSubsetGateway {

            private final CurrentTableGateway currentTableGateway;

            public InMemoryDataSubsetGateway(CurrentTableGateway currentTableGateway) {
                this.currentTableGateway = currentTableGateway;
            }

            @Override
            public DataSubsetData loadSubset(DataSubsetSpec spec) {
                // There is only ONE DataSet at a time: get it from the table gateway
                DataSet dataSet = currentTableGateway.load();
                if (dataSet == null) {
                    throw new IllegalStateException("No current DataSet is loaded.");
                }

                Map<String, List<Double>> numericColumns = new HashMap<>();

                for (String colName : spec.getColumnNames()) {
                    List<Double> colValues = new ArrayList<>();
                    for (Integer rowIndex : spec.getRowIndices()) {
                        DataRow row = dataSet.getRows().get(rowIndex);
                        int colIndex = -1;
                        for (int i = 0; i < dataSet.getColumns().size(); i++) {
                            if (dataSet.getColumns().get(i).getHeader().equals(colName)) {
                                colIndex = i;
                                break;
                            }
                        }
                        double value = Double.parseDouble(row.getCells().get(colIndex));
                        colValues.add(value);
                    }
                    numericColumns.put(colName, colValues);
                }

                return new DataSubsetData(numericColumns);
            }
        }