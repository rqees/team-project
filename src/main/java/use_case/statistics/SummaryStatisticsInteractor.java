// package use_case.statistics;

// import entity.*;

// import java.util.ArrayList;
// import java.util.List;

// /**
//  * The Summary Statistics Interactor - REFACTORED VERSION
//  *
//  * RESPONSIBILITY: Orchestrate the use case flow only.
//  * All calculations are delegated to StatisticsCalculator.
//  */
// public class SummaryStatisticsInteractor implements SummaryStatisticsInputBoundary {

//     private final SummaryStatisticsDataAccessInterface summaryStatisticsDataAccessObject;
//     private final SummaryStatisticsOutputBoundary summaryStatisticsPresenter;
//     private static final double OUTLIER_Z_SCORE_THRESHOLD = 3.0;

//     public SummaryStatisticsInteractor(SummaryStatisticsDataAccessInterface dataAccess,
//                                        SummaryStatisticsOutputBoundary presenter) {
//         this.summaryStatisticsDataAccessObject = dataAccess;
//         this.summaryStatisticsPresenter = presenter;
//     }

//     @Override
//     public void execute(SummaryStatisticsInputData input) {
//         try {
//             // Step 1: Validate input
//             validateInput(input);

//             final DataSubsetSpec subset = input.getDataSubsetSpec();

//             // Step 2: Validate subset exists and is accessible
//             if (!summaryStatisticsDataAccessObject.validateDataSubset(subset)) {
//                 summaryStatisticsPresenter.prepareFailView("Invalid or inaccessible data subset");
//                 return;
//             }

//             // Step 3: Verify dataset exists
//             final DataSubsetSpec validatedSubset = summaryStatisticsDataAccessObject.getDataSubsetById(subset.getDatasetId());
//             if (validatedSubset == null) {
//                 summaryStatisticsPresenter.prepareFailView("Dataset not found: " + subset.getDatasetId());
//                 return;
//             }

//             // Step 4: Calculate all summary metrics
//             List<SummaryMetric> metrics = calculateAllMetrics(subset);

//             // Step 5: Create SummaryReport entity
//             SummaryReport report = new SummaryReport(
//                     input.getDataSubsetId(),
//                     input.getReportName(),
//                     subset,
//                     metrics
//             );

//             // Step 6: Create and send output data
//             SummaryStatisticsOutputData outputData = new SummaryStatisticsOutputData(report);
//             summaryStatisticsPresenter.prepareSuccessView(outputData);

//         } catch (IllegalArgumentException e) {
//             summaryStatisticsPresenter.prepareFailView("Invalid input: " + e.getMessage());
//         } catch (Exception e) {
//             summaryStatisticsPresenter.prepareFailView("Error computing statistics: " + e.getMessage());
//         }
//     }

//     // ========================================
//     // VALIDATION (Application Logic)
//     // ========================================

//     private void validateInput(SummaryStatisticsInputData input) {
//         if (input == null) {
//             throw new IllegalArgumentException("Input data cannot be null");
//         }

//         if (input.getDataSubsetSpec() == null) {
//             throw new IllegalArgumentException("Data subset specification is required");
//         }

//         if (input.getDataSubsetSpec().getColumnNames() == null ||
//                 input.getDataSubsetSpec().getColumnNames().isEmpty()) {
//             throw new IllegalArgumentException("No columns specified for analysis");
//         }

//         if (input.getReportName() == null || input.getReportName().trim().isEmpty()) {
//             throw new IllegalArgumentException("Report name is required");
//         }
//     }

//     // ========================================
//     // ORCHESTRATION (Application Logic)
//     // ========================================

//     private List<SummaryMetric> calculateAllMetrics(DataSubsetSpec subset) {
//         List<SummaryMetric> metrics = new ArrayList<>();

//         // Identify numeric columns
//         List<String> numericColumns = new ArrayList<>();
//         for (String columnName : subset.getColumnNames()) {
//             if (summaryStatisticsDataAccessObject.isNumericColumn(subset.getDatasetId(), columnName)) {
//                 numericColumns.add(columnName);
//             }
//         }

//         if (numericColumns.isEmpty()) {
//             throw new IllegalArgumentException("No numeric columns found for statistical analysis");
//         }

//         // Calculate metrics for each numeric column
//         for (String columnName : numericColumns) {
//             metrics.addAll(calculateColumnMetrics(subset, columnName));
//         }

//         // Calculate outliers across all numeric columns
//         List<OutlierPoint> outliers = detectOutliers(subset, numericColumns);
//         if (!outliers.isEmpty()) {
//             metrics.add(new OutlierSummaryMetric(
//                     MetricType.OUTLIERS,
//                     subset,
//                     OUTLIER_Z_SCORE_THRESHOLD,
//                     outliers
//             ));
//         }

//         // Calculate correlation matrix if multiple numeric columns exist
//         if (numericColumns.size() > 1) {
//             double[][] correlationMatrix = calculateCorrelationMatrix(subset, numericColumns);
//             metrics.add(new CorrelationMatrixMetric(
//                     MetricType.CORRELATION_MATRIX,
//                     subset,
//                     correlationMatrix,
//                     numericColumns
//             ));
//         }

//         return metrics;
//     }

//     private List<SummaryMetric> calculateColumnMetrics(DataSubsetSpec subset, String columnName) {
//         List<SummaryMetric> metrics = new ArrayList<>();
//         DataSubsetSpec columnSubset = createColumnSubset(subset, columnName);

//         try {
//             List<Double> values = summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName);

//             if (values.isEmpty()) {
//                 return metrics;
//             }

//             // DELEGATE ALL CALCULATIONS TO StatisticsCalculator
//             double mean = StatisticsCalculator.calculateMean(values);
//             double median = StatisticsCalculator.calculateMedian(values);
//             double stdDev = StatisticsCalculator.calculateStandardDeviation(values, mean);
//             double min = StatisticsCalculator.calculateMin(values);
//             double max = StatisticsCalculator.calculateMax(values);
//             int count = values.size();

//             // Create metric entities (interactor's job)
//             metrics.add(new ScalarSummaryMetrics(MetricType.MEAN, columnSubset, mean));
//             metrics.add(new ScalarSummaryMetrics(MetricType.MEDIAN, columnSubset, median));
//             metrics.add(new ScalarSummaryMetrics(MetricType.STANDARD_DEVIATION, columnSubset, stdDev));
//             metrics.add(new ScalarSummaryMetrics(MetricType.MIN, columnSubset, min));
//             metrics.add(new ScalarSummaryMetrics(MetricType.MAX, columnSubset, max));
//             metrics.add(new ScalarSummaryMetrics(MetricType.COUNT, columnSubset, count));

//         } catch (Exception e) {
//             System.err.println("Error calculating metrics for column " + columnName + ": " + e.getMessage());
//         }

//         return metrics;
//     }

//     private DataSubsetSpec createColumnSubset(DataSubsetSpec originalSubset, String columnName) {
//         return new DataSubsetSpec(
//                 originalSubset.getDatasetId(),
//                 List.of(columnName),
//                 originalSubset.getRowIndices()
//         );
//     }

//     // ========================================
//     // OUTLIER DETECTION (Orchestration + Delegation)
//     // ========================================

//     private List<OutlierPoint> detectOutliers(DataSubsetSpec subset, List<String> numericColumns) {
//         List<OutlierPoint> outliers = new ArrayList<>();

//         for (int colIdx = 0; colIdx < numericColumns.size(); colIdx++) {
//             String columnName = numericColumns.get(colIdx);

//             try {
//                 List<Double> values = summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName);

//                 if (values.isEmpty()) {
//                     continue;
//                 }

//                 // DELEGATE calculation to StatisticsCalculator
//                 List<StatisticsCalculator.OutlierInfo> outlierInfos =
//                         StatisticsCalculator.detectOutliers(values, OUTLIER_Z_SCORE_THRESHOLD);

//                 // Convert to entity format with colIndex
//                 List<Integer> rowIndices = subset.getRowIndices();
//                 for (StatisticsCalculator.OutlierInfo info : outlierInfos) {
//                     int actualRowIndex = (rowIndices != null && info.getIndex() < rowIndices.size())
//                             ? rowIndices.get(info.getIndex())
//                             : info.getIndex();

//                     outliers.add(new OutlierPoint(actualRowIndex, colIdx, info.getZScore()));
//                 }

//             } catch (Exception e) {
//                 System.err.println("Error detecting outliers in column " + columnName + ": " + e.getMessage());
//             }
//         }

//         return outliers;
//     }

//     // ========================================
//     // CORRELATION MATRIX (Orchestration + Delegation)
//     // ========================================

//     private double[][] calculateCorrelationMatrix(DataSubsetSpec subset, List<String> numericColumns) {
//         int n = numericColumns.size();
//         double[][] matrix = new double[n][n];

//         // Retrieve data for all columns
//         List<List<Double>> allColumnValues = new ArrayList<>();
//         for (String columnName : numericColumns) {
//             try {
//                 allColumnValues.add(summaryStatisticsDataAccessObject.getNumericColumnValues(subset, columnName));
//             } catch (Exception e) {
//                 allColumnValues.add(new ArrayList<>());
//             }
//         }

//         // Calculate correlation for each pair
//         for (int i = 0; i < n; i++) {
//             for (int j = 0; j < n; j++) {
//                 if (i == j) {
//                     matrix[i][j] = 1.0;
//                 } else if (i < j) {
//                     // DELEGATE calculation to StatisticsCalculator
//                     double correlation = StatisticsCalculator.calculatePearsonCorrelation(
//                             allColumnValues.get(i),
//                             allColumnValues.get(j)
//                     );
//                     matrix[i][j] = correlation;
//                     matrix[j][i] = correlation; // Mirror to lower triangle
//                 }
//             }
//         }

//         return matrix;
//     }
// }