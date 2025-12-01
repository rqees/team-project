package use_case.save_dataset;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SaveDataSetInteractorTest {
    private DataSet createSampleDataSet() {
        DataRow row1 = new DataRow(Arrays.asList("Alice", "25"));
        DataRow row2 = new DataRow(Arrays.asList("Bob", "30"));
        Column col1 = new Column(Arrays.asList("Alice", "Bob"), DataType.CATEGORICAL, "Name");
        Column col2 = new Column(Arrays.asList("25", "30"), DataType.NUMERIC, "Age");
        return new DataSet(Arrays.asList(row1, row2), Arrays.asList(col1, col2));
    }

    @Test
    void execute_validId_callsDataAccessAndReturnsSuccess() {
        // Arrange
        DataSet sample = createSampleDataSet();
        FakeCurrentTableGateway fakeCurrentTableGateway = new FakeCurrentTableGateway(sample);
        FakeDataAccess fakeDataAccess = new FakeDataAccess();
        FakePresenter fakePresenter = new FakePresenter();
        SaveDataSetInteractor interactor =
                new SaveDataSetInteractor(fakeDataAccess, fakePresenter, fakeCurrentTableGateway);
        SaveDataSetInputData input = new SaveDataSetInputData("test_dataset");

        interactor.execute(input);

        assertTrue(fakeCurrentTableGateway.loadCalled, "CurrentTableGateway.load should have been called");
        assertTrue(fakeDataAccess.saveCalled, "DataAccess.save should have been called");
        assertEquals("test_dataset", fakeDataAccess.lastId);
        assertSame(sample, fakeDataAccess.lastDataSet);
        assertNotNull(fakePresenter.lastOutput, "Presenter should have been called");
        assertTrue(fakePresenter.lastOutput.isSuccess(), "Output should indicate success");
        assertEquals("test_dataset", fakePresenter.lastOutput.getDatasetId());
        assertEquals("Dataset saved successfully.", fakePresenter.lastOutput.getMessage());
    }

    @Test
    void execute_blankId_doesNotCallDataAccessAndReturnsFailure() {
        DataSet sample = createSampleDataSet();
        FakeCurrentTableGateway fakeCurrentTableGateway = new FakeCurrentTableGateway(sample);
        FakeDataAccess fakeDataAccess = new FakeDataAccess();
        FakePresenter fakePresenter = new FakePresenter();
        SaveDataSetInteractor interactor =
                new SaveDataSetInteractor(fakeDataAccess, fakePresenter, fakeCurrentTableGateway);
        SaveDataSetInputData input = new SaveDataSetInputData("   ");

        interactor.execute(input);

        assertFalse(fakeCurrentTableGateway.loadCalled, "CurrentTableGateway.load should NOT be called for blank ID");
        assertFalse(fakeDataAccess.saveCalled, "DataAccess.save should NOT have been called for blank ID");
        assertNotNull(fakePresenter.lastOutput, "Presenter should have been called");
        assertFalse(fakePresenter.lastOutput.isSuccess(), "Output should indicate failure");
        assertEquals("Dataset ID cannot be empty.", fakePresenter.lastOutput.getMessage());
    }

    @Test
    void execute_noCurrentDataSet_returnsFailure() {
        FakeCurrentTableGateway fakeCurrentTableGateway = new FakeCurrentTableGateway(null);
        FakeDataAccess fakeDataAccess = new FakeDataAccess();
        FakePresenter fakePresenter = new FakePresenter();
        SaveDataSetInteractor interactor =
                new SaveDataSetInteractor(fakeDataAccess, fakePresenter, fakeCurrentTableGateway);
        SaveDataSetInputData input = new SaveDataSetInputData("test_dataset");

        interactor.execute(input);

        assertTrue(fakeCurrentTableGateway.loadCalled, "CurrentTableGateway.load should have been called");
        assertFalse(fakeDataAccess.saveCalled, "DataAccess.save should NOT be called when there is no dataset");
        assertNotNull(fakePresenter.lastOutput, "Presenter should have been called");
        assertFalse(fakePresenter.lastOutput.isSuccess(), "Output should indicate failure");
        assertEquals("No dataset loaded to save.", fakePresenter.lastOutput.getMessage());
    }

    private static class FakeDataAccess implements SaveDataSetDataAccessInterface {
        boolean saveCalled = false;
        String lastId = null;
        DataSet lastDataSet = null;

        @Override
        public void save(String id, DataSet dataSet) {
            saveCalled = true;
            lastId = id;
            lastDataSet = dataSet;
        }
    }

    private static class FakePresenter implements SaveDataSetOutputBoundary {
        SaveDataSetOutputData lastOutput = null;

        @Override
        public void present(SaveDataSetOutputData outputData) {
            this.lastOutput = outputData;
        }
    }

    private static class FakeCurrentTableGateway implements CurrentTableGateway {
        boolean loadCalled = false;
        private final DataSet dataSet;

        private FakeCurrentTableGateway(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public DataSet load() {
            loadCalled = true;
            return dataSet;
        }
    }
}
