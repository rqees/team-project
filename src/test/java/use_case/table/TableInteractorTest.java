package use_case.table;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.dataset.CurrentTableGateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DisplayTableInteractor.
 * Achieves 100% line coverage.
 */
class DisplayTableInteractorTest {
    private FakeTableGateway fakeGateway;
    private FakeTablePresenter fakePresenter;
    private DisplayTableInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeGateway = new FakeTableGateway();
        fakePresenter = new FakeTablePresenter();
        interactor = new DisplayTableInteractor(fakeGateway, fakePresenter);
    }

    @Test
    void testExecute_NullDataSet_CallsFailureView() {
        // Arrange
        fakeGateway.dataSet = null;

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.failureViewCalled);
        assertEquals("No dataset to display", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_EmptyColumns_CallsFailureView() {
        // Arrange
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("cell1")
        ));
        fakeGateway.dataSet = new DataSet(rows, new ArrayList<>());

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.failureViewCalled);
        assertEquals("Dataset is empty", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_EmptyRows_CallsFailureView() {
        // Arrange
        List<Column> columns = createColumns(Arrays.asList("Col1", "Col2"), 0);
        fakeGateway.dataSet = new DataSet(new ArrayList<>(), columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.failureViewCalled);
        assertEquals("Dataset is empty", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_ValidDataSet_CallsSuccessView() {
        // Arrange
        List<String> headers = Arrays.asList("Name", "Age", "City");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("Alice", "25", "Toronto"),
                Arrays.asList("Bob", "30", "Vancouver"),
                Arrays.asList("Charlie", "35", "Montreal")
        ));
        List<Column> columns = createColumns(headers, 3);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertArrayEquals(new String[]{"Name", "Age", "City"}, fakePresenter.outputData.getHeaders());

        String[][] rowData = fakePresenter.outputData.getRowData();
        assertEquals(3, rowData.length);
        assertArrayEquals(new String[]{"Alice", "25", "Toronto"}, rowData[0]);
        assertArrayEquals(new String[]{"Bob", "30", "Vancouver"}, rowData[1]);
        assertArrayEquals(new String[]{"Charlie", "35", "Montreal"}, rowData[2]);

        assertFalse(fakePresenter.failureViewCalled);
    }

    @Test
    void testExecute_SingleRowDataSet() {
        // Arrange
        List<String> headers = Arrays.asList("ID", "Value");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("1", "Test")
        ));
        List<Column> columns = createColumns(headers, 1);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(1, fakePresenter.outputData.getRowData().length);
        assertArrayEquals(new String[]{"1", "Test"}, fakePresenter.outputData.getRowData()[0]);
    }

    @Test
    void testExecute_SingleColumnDataSet() {
        // Arrange
        List<String> headers = Arrays.asList("SingleColumn");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("Value1"),
                Arrays.asList("Value2")
        ));
        List<Column> columns = createColumns(headers, 2);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertArrayEquals(new String[]{"SingleColumn"}, fakePresenter.outputData.getHeaders());
        assertEquals(2, fakePresenter.outputData.getRowData().length);
    }

    @Test
    void testExecute_LargeDataSet() {
        // Arrange
        List<String> headers = Arrays.asList("Col1", "Col2", "Col3", "Col4", "Col5");

        List<List<String>> rowValues = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rowValues.add(Arrays.asList("A" + i, "B" + i, "C" + i, "D" + i, "E" + i));
        }
        List<DataRow> rows = createDataRows(rowValues);
        List<Column> columns = createColumns(headers, 100);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(5, fakePresenter.outputData.getHeaders().length);
        assertEquals(100, fakePresenter.outputData.getRowData().length);
    }

    @Test
    void testExecute_DataSetWithEmptyStrings() {
        // Arrange
        List<String> headers = Arrays.asList("Field1", "Field2");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("", "Value"),
                Arrays.asList("Value", "")
        ));
        List<Column> columns = createColumns(headers, 2);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals("", fakePresenter.outputData.getRowData()[0][0]);
        assertEquals("Value", fakePresenter.outputData.getRowData()[0][1]);
        assertEquals("Value", fakePresenter.outputData.getRowData()[1][0]);
        assertEquals("", fakePresenter.outputData.getRowData()[1][1]);
    }

    @Test
    void testExecute_DataSetWithSpecialCharacters() {
        // Arrange
        List<String> headers = Arrays.asList("Name", "Description");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("Test@123", "Hello, World!"),
                Arrays.asList("User#456", "Line1\nLine2")
        ));
        List<Column> columns = createColumns(headers, 2);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals("Test@123", fakePresenter.outputData.getRowData()[0][0]);
        assertEquals("Hello, World!", fakePresenter.outputData.getRowData()[0][1]);
        assertEquals("User#456", fakePresenter.outputData.getRowData()[1][0]);
        assertEquals("Line1\nLine2", fakePresenter.outputData.getRowData()[1][1]);
    }

    @Test
    void testExecute_VerifiesCorrectArrayConversion() {
        // Arrange
        List<String> headers = Arrays.asList("A", "B", "C");
        List<DataRow> rows = createDataRows(Arrays.asList(
                Arrays.asList("1", "2", "3")
        ));
        List<Column> columns = createColumns(headers, 1);

        fakeGateway.dataSet = new DataSet(rows, columns);

        // Act
        interactor.execute();

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(headers.size(), fakePresenter.outputData.getHeaders().length);
        assertEquals(1, fakePresenter.outputData.getRowData().length);
        assertEquals(3, fakePresenter.outputData.getRowData()[0].length);
    }

    // ===== FAKE IMPLEMENTATIONS =====

    private static class FakeTableGateway implements CurrentTableGateway {
        DataSet dataSet;

        @Override
        public DataSet load() {
            return dataSet;
        }

        @Override
        public void save(DataSet dataSet) {
            this.dataSet = dataSet;
        }
    }

    private static class FakeTablePresenter implements DisplayTableOutputBoundary {
        boolean successViewCalled = false;
        boolean failureViewCalled = false;
        DisplayTableOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(DisplayTableOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailureView(String errorMessage) {
            this.failureViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }

    // Helper methods to create real Column and DataRow objects
    private List<Column> createColumns(List<String> headers, int numRows) {
        List<Column> columns = new ArrayList<>();
        for (String header : headers) {
            // Create column cells (each column has numRows cells)
            List<String> cells = new ArrayList<>();
            for (int i = 0; i < numRows; i++) {
                cells.add("");  // Placeholder values
            }
            columns.add(new Column(cells, null, header));
        }
        return columns;
    }

    private List<DataRow> createDataRows(List<List<String>> rowValues) {
        List<DataRow> rows = new ArrayList<>();
        for (List<String> rowValue : rowValues) {
            rows.add(new DataRow(new ArrayList<>(rowValue)));
        }
        return rows;
    }
}
