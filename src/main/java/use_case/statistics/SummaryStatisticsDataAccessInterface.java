package use_case.statistics;

import entity.DataSet;
import entity.DataSubsetSpec;

import java.util.List;

/**
 * The DAO interface for the Summary Statistics Use Case.
 */
public interface SummaryStatisticsDataAccessInterface {
    /**
     * Retrieves the data subset specification by its identifier.
     *
     * @param dataSubsetId identifier of the data subset to retrieve
     * @return the DataSubsetSpec associated with the given id, or null if not found
     */
    DataSubsetSpec getDataSubsetById(String dataSubsetId);

    /**
     * Retrieves the complete dataset by its identifier.
     *
     * @param datasetId identifier of the dataset
     * @return the DataSet, or null if not found
     */
    DataSet getDataSet(String datasetId);

    /**
     * Retrieves numeric values for a specific column within a data subset.
     *
     * @param subset the data subset specification
     * @param columnName the name of the column to retrieve
     * @return list of numeric values from the specified column and rows
     * @throws IllegalArgumentException if the column is not numeric or doesn't exist
     */
    List<Double> getNumericColumnValues(DataSubsetSpec subset, String columnName);

    /**
     * Retrieves all values for a specific column within a data subset.
     *
     * @param subset the data subset specification
     * @param columnName the name of the column to retrieve
     * @return list of string values from the specified column and rows
     * @throws IllegalArgumentException if the column doesn't exist
     */
    List<String> getColumnValues(DataSubsetSpec subset, String columnName);

    /**
     * Checks if a column contains numeric data.
     *
     * @param datasetId the dataset identifier
     * @param columnName the column name
     * @return true if the column is numeric, false otherwise
     */
    boolean isNumericColumn(String datasetId, String columnName);

    /**
     * Validates that a data subset specification is valid.
     *
     * @param subset the data subset to validate
     * @return true if valid, false otherwise
     */
    boolean validateDataSubset(DataSubsetSpec subset);
}
