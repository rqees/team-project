package use_case.load_api;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import org.junit.jupiter.api.Test;
import use_case.dataset.CurrentTableGateway;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadAPIInteractorTest {


    static class MockPresenter implements LoadAPIOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;
        String error = null;

        @Override
        public void prepareFail(String errorMessage) {
            failCalled = true;
            error = errorMessage;
        }

        @Override
        public void prepareSuccess() {
            successCalled = true;
        }
    }

    static class MockGateway implements CurrentTableGateway {
        DataSet saved = null;

        @Override
        public void save(DataSet table) {
            this.saved = table;
        }

        @Override
        public DataSet load() {
            return saved;
        }
    }

    static class MockAPIGateway implements LoadAPIDataGateway {
        String returnValue;

        MockAPIGateway(String returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public String getCSV(String datasetName) {
            return returnValue;
        }
    }

    // -------------------------------------------------------

    @Test
    void testFail_datasetNotFound() {
        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway("Dataset not found.");

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("anything"));

        assertTrue(presenter.failCalled);
        assertEquals("Dataset not found.", presenter.error);
        assertFalse(presenter.successCalled);
        assertNull(tableGateway.saved);
    }

    @Test
    void testFail_noCSV() {
        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway("Dataset found, but no CSV resource available.");

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("dataset"));

        assertTrue(presenter.failCalled);
        assertEquals("Dataset found, but no CSV resource available.", presenter.error);
        assertNull(tableGateway.saved);
    }

    @Test
    void testFail_errorPrefix() {
        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway("Error: Timeout");

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("dataset"));

        assertTrue(presenter.failCalled);
        assertEquals("Error: Timeout", presenter.error);
        assertNull(tableGateway.saved);
    }

    @Test
    void testSuccess_fullProcessing() {
        /*
             Headers: num,bool,date,cat
             Rows:
               1,true,2024-01-01,hello
               2.5,false,2024-01-02,
               ,,,
         */

        String csv =
                "num,bool,date,cat\n" +
                        "1,true,2024-01-01,hello\n" +
                        "2.5,false,2024-01-02,\n" +
                        ",,,";

        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway(csv);

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("dataset"));

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        DataSet saved = tableGateway.saved;
        assertNotNull(saved);

        // Columns
        List<Column> cols = saved.getColumns();
        assertEquals(4, cols.size());
        assertEquals("num", cols.get(0).getHeader());
        assertEquals("bool", cols.get(1).getHeader());
        assertEquals("date", cols.get(2).getHeader());
        assertEquals("cat", cols.get(3).getHeader());

        assertEquals(DataType.NUMERIC, cols.get(0).getDataType());
        assertEquals(DataType.BOOLEAN, cols.get(1).getDataType());
        assertEquals(DataType.DATE, cols.get(2).getDataType());
        assertEquals(DataType.CATEGORICAL, cols.get(3).getDataType());

        // Rows
        List<DataRow> rows = saved.getRows();
        assertEquals(3, rows.size());

        assertEquals(List.of("1", "true", "2024-01-01", "hello"), rows.get(0).getCells());
        assertEquals(List.of("2.5", "false", "2024-01-02", ""), rows.get(1).getCells());
        assertEquals(List.of("", "", "", ""), rows.get(2).getCells());
    }

    @Test
    void testDatatypeTieBreakers() {
        /*
            Ensures type priority rule:
            numeric >= boolean >= date >= categorical

            Values:
             numeric: 1
             boolean: true
             date: 2024-01-01
             categorical: ""
        */

        String csv =
                "col\n" +
                        "1\n" +
                        "true\n" +
                        "2024-01-01\n" +
                        "";

        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway(csv);

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("dataset"));

        Column col = tableGateway.saved.getColumns().get(0);

        // numeric count = 1; boolean=1; date=1; categorical=1 → numeric wins
        assertEquals(DataType.NUMERIC, col.getDataType());
    }

    @Test
    void testUnevenRows_fillMissingCells() {
        String csv =
                "A,B,C\n" +
                        "x,y\n" +
                        "1,2,3,4"; // extra ignored movement since split includes all

        MockPresenter presenter = new MockPresenter();
        MockGateway tableGateway = new MockGateway();
        MockAPIGateway api = new MockAPIGateway(csv);

        LoadAPIInteractor interactor = new LoadAPIInteractor(presenter, api, tableGateway);

        interactor.execute(new LoadAPIInputData("dataset"));

        DataSet saved = tableGateway.saved;

        // Should use the maximum column count (4 cells in last row)
        assertEquals(3, saved.getColumns().size());

        // First row "x,y" → fill missing with ""
        assertEquals(List.of("x","y"), saved.getRows().get(0).getCells());
    }
}
