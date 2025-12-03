package use_case.load_csv;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import org.junit.jupiter.api.Test;
import use_case.dataset.CurrentTableGateway;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadCSVInteractorTest {

    static class MockPresenter implements LoadOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;
        String receivedError = null;

        @Override
        public void prepareFail(String errorMessage) {
            failCalled = true;
            receivedError = errorMessage;
        }

        @Override
        public void prepareSuccess() {
            successCalled = true;
        }
    }

    static class MockGateway implements CurrentTableGateway {
        DataSet saved;

        @Override
        public void save(DataSet table) {
            this.saved = table;
        }

        @Override
        public DataSet load() {
            return this.saved;
        }
    }

    @Test
    void testExecuteFailPath() {
        MockPresenter presenter = new MockPresenter();
        MockGateway gateway = new MockGateway();

        LoadInteractor interactor = new LoadInteractor(presenter, gateway);

        LoadInputData data = new LoadInputData(null, true, "failure occurred");
        interactor.execute(data);

        assertTrue(presenter.failCalled);
        assertEquals("failure occurred", presenter.receivedError);
        assertFalse(presenter.successCalled);
        assertNull(gateway.saved);
    }

    @Test
    void testExecuteSuccessPathAllDataTypes() {
        /*
            Mixed CSV:
            Numeric: 1, 2.5
            Boolean: true, false
            Date: 2024-01-01
            Categorical: empty strings
        */
        List<String> lines = new ArrayList<>(List.of(
                "num,bool,date,cat",
                "1,true,2024-01-01,hello",
                "2.5,false,2024-01-02,",
                ",,"
        ));

        MockPresenter presenter = new MockPresenter();
        MockGateway gateway = new MockGateway();

        LoadInteractor interactor = new LoadInteractor(presenter, gateway);
        LoadInputData data = new LoadInputData(lines, false, null);

        interactor.execute(data);

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);

        assertNotNull(gateway.saved);
        DataSet table = gateway.saved;

        // Columns
        List<Column> cols = table.getColumns();
        assertEquals(4, cols.size());

        assertEquals("num", cols.get(0).getHeader());
        assertEquals("bool", cols.get(1).getHeader());
        assertEquals("date", cols.get(2).getHeader());
        assertEquals("cat", cols.get(3).getHeader());

        // Datatype checks
        assertEquals(DataType.NUMERIC, cols.get(0).getDataType());
        assertEquals(DataType.BOOLEAN, cols.get(1).getDataType());
        assertEquals(DataType.DATE, cols.get(2).getDataType());
        assertEquals(DataType.CATEGORICAL, cols.get(3).getDataType());

        // Rows
        List<DataRow> rows = table.getRows();
        assertEquals(3, rows.size());
        assertEquals(List.of("1", "true", "2024-01-01", "hello"), rows.get(0).getCells());
        assertEquals(List.of("2.5", "false", "2024-01-02", ""), rows.get(1).getCells());
        assertEquals(List.of("", "", ""), rows.get(2).getCells());
    }

    @Test
    void testDatatypePriorityTieBreakers() {
        /*
            Ensures datatype priority ordering works:
            - numeric >= boolean >= date >= categorical
        */

        List<String> lines = new ArrayList<>(List.of(
                "col",
                "1",
                "true",
                "2024-01-01",
                ""
        ));

        MockPresenter presenter = new MockPresenter();
        MockGateway gateway = new MockGateway();
        LoadInteractor interactor = new LoadInteractor(presenter, gateway);

        interactor.execute(new LoadInputData(lines, false, null));

        Column col = gateway.saved.getColumns().get(0);

        // numeric count = 1
        // boolean count = 1
        // date count = 1
        // categorical count = 1
        // numeric wins because >= all others
        assertEquals(DataType.NUMERIC, col.getDataType());
    }
}
