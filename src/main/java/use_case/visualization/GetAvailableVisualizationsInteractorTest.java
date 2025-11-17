package use_case.visualization;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GetAvailableVisualizationsInteractor.
 */
class GetAvailableVisualizationsInteractorTest {

    /**
     * A simple fake implementation of DataSetVisualizationGateway
     * that always returns the same test DataSet for any id.
     */
    private static class FakeDataSetVisualizationGateway implements DataSetVisualizationGateway {

        private final DataSet dataSet;

        FakeDataSetVisualizationGateway(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public DataSet getDataSetById(String dataSetId) {
            // For testing, we ignore the id and always return the same dataset.
            return dataSet;
        }
    }

    /**
     * Helper: builds a DataSet with:
     *  - Column 0: numeric (e.g. "1.0", "2.5", "3.0")
     *  - Column 1: categorical (e.g. "A", "B", "A")
     *  - Column 2: numeric (e.g. "10", "20", "15")
     *
     * DataRow objects are created for completeness, although the interactor
     * only needs the Column objects for type inference.
     */
    private DataSet buildTestDataSet() {
        // Column 0: numeric
        List<String> col0Cells = Arrays.asList("1.0", "2.5", "3.0");
        Column col0 = new Column(col0Cells);

        // Column 1: categorical
        List<String> col1Cells = Arrays.asList("A", "B", "A");
        Column col1 = new Column(col1Cells);

        // Column 2: numeric
        List<String> col2Cells = Arrays.asList("10", "20", "15");
        Column col2 = new Column(col2Cells);

        List<Column> columns = Arrays.asList(col0, col1, col2);

        // Build DataRow list too (even if not used by the interactor)
        List<DataRow> rows = new ArrayList<>();
        for (int i = 0; i < col0Cells.size(); i++) {
            List<String> rowCells = Arrays.asList(
                    col0Cells.get(i),
                    col1Cells.get(i),
                    col2Cells.get(i)
            );
            rows.add(new DataRow(rowCells));
        }

        return new DataSet(rows, columns);
    }

    @Test
    void oneNumericColumn_allowsHistogram() {
        DataSet dataSet = buildTestDataSet();
        FakeDataSetVisualizationGateway gateway = new FakeDataSetVisualizationGateway(dataSet);

        GetAvailableVisualizationsInteractor interactor =
                new GetAvailableVisualizationsInteractor(gateway);

        // Select only column 0 (numeric)
        List<Integer> selectedIndices = Collections.singletonList(0);
        GetAvailableVisualizationsRequestModel request =
                new GetAvailableVisualizationsRequestModel("any-id", selectedIndices);

        GetAvailableVisualizationsResponseModel response =
                interactor.getAvailableVisualizations(request);

        List<VisualizationType> allowed = response.getAllowedVisualizations();

        assertEquals(1, allowed.size());
        assertTrue(allowed.contains(VisualizationType.HISTOGRAM));
    }

    @Test
    void twoNumericColumns_allowScatter() {
        DataSet dataSet = buildTestDataSet();
        FakeDataSetVisualizationGateway gateway = new FakeDataSetVisualizationGateway(dataSet);

        GetAvailableVisualizationsInteractor interactor =
                new GetAvailableVisualizationsInteractor(gateway);

        // Select columns 0 and 2 (both numeric)
        List<Integer> selectedIndices = Arrays.asList(0, 2);
        GetAvailableVisualizationsRequestModel request =
                new GetAvailableVisualizationsRequestModel("any-id", selectedIndices);

        GetAvailableVisualizationsResponseModel response =
                interactor.getAvailableVisualizations(request);

        List<VisualizationType> allowed = response.getAllowedVisualizations();

        assertEquals(1, allowed.size());
        assertTrue(allowed.contains(VisualizationType.SCATTER));
    }

    @Test
    void oneNumericOneCategorical_allowsBarChart() {
        DataSet dataSet = buildTestDataSet();
        FakeDataSetVisualizationGateway gateway = new FakeDataSetVisualizationGateway(dataSet);

        GetAvailableVisualizationsInteractor interactor =
                new GetAvailableVisualizationsInteractor(gateway);

        // Select columns 0 (numeric) and 1 (categorical)
        List<Integer> selectedIndices = Arrays.asList(0, 1);
        GetAvailableVisualizationsRequestModel request =
                new GetAvailableVisualizationsRequestModel("any-id", selectedIndices);

        GetAvailableVisualizationsResponseModel response =
                interactor.getAvailableVisualizations(request);

        List<VisualizationType> allowed = response.getAllowedVisualizations();

        assertEquals(1, allowed.size());
        assertTrue(allowed.contains(VisualizationType.BAR));
    }

    @Test
    void oneCategoricalColumn_allowsNoVisualizations() {
        DataSet dataSet = buildTestDataSet();
        FakeDataSetVisualizationGateway gateway = new FakeDataSetVisualizationGateway(dataSet);

        GetAvailableVisualizationsInteractor interactor =
                new GetAvailableVisualizationsInteractor(gateway);

        // Select only column 1 (categorical)
        List<Integer> selectedIndices = Collections.singletonList(1);
        GetAvailableVisualizationsRequestModel request =
                new GetAvailableVisualizationsRequestModel("any-id", selectedIndices);

        GetAvailableVisualizationsResponseModel response =
                interactor.getAvailableVisualizations(request);

        List<VisualizationType> allowed = response.getAllowedVisualizations();

        assertTrue(allowed.isEmpty());
    }
}
