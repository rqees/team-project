package use_case.visualization.interactor;

import entity.DataSubsetSpec;
import entity.SummaryReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;
import use_case.visualization.io.VisualizationInputData;
import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.io.VisualizationOutputData;
import use_case.visualization.model.PlotKind;
import use_case.visualization.model.PlotKindModelFactory;
import use_case.visualization.model.VisualizationModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for VisualizationInteractor.
 * Achieves 100% line coverage for VisualizationInteractor.
 */
class VisualizationInteractorTest {
    private FakeDataSubsetGateway fakeDataSubsetGateway;
    private FakeSummaryReportGateway fakeSummaryReportGateway;
    private FakePresenter fakePresenter;
    private FakeModelFactory fakeModelFactory;
    private VisualizationInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeDataSubsetGateway = new FakeDataSubsetGateway();
        fakeSummaryReportGateway = new FakeSummaryReportGateway();
        fakePresenter = new FakePresenter();
        fakeModelFactory = new FakeModelFactory();
        interactor = new VisualizationInteractor(
                fakeDataSubsetGateway,
                fakeSummaryReportGateway,
                fakePresenter,
                fakeModelFactory
        );
    }

    // ===== Validation Tests =====

    @Test
    void testVisualize_NullInputData_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(null)
        );
        assertEquals("VisualizationInputData cannot be null", exception.getMessage());
    }

    @Test
    void testVisualize_NullSubsetSpec_ThrowsException() {
        // Arrange
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, null, List.of("x"), List.of("y"), "title"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("DataSubsetSpec cannot be null", exception.getMessage());
    }

    @Test
    void testVisualize_NullColumnNames_ThrowsException() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", null, List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("Column names in DataSubsetSpec cannot be null", exception.getMessage());
    }

    @Test
    void testVisualize_EmptyColumnNames_ThrowsException() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", new ArrayList<>(), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("Column names in DataSubsetSpec cannot be empty", exception.getMessage());
    }

    @Test
    void testVisualize_NullRowIndices_ThrowsException() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), null);
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("Row indices in DataSubsetSpec cannot be null", exception.getMessage());
    }

    @Test
    void testVisualize_EmptyRowIndices_ThrowsException() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), new ArrayList<>());
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("Row indices in DataSubsetSpec cannot be empty", exception.getMessage());
    }

    @Test
    void testVisualize_NullSubsetData_ThrowsException() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        fakeDataSubsetGateway.subsetData = null;

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> interactor.visualize(inputData)
        );
        assertEquals("Failed to load subset data: DataSubsetData is null", exception.getMessage());
    }

    // ===== Successful Visualization Tests =====

    @Test
    void testVisualize_ValidInputWithoutSummaryReport_Success() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        assertNotNull(fakePresenter.outputData);
        assertEquals(model, fakePresenter.outputData.getVisualizationModel());
        assertFalse(fakeSummaryReportGateway.getByIdCalled);
        assertEquals(inputData, fakeModelFactory.receivedInputData);
    }

    @Test
    void testVisualize_ValidInputWithSummaryReport_Success() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                5, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        SummaryReport report = new SummaryReport(5, "Test Report", subsetSpec, new ArrayList<>());
        fakeSummaryReportGateway.report = report;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        assertTrue(fakeSummaryReportGateway.getByIdCalled);
        assertEquals(5, fakeSummaryReportGateway.requestedId);
        assertEquals(report, fakeModelFactory.receivedReport);
    }

    @Test
    void testVisualize_SummaryReportIdZero_LoadsReport() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                0, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        SummaryReport report = new SummaryReport(0, "Report", subsetSpec, new ArrayList<>());
        fakeSummaryReportGateway.report = report;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakeSummaryReportGateway.getByIdCalled);
        assertEquals(0, fakeSummaryReportGateway.requestedId);
    }

    // ===== Filter Null Values Tests =====

    @Test
    void testFilterNullValues_EmptyColumns_ReturnsOriginalData() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        DataSubsetData subsetData = new DataSubsetData(new LinkedHashMap<>());
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify that the same empty data was passed to factory
        assertTrue(fakeModelFactory.receivedData.getNumericColumns().isEmpty());
        assertTrue(fakeModelFactory.receivedData.getCategoricalColumns().isEmpty());
    }

    @Test
    void testFilterNullValues_ZeroLengthColumns_ReturnsOriginalData() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1"), List.of(0));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", new ArrayList<>());
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify that empty list was passed
        assertTrue(fakeModelFactory.receivedData.getNumericColumns().get("col1").isEmpty());
    }

    @Test
    void testFilterNullValues_NoNulls_ReturnsFilteredData() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "col2"), List.of(0, 1));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        numericColumns.put("col2", Arrays.asList(4.0, 5.0, 6.0));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify all rows are kept (no nulls to filter)
        assertEquals(3, fakeModelFactory.receivedData.getNumericColumns().get("col1").size());
        assertEquals(3, fakeModelFactory.receivedData.getNumericColumns().get("col2").size());
        assertEquals(1.0, fakeModelFactory.receivedData.getNumericColumns().get("col1").get(0));
        assertEquals(3.0, fakeModelFactory.receivedData.getNumericColumns().get("col1").get(2));
    }

    @Test
    void testFilterNullValues_WithNullsInNumericColumns_FiltersNulls() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "col2"), List.of(0, 1, 2));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, null, 3.0));
        numericColumns.put("col2", Arrays.asList(4.0, 5.0, 6.0));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify row 1 (index 1) is filtered out because col1 has null
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<Double> filteredCol2 = fakeModelFactory.receivedData.getNumericColumns().get("col2");
        assertEquals(2, filteredCol1.size());
        assertEquals(2, filteredCol2.size());
        assertEquals(1.0, filteredCol1.get(0));
        assertEquals(3.0, filteredCol1.get(1));
        assertEquals(4.0, filteredCol2.get(0));
        assertEquals(6.0, filteredCol2.get(1));
    }

    @Test
    void testFilterNullValues_WithNullsInCategoricalColumns_FiltersNulls() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "cat1"), List.of(0, 1, 2));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        Map<String, List<String>> categoricalColumns = new LinkedHashMap<>();
        categoricalColumns.put("cat1", Arrays.asList("A", null, "C"));
        DataSubsetData subsetData = new DataSubsetData(numericColumns, categoricalColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify row 1 (index 1) is filtered out because cat1 has null
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<String> filteredCat1 = fakeModelFactory.receivedData.getCategoricalColumns().get("cat1");
        assertEquals(2, filteredCol1.size());
        assertEquals(2, filteredCat1.size());
        assertEquals(1.0, filteredCol1.get(0));
        assertEquals(3.0, filteredCol1.get(1));
        assertEquals("A", filteredCat1.get(0));
        assertEquals("C", filteredCat1.get(1));
    }

    @Test
    void testFilterNullValues_WithNullsInBothColumns_FiltersNulls() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "cat1"), List.of(0, 1, 2, 3));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, null, 3.0, 4.0));
        Map<String, List<String>> categoricalColumns = new LinkedHashMap<>();
        categoricalColumns.put("cat1", Arrays.asList("A", "B", null, "D"));
        DataSubsetData subsetData = new DataSubsetData(numericColumns, categoricalColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify only rows 0 and 3 are kept (rows 1 and 2 have nulls)
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<String> filteredCat1 = fakeModelFactory.receivedData.getCategoricalColumns().get("cat1");
        assertEquals(2, filteredCol1.size());
        assertEquals(2, filteredCat1.size());
        assertEquals(1.0, filteredCol1.get(0));
        assertEquals(4.0, filteredCol1.get(1));
        assertEquals("A", filteredCat1.get(0));
        assertEquals("D", filteredCat1.get(1));
    }

    @Test
    void testFilterNullValues_MismatchedColumnLengths_FiltersCorrectly() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "col2"), List.of(0, 1, 2));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        numericColumns.put("col2", Arrays.asList(4.0, 5.0)); // Shorter column
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify only rows 0 and 1 are kept (row 2 is out of bounds for col2)
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<Double> filteredCol2 = fakeModelFactory.receivedData.getNumericColumns().get("col2");
        assertEquals(2, filteredCol1.size());
        assertEquals(2, filteredCol2.size());
        assertEquals(1.0, filteredCol1.get(0));
        assertEquals(2.0, filteredCol1.get(1));
        assertEquals(4.0, filteredCol2.get(0));
        assertEquals(5.0, filteredCol2.get(1));
    }

    @Test
    void testFilterNullValues_AllRowsHaveNulls_ReturnsEmptyData() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "col2"), List.of(0, 1));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(null, 2.0));
        numericColumns.put("col2", Arrays.asList(4.0, null));
        DataSubsetData subsetData = new DataSubsetData(numericColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify all rows are filtered out (each row has at least one null)
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<Double> filteredCol2 = fakeModelFactory.receivedData.getNumericColumns().get("col2");
        assertEquals(0, filteredCol1.size());
        assertEquals(0, filteredCol2.size());
    }

    @Test
    void testFilterNullValues_MixedNumericAndCategorical_NoNulls() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "cat1"), List.of(0, 1, 2));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, 3.0));
        Map<String, List<String>> categoricalColumns = new LinkedHashMap<>();
        categoricalColumns.put("cat1", Arrays.asList("A", "B", "C"));
        DataSubsetData subsetData = new DataSubsetData(numericColumns, categoricalColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify all rows are kept
        assertEquals(3, fakeModelFactory.receivedData.getNumericColumns().get("col1").size());
        assertEquals(3, fakeModelFactory.receivedData.getCategoricalColumns().get("cat1").size());
    }

    @Test
    void testFilterNullValues_MixedNumericAndCategorical_WithNulls() {
        // Arrange
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test", List.of("col1", "cat1"), List.of(0, 1, 2, 3));
        VisualizationInputData inputData = new VisualizationInputData(
                -1, PlotKind.SCATTER, subsetSpec, List.of("x"), List.of("y"), "title"
        );
        
        Map<String, List<Double>> numericColumns = new LinkedHashMap<>();
        numericColumns.put("col1", Arrays.asList(1.0, 2.0, null, 4.0));
        Map<String, List<String>> categoricalColumns = new LinkedHashMap<>();
        categoricalColumns.put("cat1", Arrays.asList("A", null, "C", "D"));
        DataSubsetData subsetData = new DataSubsetData(numericColumns, categoricalColumns);
        fakeDataSubsetGateway.subsetData = subsetData;
        
        FakeVisualizationModel model = new FakeVisualizationModel();
        fakeModelFactory.model = model;

        // Act
        interactor.visualize(inputData);

        // Assert
        assertTrue(fakePresenter.presentCalled);
        // Verify only rows 0 and 3 are kept (rows 1 and 2 have nulls)
        List<Double> filteredCol1 = fakeModelFactory.receivedData.getNumericColumns().get("col1");
        List<String> filteredCat1 = fakeModelFactory.receivedData.getCategoricalColumns().get("cat1");
        assertEquals(2, filteredCol1.size());
        assertEquals(2, filteredCat1.size());
        assertEquals(1.0, filteredCol1.get(0));
        assertEquals(4.0, filteredCol1.get(1));
        assertEquals("A", filteredCat1.get(0));
        assertEquals("D", filteredCat1.get(1));
    }

    // ===== FAKE IMPLEMENTATIONS =====

    private static class FakeDataSubsetGateway implements DataSubsetGateway {
        DataSubsetData subsetData;

        @Override
        public DataSubsetData loadSubset(DataSubsetSpec spec) {
            return subsetData;
        }
    }

    private static class FakeSummaryReportGateway implements SummaryReportGateway {
        SummaryReport report;
        boolean getByIdCalled = false;
        int requestedId;

        @Override
        public SummaryReport getById(int summaryId) {
            getByIdCalled = true;
            requestedId = summaryId;
            return report;
        }

        @Override
        public void save(SummaryReport report) {
            // Not used in tests
        }
    }

    private static class FakePresenter implements VisualizationOutputBoundary {
        boolean presentCalled = false;
        VisualizationOutputData outputData;

        @Override
        public void present(VisualizationOutputData outputData) {
            presentCalled = true;
            this.outputData = outputData;
        }
    }

    private static class FakeModelFactory extends PlotKindModelFactory {
        VisualizationModel model;
        VisualizationInputData receivedInputData;
        DataSubsetData receivedData;
        SummaryReport receivedReport;

        @Override
        public VisualizationModel create(VisualizationInputData input,
                                        DataSubsetData subsetData,
                                        SummaryReport report) {
            receivedInputData = input;
            receivedData = subsetData;
            receivedReport = report;
            return model;
        }
    }

    private static class FakeVisualizationModel implements VisualizationModel {
        @Override
        public String getTitle() {
            return "Test Title";
        }

        @Override
        public PlotKind getPlotKind() {
            return PlotKind.SCATTER;
        }
    }
}

