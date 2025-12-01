package use_case.save_dataset;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

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
    void save_validId_callsDataAccessAndReturnsSuccess() {
        // Arrange
        DataSet sample = createSampleDataSet();
        FakeDataAccess fakeDataAccess = new FakeDataAccess();
        FakePresenter fakePresenter = new FakePresenter();
        SaveDataSetInteractor interactor = new SaveDataSetInteractor(fakeDataAccess, fakePresenter);
        SaveDataSetInputData input = new SaveDataSetInputData("test_dataset", sample);

        interactor.save(input);

        assertTrue(fakeDataAccess.saveCalled, "DataAccess.save should have been called");
        assertEquals("test_dataset", fakeDataAccess.lastId);
        assertSame(sample, fakeDataAccess.lastDataSet);
        assertNotNull(fakePresenter.lastOutput, "Presenter should have been called");
        assertTrue(fakePresenter.lastOutput.isSuccess(), "Output should indicate success");
        assertEquals("test_dataset", fakePresenter.lastOutput.getDatasetId());
        assertEquals("Dataset saved successfully.", fakePresenter.lastOutput.getMessage());
    }

    @Test
    void save_blankId_doesNotCallDataAccessAndReturnsFailure() {
        DataSet sample = createSampleDataSet();
        FakeDataAccess fakeDataAccess = new FakeDataAccess();
        FakePresenter fakePresenter = new FakePresenter();
        SaveDataSetInteractor interactor = new SaveDataSetInteractor(fakeDataAccess, fakePresenter);
        SaveDataSetInputData input = new SaveDataSetInputData("   ", sample);

        interactor.save(input);

        assertFalse(fakeDataAccess.saveCalled, "DataAccess.save should NOT have been called for blank ID");
        assertNotNull(fakePresenter.lastOutput, "Presenter should have been called");
        assertFalse(fakePresenter.lastOutput.isSuccess(), "Output should indicate failure");
        assertEquals("Dataset ID cannot be empty.", fakePresenter.lastOutput.getMessage());
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
}
