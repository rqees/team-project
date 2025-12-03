package use_case.cleaner;


import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import entity.MissingCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.dataset.CurrentTableGateway;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for DataCleanerInteractor.
 * Achieves 100% line coverage for DataCleanerInteractor
 */
class DataCleanerInteractorTest {
    private TempTableGateway fakeGateway;
    private TempDataCleaningPresenter fakePresenter;
    private DataCleanerInteractor interactor;


    @BeforeEach
    void setUp() {
        fakeGateway = new TempTableGateway();
        fakePresenter = new TempDataCleaningPresenter();
        interactor = new DataCleanerInteractor(fakeGateway, fakePresenter);
    }


    //cleanEditedCell
    @Test
    void testCleanEditedCell_ValidNumericValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 1, "25");


        // Act
        interactor.cleanEditedCell(inputData);


        // Assert
        assertTrue(fakePresenter.editedCellCalled);
        assertEquals(0, fakePresenter.editedCellOutput.getRowIndex());
        assertEquals(1, fakePresenter.editedCellOutput.getColIndex());
        assertEquals("25", fakePresenter.editedCellOutput.getCleanedValue());
        assertTrue(fakeGateway.saveCalled);
    }


    @Test
    void testCleanEditedCell_InvalidNumericValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 1, "abc");


        // Act
        interactor.cleanEditedCell(inputData);


        // Assert
        assertTrue(fakePresenter.editedCellCalled);
        assertNull(fakePresenter.editedCellOutput.getCleanedValue());
    }


    @Test
    void testCleanEditedCell_NullValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 1, null);


        // Act
        interactor.cleanEditedCell(inputData);


        // Assert
        assertTrue(fakePresenter.editedCellCalled);
        assertNull(fakePresenter.editedCellOutput.getCleanedValue());
    }


    @Test
    void testCleanEditedCell_BlankValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 1, "   ");


        // Act
        interactor.cleanEditedCell(inputData);


        // Assert
        assertTrue(fakePresenter.editedCellCalled);
        assertNull(fakePresenter.editedCellOutput.getCleanedValue());
    }


    @Test
    void testCleanEditedCell_ValidCategoricalValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 0, "NewName");


        // Act
        interactor.cleanEditedCell(inputData);


        // Assert
        assertTrue(fakePresenter.editedCellCalled);
        assertEquals("NewName", fakePresenter.editedCellOutput.getCleanedValue());
    }


    @Test
    void testCleanEditedCell_NoDataSetLoaded() {
        // Arrange
        fakeGateway.setDataSet(null);


        DataCleaningInputData.EditedCellInputData inputData =
                new DataCleaningInputData.EditedCellInputData(0, 0, "test");


        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            interactor.cleanEditedCell(inputData);
        });
    }


    // editHeader

    @Test
    void testEditHeader_ValidNewHeader() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "FullName");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditCalled);
        assertEquals(0, fakePresenter.headerEditOutput.getColIndex());
        assertEquals("FullName", fakePresenter.headerEditOutput.getNewHeader());
        assertFalse(fakePresenter.headerEditFailureCalled);
        assertTrue(fakeGateway.saveCalled);
    }


    @Test
    void testEditHeader_DuplicateHeader() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Try to rename "Name" to "Age" (which already exists)
        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "Age");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Header already exists", fakePresenter.headerEditFailureMessage);
        assertFalse(fakePresenter.headerEditCalled);
    }


    @Test
    void testEditHeader_DuplicateHeader_CaseInsensitive() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Try to rename "Name" to "age" (different case, but should still be duplicate)
        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "age");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Header already exists", fakePresenter.headerEditFailureMessage);
    }


    @Test
    void testEditHeader_EmptyHeader() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Header cannot be empty", fakePresenter.headerEditFailureMessage);
    }


    @Test
    void testEditHeader_NullHeader() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, null);


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Header cannot be empty", fakePresenter.headerEditFailureMessage);
    }


    @Test
    void testEditHeader_WhitespaceOnlyHeader() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "   ");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Header cannot be empty", fakePresenter.headerEditFailureMessage);
    }


    @Test
    void testEditHeader_SameHeaderName() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Try to rename "Name" to "Name" (same name)
        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "Name");


        // Act
        interactor.editHeader(inputData);


        // Assert
        assertTrue(fakePresenter.headerEditCalled);
        assertFalse(fakePresenter.headerEditFailureCalled);
    }


    @Test
    void testEditHeader_NoDataSetLoaded() {
        // Arrange
        fakeGateway.setDataSet(null);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "NewName");


        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            interactor.editHeader(inputData);
        });
    }


    @Test
    void testEditHeader_NullHeaderInDataSet() {
        // Arrange
        DataSet dataSetWithNullHeader = createDataSetWithNullHeader();
        fakeGateway.setDataSet(dataSetWithNullHeader);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(1, "NewName");


        // Act
        interactor.editHeader(inputData);


        // Assert - Should call presenter's failure method
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Column header cannot be null.", fakePresenter.headerEditFailureMessage);
        assertFalse(fakePresenter.headerEditCalled);
    }


    @Test
    void testEditHeader_DuplicateHeaderInDataSet() {
        // Arrange
        DataSet dataSetWithDuplicates = createDataSetWithDuplicateHeaders();
        fakeGateway.setDataSet(dataSetWithDuplicates);


        DataCleaningInputData.HeaderEditInputData inputData =
                new DataCleaningInputData.HeaderEditInputData(0, "NewName");


        // Act
        interactor.editHeader(inputData);


        // Assert - Should call presenter's failure method
        assertTrue(fakePresenter.headerEditFailureCalled);
        assertEquals("Column header already exists.", fakePresenter.headerEditFailureMessage);
        assertFalse(fakePresenter.headerEditCalled);
    }


    // cleanEntireDataSet
    @Test
    void testCleanEntireDataSet_WithInvalidValues() {
        // Arrange
        DataSet dataSet = createDataSetWithInvalidValues();
        fakeGateway.setDataSet(dataSet);


        // Act
        interactor.cleanEntireDataSet();


        // Assert
        assertTrue(fakePresenter.entireDataSetCleanedCalled);
        assertFalse(fakePresenter.entireDataSetCleanedOutput.getMissingCells().isEmpty());


        List<MissingCell> missingCells = fakePresenter.entireDataSetCleanedOutput.getMissingCells();
        // Should have found the invalid "abc" in Age column
        assertTrue(missingCells.stream().anyMatch(
                cell -> cell.getRowIndex() == 1 && cell.getColumnHeader().equals("Age")
        ));
    }


    @Test
    void testCleanEntireDataSet_AllValidValues() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        interactor.cleanEntireDataSet();


        // Assert
        assertTrue(fakePresenter.entireDataSetCleanedCalled);
        assertTrue(fakePresenter.entireDataSetCleanedOutput.getMissingCells().isEmpty());
    }


    @Test
    void testCleanEntireDataSet_NoDataSetLoaded() {
        // Arrange
        fakeGateway.setDataSet(null);


        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            interactor.cleanEntireDataSet();
        });
    }


    // findMissingCells
    @Test
    void testFindMissingCells_WithMissingValues() {
        // Arrange
        DataSet dataSet = createDataSetWithMissingValues();
        fakeGateway.setDataSet(dataSet);


        // Act
        List<MissingCell> missingCells = interactor.findMissingCells();


        // Assert
        assertFalse(missingCells.isEmpty());
        assertEquals(2, missingCells.size());


        // Check that both missing cells are identified
        assertTrue(missingCells.stream().anyMatch(
                cell -> cell.getRowIndex() == 0 && cell.getColumnHeader().equals("Age")
        ));
        assertTrue(missingCells.stream().anyMatch(
                cell -> cell.getRowIndex() == 1 && cell.getColumnHeader().equals("Name")
        ));
    }


    @Test
    void testFindMissingCells_NoMissingValues() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        List<MissingCell> missingCells = interactor.findMissingCells();


        // Assert
        assertTrue(missingCells.isEmpty());
    }


    // cleanValueForColumn
    @Test
    void testCleanValueForColumn_ValidNumeric() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 1, "42");


        // Assert
        assertEquals("42", result);
    }


    @Test
    void testCleanValueForColumn_InvalidNumeric() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 1, "not a number");


        // Assert
        assertNull(result);
    }


    @Test
    void testCleanValueForColumn_ValidCategorical() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 0, "Some Text");


        // Assert
        assertEquals("Some Text", result);
    }


    @Test
    void testCleanValueForColumn_NullValue() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 0, null);


        // Assert
        assertNull(result);
    }


    @Test
    void testCleanValueForColumn_EmptyString() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 0, "");


        // Assert
        assertNull(result);
    }


    @Test
    void testCleanValueForColumn_WhitespaceOnly() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 0, "   ");


        // Assert
        assertNull(result);
    }


    @Test
    void testCleanValueForColumn_TrimsWhitespace() {
        // Arrange
        DataSet dataSet = createTestDataSet();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 0, "  Alice  ");


        // Assert
        assertEquals("Alice", result);
    }


    @Test
    void testCleanValueForColumn_BooleanDataType() {
        // Arrange
        DataSet dataSet = createDataSetWithBooleanColumn();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 3, "true");


        // Assert
        assertEquals("true", result);
    }


    @Test
    void testCleanValueForColumn_DateDataType() {
        // Arrange
        DataSet dataSet = createDataSetWithDateColumn();
        fakeGateway.setDataSet(dataSet);


        // Act
        String result = interactor.cleanValueForColumn(dataSet, 3, "2024-01-15");


        // Assert
        assertEquals("2024-01-15", result);
    }


    @Test
    void testCleanEntireDataSet_WithMixedValidAndInvalid() {
        // Arrange
        DataSet dataSet = createDataSetWithMixedValidity();
        fakeGateway.setDataSet(dataSet);


        // Act
        interactor.cleanEntireDataSet();


        // Assert
        assertTrue(fakePresenter.entireDataSetCleanedCalled);
        List<MissingCell> missingCells = fakePresenter.entireDataSetCleanedOutput.getMissingCells();


        // Should find multiple invalid cells
        assertTrue(missingCells.size() > 0);
    }


    // HELPER METHODS
    /**
     * Creates a test dataset with valid data:
     * - Name (CATEGORICAL): Alice, Bob
     * - Age (NUMERIC): 30, 25
     * - City (CATEGORICAL): Toronto, Vancouver
     */
    private DataSet createTestDataSet() {
        // Create columns with their data
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("Bob");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("25");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");
        row1Cells.add("Toronto");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("25");
        row2Cells.add("Vancouver");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with some invalid values:
     * - Row 0: Alice, 30, Toronto (valid)
     * - Row 1: Bob, "abc", Vancouver (invalid age)
     */
    private DataSet createDataSetWithInvalidValues() {
        // Create columns with their data
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("Bob");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("abc");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");
        row1Cells.add("Toronto");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("abc");
        row2Cells.add("Vancouver");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with missing (null) values:
     * - Row 0: Alice, null, Toronto
     * - Row 1: null, 25, Vancouver
     */
    private DataSet createDataSetWithMissingValues() {
        // Create columns with their data
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add(null);
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add(null);
        ageData.add("25");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add(null);
        row1Cells.add("Toronto");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add(null);
        row2Cells.add("25");
        row2Cells.add("Vancouver");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with a BOOLEAN column for testing.
     */
    private DataSet createDataSetWithBooleanColumn() {
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("Bob");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("25");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<String> activeData = new ArrayList<>();
        activeData.add("true");
        activeData.add("false");
        Column activeColumn = new Column(activeData, DataType.BOOLEAN, "Active");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);
        columns.add(activeColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");
        row1Cells.add("Toronto");
        row1Cells.add("true");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("25");
        row2Cells.add("Vancouver");
        row2Cells.add("false");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with a DATE column for testing.
     */
    private DataSet createDataSetWithDateColumn() {
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("Bob");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("25");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<String> birthDateData = new ArrayList<>();
        birthDateData.add("2024-01-15");
        birthDateData.add("2023-12-25");
        Column birthDateColumn = new Column(birthDateData, DataType.DATE, "BirthDate");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);
        columns.add(birthDateColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");
        row1Cells.add("Toronto");
        row1Cells.add("2024-01-15");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("25");
        row2Cells.add("Vancouver");
        row2Cells.add("2023-12-25");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with mixed valid and invalid values across different data types.
     */
    private DataSet createDataSetWithMixedValidity() {
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("");  // Invalid - empty categorical
        nameData.add("Charlie");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, "Name");


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("invalid");  // Invalid numeric
        ageData.add("35");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<String> cityData = new ArrayList<>();
        cityData.add("Toronto");
        cityData.add("Vancouver");
        cityData.add(null);  // Invalid - null
        Column cityColumn = new Column(cityData, DataType.CATEGORICAL, "City");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);
        columns.add(cityColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");
        row1Cells.add("Toronto");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("");
        row2Cells.add("invalid");
        row2Cells.add("Vancouver");


        List<String> row3Cells = new ArrayList<>();
        row3Cells.add("Charlie");
        row3Cells.add("35");
        row3Cells.add(null);


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));
        rows.add(new DataRow(row3Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset where one column has a null header.
     * This should trigger validation error in editHeaderInternal.
     */
    private DataSet createDataSetWithNullHeader() {
        List<String> nameData = new ArrayList<>();
        nameData.add("Alice");
        nameData.add("Bob");
        Column nameColumn = new Column(nameData, DataType.CATEGORICAL, null);  // NULL header


        List<String> ageData = new ArrayList<>();
        ageData.add("30");
        ageData.add("25");
        Column ageColumn = new Column(ageData, DataType.NUMERIC, "Age");


        List<Column> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(ageColumn);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("30");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("25");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    /**
     * Creates a dataset with duplicate headers (case-insensitive).
     * This should trigger validation error in rebuildUniqueHeaders.
     */
    private DataSet createDataSetWithDuplicateHeaders() {
        List<String> data1 = new ArrayList<>();
        data1.add("Alice");
        data1.add("Bob");
        Column column1 = new Column(data1, DataType.CATEGORICAL, "Name");


        List<String> data2 = new ArrayList<>();
        data2.add("Charlie");
        data2.add("David");
        Column column2 = new Column(data2, DataType.CATEGORICAL, "name");  // Duplicate (case-insensitive)


        List<Column> columns = new ArrayList<>();
        columns.add(column1);
        columns.add(column2);


        List<String> row1Cells = new ArrayList<>();
        row1Cells.add("Alice");
        row1Cells.add("Charlie");


        List<String> row2Cells = new ArrayList<>();
        row2Cells.add("Bob");
        row2Cells.add("David");


        List<DataRow> rows = new ArrayList<>();
        rows.add(new DataRow(row1Cells));
        rows.add(new DataRow(row2Cells));


        return new DataSet(rows, columns);
    }


    // Temp implementation
    /**
     * Temp implementation of CurrentTableGateway for testing.
     */
    private static class TempTableGateway implements CurrentTableGateway {
        private DataSet dataSet;
        boolean saveCalled = false;
        boolean loadCalled = false;


        void setDataSet(DataSet dataSet) {
            this.dataSet = dataSet;
        }


        @Override
        public void save(DataSet dataSet) {
            this.saveCalled = true;
            this.dataSet = dataSet;
        }


        @Override
        public DataSet load() {
            this.loadCalled = true;
            return dataSet;
        }
    }


    /**
     * Temp implementation of DataCleaningOutputBoundary for testing.
     */
    private static class TempDataCleaningPresenter implements DataCleaningOutputBoundary {
        boolean editedCellCalled = false;
        DataCleaningOutputData.EditedCellOutputData editedCellOutput = null;


        boolean headerEditCalled = false;
        DataCleaningOutputData.HeaderEditOutputData headerEditOutput = null;


        boolean headerEditFailureCalled = false;
        String headerEditFailureMessage = null;


        boolean entireDataSetCleanedCalled = false;
        DataCleaningOutputData.CleanEntireDataSetOutputData entireDataSetCleanedOutput = null;


        @Override
        public void presentEditedCell(DataCleaningOutputData.EditedCellOutputData outputData) {
            this.editedCellCalled = true;
            this.editedCellOutput = outputData;
        }


        @Override
        public void presentHeaderEdit(DataCleaningOutputData.HeaderEditOutputData outputData) {
            this.headerEditCalled = true;
            this.headerEditOutput = outputData;
        }


        @Override
        public void presentHeaderEditFailure(String errorMessage) {
            this.headerEditFailureCalled = true;
            this.headerEditFailureMessage = errorMessage;
        }


        @Override
        public void presentEntireDataSetCleaned(
                DataCleaningOutputData.CleanEntireDataSetOutputData outputData) {
            this.entireDataSetCleanedCalled = true;
            this.entireDataSetCleanedOutput = outputData;
        }
    }
}

