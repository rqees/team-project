package use_case.visualization;

import entity.Column;
import entity.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the "Get Available Visualizations" use case.
 * Uses the DataSet, DataRow, and Column entities to infer column types
 * and decide which visualizations are allowed.
 */
public class GetAvailableVisualizationsInteractor implements GetAvailableVisualizationsInputBoundary {

    private final DataSetVisualizationGateway dataSetGateway;

    /**
     * @param dataSetGateway gateway to access DataSet objects
     */
    public GetAvailableVisualizationsInteractor(DataSetVisualizationGateway dataSetGateway) {
        this.dataSetGateway = dataSetGateway;
    }

    @Override
    public GetAvailableVisualizationsResponseModel getAvailableVisualizations(
            GetAvailableVisualizationsRequestModel requestModel
    ) {
        DataSet dataSet = dataSetGateway.getDataSetById(requestModel.getDataSetId());
        List<Integer> selectedColumnIndices = requestModel.getSelectedColumnIndices();

        int numericCount = 0;
        int categoricalCount = 0;

        // Inspect each selected column and classify it as numeric or categorical
        for (int colIndex : selectedColumnIndices) {
            Column column = dataSet.getColumns().get(colIndex);

            if (isNumericColumn(column)) {
                numericCount++;
            } else {
                categoricalCount++;
            }
        }

        List<VisualizationType> allowed = new ArrayList<>();

        // Rule 1: 1 numeric → histogram
        if (selectedColumnIndices.size() == 1 && numericCount == 1) {
            allowed.add(VisualizationType.HISTOGRAM);
        }

        // Rule 2: 2 numeric → scatterplot
        if (selectedColumnIndices.size() == 2 && numericCount == 2) {
            allowed.add(VisualizationType.SCATTER);
        }

        // Rule 3: 1 categorical + 1 numeric → bar chart
        if (selectedColumnIndices.size() == 2 &&
                numericCount == 1 &&
                categoricalCount == 1) {
            allowed.add(VisualizationType.BAR);
        }

        return new GetAvailableVisualizationsResponseModel(allowed);
    }

    /**
     * A column is considered numeric if every non-empty value can be parsed as a Double.
     *
     * @param column the column to inspect
     * @return true if the column is numeric, false otherwise
     */
    private boolean isNumericColumn(Column column) {
        for (String cell : column.getCells()) {
            if (cell == null || cell.isEmpty()) {
                continue;  // ignore empty cells
            }
            try {
                Double.parseDouble(cell);
            } catch (NumberFormatException e) {
                // At least one non-numeric value → treat as categorical
                return false;
            }
        }
        return true;
    }
}
