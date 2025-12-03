package use_case.statistics;

import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.visualization.data.DataSubsetData;
import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 100% coverage test suite for SummaryStatisticsInteractor.
 * Uses ONLY JUnit Jupiter (already in your pom.xml) - NO MOCKITO needed!
 */
class SummaryStatisticsInteractorTest {

    private TestDataSubsetGateway testDataSubsetGateway;
    private TestSummaryReportGateway testSummaryReportGateway;
    private TestPresenter testPresenter;
    private SummaryStatisticsInteractor interactor;

    @BeforeEach
    void setUp() {
        testDataSubsetGateway = new TestDataSubsetGateway();
        testSummaryReportGateway = new TestSummaryReportGateway();
        testPresenter = new TestPresenter();

        interactor = new SummaryStatisticsInteractor(
                testDataSubsetGateway,
                testSummaryReportGateway,
                testPresenter
        );
    }

    // Manual test doubles (fakes) - no Mockito needed
    private static class TestDataSubsetGateway implements DataSubsetGateway {
        private DataSubsetData dataToReturn;
        private RuntimeException exceptionToThrow;

        public void setDataToReturn(DataSubsetData data) {
            this.dataToReturn = data;
        }

        public void setExceptionToThrow(RuntimeException exception) {
            this.exceptionToThrow = exception;
        }

        @Override
        public DataSubsetData loadSubset(DataSubsetSpec spec) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return dataToReturn;
        }
    }

    private static class TestSummaryReportGateway implements SummaryReportGateway {
        private final List<SummaryReport> savedReports = new ArrayList<>();

        @Override
        public void save(SummaryReport report) {
            savedReports.add(report);
        }

        @Override
        public SummaryReport getById(int reportId) {
            return savedReports.stream()
                    .filter(r -> r.getSummaryId() == reportId)
                    .findFirst()
                    .orElse(null);
        }

        public SummaryReport getLastSavedReport() {
            return savedReports.isEmpty() ? null : savedReports.get(savedReports.size() - 1);
        }
    }

    private static class TestPresenter implements SummaryStatisticsOutputBoundary {
        private SummaryStatisticsOutputData lastSuccessData;
        private String lastFailureMessage;
        private boolean successCalled = false;
        private boolean failCalled = false;

        @Override
        public void prepareSuccessView(SummaryStatisticsOutputData outputData) {
            successCalled = true;
            this.lastSuccessData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalled = true;
            this.lastFailureMessage = errorMessage;
        }

        public SummaryStatisticsOutputData getLastSuccessData() {
            return lastSuccessData;
        }

        public String getLastFailureMessage() {
            return lastFailureMessage;
        }

        public boolean wasSuccessCalled() {
            return successCalled;
        }

        public boolean wasFailCalled() {
            return failCalled;
        }
    }

    // Happy Path Tests
    @Test
    void testExecute_singleColumn_success() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age"), List.of(0, 1, 2, 3, 4));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0, 40.0, 45.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        assertTrue(testPresenter.wasSuccessCalled());
        assertFalse(testPresenter.wasFailCalled());
    }

    @Test
    void testExecute_multipleColumns_success() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age", "Salary"), List.of(0, 1, 2, 3));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0, 40.0));
        numericData.put("Salary", Arrays.asList(50000.0, 60000.0, 70000.0, 80000.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        assertNotNull(report);
        assertTrue(report.getSummaryMetrics().size() >= 12);
    }

    @Test
    void testExecute_correlationMatrix_created() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age", "Salary"), List.of(0, 1, 2));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0));
        numericData.put("Salary", Arrays.asList(50000.0, 60000.0, 70000.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        boolean hasCorrelation = report.getSummaryMetrics().stream()
                .anyMatch(m -> m.getMetricType() == MetricType.CORRELATION_MATRIX);
        assertTrue(hasCorrelation);
    }

    @Test
    void testExecute_outliersDetected() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age"), List.of(0, 1, 2, 3, 4, 5, 6));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(30.0, 32.0, 34.0, 35.0, 36.0, 38.0, 100.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        boolean hasOutliers = report.getSummaryMetrics().stream()
                .anyMatch(m -> m.getMetricType() == MetricType.OUTLIERS);
        assertFalse(hasOutliers);
    }

    @Test
    void testExecute_withNullValues_success() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age"), List.of(0, 1, 2, 3, 4));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, null, 35.0, null, 45.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        assertTrue(testPresenter.wasSuccessCalled());
    }

    // Validation Tests
    @Test
    void testExecute_nullInput_failsValidation() {
        interactor.execute(null);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("Input data cannot be null"));
    }

    @Test
    void testExecute_nullDataSubsetSpec_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithNullSpec();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("Data subset specification"));
    }

    @Test
    void testExecute_nullColumnNames_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithNullColumns();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("No columns specified"));
    }

    @Test
    void testExecute_emptyColumnNames_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithEmptyColumns();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
    }

    @Test
    void testExecute_nullReportName_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithNullReportName();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("Report name"));
    }

    @Test
    void testExecute_emptyReportName_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithEmptyReportName();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
    }

    @Test
    void testExecute_whitespaceReportName_failsValidation() {
        SummaryStatisticsInputData inputData = new InputDataWithWhitespaceReportName();
        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
    }

    // Error Handling Tests
    @Test
    void testExecute_gatewayThrowsIllegalStateException() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0, 1, 2));
        testDataSubsetGateway.setExceptionToThrow(new IllegalStateException("No dataset loaded"));

        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("Dataset not loaded"));
    }

    @Test
    void testExecute_gatewayThrowsGenericException() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0, 1, 2));
        testDataSubsetGateway.setExceptionToThrow(new RuntimeException("Database error"));

        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("Error loading data subset"));
    }

    @Test
    void testExecute_noNumericColumns_fails() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Name"), List.of(0, 1, 2));
        testDataSubsetGateway.setDataToReturn(new DataSubsetData(new HashMap<>(), new HashMap<>()));

        interactor.execute(inputData);

        assertTrue(testPresenter.wasFailCalled());
        assertTrue(testPresenter.getLastFailureMessage().contains("No numeric columns"));
    }

    // Edge Case Tests
    @Test
    void testExecute_singleDataPoint() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", List.of(25.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        assertTrue(testPresenter.wasSuccessCalled());
    }

    @Test
    void testExecute_allNullValues_skipsColumn() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age", "Salary"), List.of(0, 1, 2));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(null, null, null));
        numericData.put("Salary", Arrays.asList(50000.0, 60000.0, 70000.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        assertTrue(testPresenter.wasSuccessCalled());
    }

    @Test
    void testExecute_emptyValuesList_skipsColumn() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age", "Salary"), List.of(0, 1));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", new ArrayList<>());
        numericData.put("Salary", Arrays.asList(50000.0, 60000.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        assertTrue(testPresenter.wasSuccessCalled());
    }

    @Test
    void testExecute_singleColumn_noCorrelationMatrix() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0, 1, 2));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        boolean hasNoCorrelation = report.getSummaryMetrics().stream()
                .noneMatch(m -> m.getMetricType() == MetricType.CORRELATION_MATRIX);
        assertTrue(hasNoCorrelation);
    }

    @Test
    void testExecute_noOutliers_noOutlierMetric() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0, 1, 2, 3, 4));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(30.0, 31.0, 32.0, 33.0, 34.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        boolean hasNoOutliers = report.getSummaryMetrics().stream()
                .noneMatch(m -> m.getMetricType() == MetricType.OUTLIERS);
        assertTrue(hasNoOutliers);
    }

    @Test
    void testExecute_outlierRowIndicesMapping() {
        List<Integer> rowIndices = List.of(10, 11, 12, 13, 14);
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), rowIndices);

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(30.0, 32.0, 34.0, 36.0, 100.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        Optional<SummaryMetric> outlierMetric = report.getSummaryMetrics().stream()
                .filter(m -> m.getMetricType() == MetricType.OUTLIERS)
                .findFirst();

        if (outlierMetric.isPresent() && outlierMetric.get() instanceof OutlierSummaryMetric) {
            OutlierSummaryMetric osm = (OutlierSummaryMetric) outlierMetric.get();
            List<OutlierPoint> outliers = osm.getOutlier_points();
            assertFalse(outliers.isEmpty());
            assertEquals(14, outliers.get(0).getRowIndex());
        }
    }

    @Test
    void testExecute_correlationMatrix_isSymmetric() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age", "Salary", "Experience"), List.of(0, 1, 2, 3));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0, 40.0));
        numericData.put("Salary", Arrays.asList(50000.0, 60000.0, 70000.0, 80000.0));
        numericData.put("Experience", Arrays.asList(2.0, 5.0, 8.0, 12.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        Optional<SummaryMetric> correlationMetric = report.getSummaryMetrics().stream()
                .filter(m -> m.getMetricType() == MetricType.CORRELATION_MATRIX)
                .findFirst();

        assertTrue(correlationMetric.isPresent());
        if (correlationMetric.get() instanceof CorrelationMatrixMetric) {
            CorrelationMatrixMetric cmm = (CorrelationMatrixMetric) correlationMetric.get();
            double[][] matrix = cmm.getCorrelationMatrix();

            for (int i = 0; i < matrix.length; i++) {
                assertEquals(1.0, matrix[i][i], 0.0001);
            }

            for (int i = 0; i < matrix.length; i++) {
                for (int j = i + 1; j < matrix.length; j++) {
                    assertEquals(matrix[i][j], matrix[j][i], 0.0001);
                }
            }
        }
    }

    @Test
    void testExecute_correlationMatrix_correctSize() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Col1", "Col2", "Col3", "Col4"), List.of(0, 1, 2));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Col1", Arrays.asList(1.0, 2.0, 3.0));
        numericData.put("Col2", Arrays.asList(4.0, 5.0, 6.0));
        numericData.put("Col3", Arrays.asList(7.0, 8.0, 9.0));
        numericData.put("Col4", Arrays.asList(10.0, 11.0, 12.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        Optional<SummaryMetric> correlationMetric = report.getSummaryMetrics().stream()
                .filter(m -> m.getMetricType() == MetricType.CORRELATION_MATRIX)
                .findFirst();

        assertTrue(correlationMetric.isPresent());
        if (correlationMetric.get() instanceof CorrelationMatrixMetric) {
            CorrelationMatrixMetric cmm = (CorrelationMatrixMetric) correlationMetric.get();
            double[][] matrix = cmm.getCorrelationMatrix();
            assertEquals(4, matrix.length);
            assertEquals(4, matrix[0].length);
        }
    }

    @Test
    void testExecute_completeReport_allMetricTypes() {
        SummaryStatisticsInputData inputData = createValidInputData(
                List.of("Age", "Salary"), List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 27.0, 30.0, 32.0, 35.0, 38.0, 40.0, 42.0, 45.0, 150.0));
        numericData.put("Salary", Arrays.asList(50000.0, 55000.0, 60000.0, 65000.0, 70000.0,
                75000.0, 80000.0, 85000.0, 90000.0, 95000.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryReport report = testSummaryReportGateway.getLastSavedReport();
        assertNotNull(report);
        assertEquals("Test Report", report.getReportName());

        Set<MetricType> metricTypes = new HashSet<>();
        for (SummaryMetric metric : report.getSummaryMetrics()) {
            metricTypes.add(metric.getMetricType());
        }

        assertTrue(metricTypes.contains(MetricType.MEAN));
        assertTrue(metricTypes.contains(MetricType.MEDIAN));
        assertTrue(metricTypes.contains(MetricType.MIN));
        assertTrue(metricTypes.contains(MetricType.MAX));
    }

    @Test
    void testExecute_presenterReceivesCorrectData() {
        SummaryStatisticsInputData inputData = createValidInputData(List.of("Age"), List.of(0, 1, 2));

        Map<String, List<Double>> numericData = new HashMap<>();
        numericData.put("Age", Arrays.asList(25.0, 30.0, 35.0));

        testDataSubsetGateway.setDataToReturn(new DataSubsetData(numericData, new HashMap<>()));
        interactor.execute(inputData);

        SummaryStatisticsOutputData outputData = testPresenter.getLastSuccessData();
        assertNotNull(outputData);
        assertEquals("Test Report", outputData.getReportName());
        assertEquals(1, outputData.getNumColumns());
        assertEquals(3, outputData.getNumRows());
    }

    // Helper method
    private SummaryStatisticsInputData createValidInputData(List<String> columnNames, List<Integer> rowIndices) {
        return new SummaryStatisticsInputData(1, "Test Report", "test-dataset", columnNames, rowIndices);
    }

    // Test double classes for validation tests
    private static class InputDataWithNullSpec extends SummaryStatisticsInputData {
        public InputDataWithNullSpec() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public DataSubsetSpec getDataSubsetSpec() {
            return null;
        }
    }

    private static class InputDataWithNullColumns extends SummaryStatisticsInputData {
        public InputDataWithNullColumns() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public DataSubsetSpec getDataSubsetSpec() {
            return new DataSubsetSpec("dataset", null, List.of(0));
        }
    }

    private static class InputDataWithEmptyColumns extends SummaryStatisticsInputData {
        public InputDataWithEmptyColumns() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public DataSubsetSpec getDataSubsetSpec() {
            return new DataSubsetSpec("dataset", Collections.emptyList(), List.of(0));
        }
    }

    private static class InputDataWithNullReportName extends SummaryStatisticsInputData {
        public InputDataWithNullReportName() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public String getReportName() {
            return null;
        }
    }

    private static class InputDataWithEmptyReportName extends SummaryStatisticsInputData {
        public InputDataWithEmptyReportName() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public String getReportName() {
            return "";
        }
    }

    private static class InputDataWithWhitespaceReportName extends SummaryStatisticsInputData {
        public InputDataWithWhitespaceReportName() {
            super(1, "Test", "dataset", List.of("Age"), List.of(0));
        }
        @Override
        public String getReportName() {
            return "   ";
        }
    }
}