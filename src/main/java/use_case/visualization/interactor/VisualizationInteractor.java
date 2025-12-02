// src/main/java/use_case/visualization/interactor/VisualizationInteractor.java
package use_case.visualization.interactor;

import entity.*;

import use_case.visualization.data.DataSubsetData;
import use_case.visualization.gateway.*;
import use_case.visualization.io.*;
import use_case.visualization.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class VisualizationInteractor implements VisualizationInputBoundary {

    private final DataSubsetGateway dataSubsetGateway;
    private final SummaryReportGateway summaryReportGateway;
    private final VisualizationOutputBoundary presenter;
    private final PlotKindModelFactory modelFactory;

    public VisualizationInteractor(DataSubsetGateway dataSubsetGateway,
                                   SummaryReportGateway summaryReportGateway,
                                   VisualizationOutputBoundary presenter,
                                   PlotKindModelFactory modelFactory) {
        this.dataSubsetGateway = dataSubsetGateway;
        this.summaryReportGateway = summaryReportGateway;
        this.presenter = presenter;
        this.modelFactory = modelFactory;
    }

    @Override
    public void visualize(VisualizationInputData inputData) {
        // Validate input data
        if (inputData == null) {
            throw new IllegalArgumentException("VisualizationInputData cannot be null");
        }

        DataSubsetSpec subsetSpec = inputData.getSubsetSpec();
        
        // Validate subsetSpec
        if (subsetSpec == null) {
            throw new IllegalArgumentException("DataSubsetSpec cannot be null");
        }
        
        if (subsetSpec.getColumnNames() == null) {
            throw new IllegalArgumentException("Column names in DataSubsetSpec cannot be null");
        }
        
        if (subsetSpec.getColumnNames().isEmpty()) {
            throw new IllegalArgumentException("Column names in DataSubsetSpec cannot be empty");
        }
        
        if (subsetSpec.getRowIndices() == null) {
            throw new IllegalArgumentException("Row indices in DataSubsetSpec cannot be null");
        }
        
        if (subsetSpec.getRowIndices().isEmpty()) {
            throw new IllegalArgumentException("Row indices in DataSubsetSpec cannot be empty");
        }

        // Load subset data
        DataSubsetData subsetData = dataSubsetGateway.loadSubset(subsetSpec);
        
        // Validate subsetData
        if (subsetData == null) {
            throw new IllegalStateException("Failed to load subset data: DataSubsetData is null");
        }

        // Filter out null values from the data
        DataSubsetData filteredData = filterNullValues(subsetData);

        // Get summary report (may be null if no report ID provided)
        SummaryReport summaryReport = null;
        if (inputData.getSummaryReportId() >= 0) {
            summaryReport = summaryReportGateway.getById(inputData.getSummaryReportId());
        }

        // Explicit Factory usage:
        VisualizationModel model =
                modelFactory.create(inputData, filteredData, summaryReport);

        VisualizationOutputData outputData = new VisualizationOutputData(model);
        presenter.present(outputData);
    }

    /**
     * Filters out null values from DataSubsetData.
     * For numeric columns, removes null Double values.
     * For categorical columns, removes null String values.
     * Ensures all columns have the same length after filtering by keeping only rows
     * where all selected columns have non-null values.
     * 
     * @param data the original DataSubsetData
     * @return a new DataSubsetData with null values filtered out
     */
    private DataSubsetData filterNullValues(DataSubsetData data) {
        Map<String, List<Double>> numericColumns = data.getNumericColumns();
        Map<String, List<String>> categoricalColumns = data.getCategoricalColumns();
        
        // Collect all column names and their value lists
        Map<String, List<?>> allColumns = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : numericColumns.entrySet()) {
            allColumns.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, List<String>> entry : categoricalColumns.entrySet()) {
            allColumns.put(entry.getKey(), entry.getValue());
        }
        
        if (allColumns.isEmpty()) {
            return data; // No data to filter
        }
        
        // Find the maximum length to determine number of rows
        int maxLength = allColumns.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);
        
        if (maxLength == 0) {
            return data; // No rows to process
        }
        
        // Find valid row indices (where all columns have non-null values)
        List<Integer> validIndices = new ArrayList<>();
        for (int i = 0; i < maxLength; i++) {
            boolean isValid = true;
            for (List<?> columnValues : allColumns.values()) {
                if (i >= columnValues.size() || columnValues.get(i) == null) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                validIndices.add(i);
            }
        }
        
        // Create filtered numeric columns
        Map<String, List<Double>> filteredNumericColumns = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : numericColumns.entrySet()) {
            List<Double> filteredValues = validIndices.stream()
                    .map(entry.getValue()::get)
                    .collect(Collectors.toList());
            filteredNumericColumns.put(entry.getKey(), filteredValues);
        }
        
        // Create filtered categorical columns
        Map<String, List<String>> filteredCategoricalColumns = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : categoricalColumns.entrySet()) {
            List<String> filteredValues = validIndices.stream()
                    .map(entry.getValue()::get)
                    .collect(Collectors.toList());
            filteredCategoricalColumns.put(entry.getKey(), filteredValues);
        }
        
        return new DataSubsetData(filteredNumericColumns, filteredCategoricalColumns);
    }
}