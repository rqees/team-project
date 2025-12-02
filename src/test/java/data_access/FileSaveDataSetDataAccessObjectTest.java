package data_access;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FileSaveDataSetDataAccessObject}.
 */
class FileSaveDataSetDataAccessObjectTest {

    @TempDir
    Path tempDir;
    /**
     * Creates a sample dataset for validating CSV output.
     *
     * @return dataset containing two rows and two columns
     */
    private DataSet createSampleDataSet() {
        DataRow row1 = new DataRow(Arrays.asList("Alice", "25"));
        DataRow row2 = new DataRow(Arrays.asList("Bob", "30"));
        Column col1 = new Column(Arrays.asList("Alice", "Bob"), DataType.CATEGORICAL, "Name");
        Column col2 = new Column(Arrays.asList("25", "30"), DataType.NUMERIC, "Age");
        return new DataSet(Arrays.asList(row1, row2), Arrays.asList(col1, col2));
    }

    @Test
    void save_createsCsvFileInGivenDirectory() throws IOException {
        DataSet dataSet = createSampleDataSet();
        Path rootDir = tempDir.resolve("datasets");
        FileSaveDataSetDataAccessObject dao = new FileSaveDataSetDataAccessObject(rootDir.toString());
        String datasetId = "my_dataset";
        dao.save(datasetId, dataSet);

        Path expectedFile = rootDir.resolve(datasetId + ".csv");
        assertTrue(Files.exists(expectedFile),
                "Expected CSV file " + expectedFile + " to exist after save()");
    }

    @Test
    void save_writesCorrectHeaderAndRows() throws IOException {
        // Arrange
        DataSet dataSet = createSampleDataSet();
        Path rootDir = tempDir.resolve("datasets");
        FileSaveDataSetDataAccessObject dao = new FileSaveDataSetDataAccessObject(rootDir.toString());
        String datasetId = "my_dataset";

        dao.save(datasetId, dataSet);

        Path expectedFile = rootDir.resolve(datasetId + ".csv");
        assertTrue(Files.exists(expectedFile), "CSV file should exist");
        List<String> lines = Files.readAllLines(expectedFile);
        assertEquals(3, lines.size(), "CSV should have 3 lines (header + 2 rows)");
        assertEquals("Name,Age", lines.get(0));
        assertEquals("Alice,25", lines.get(1));
        assertEquals("Bob,30", lines.get(2));
    }

    @Test
    void save_allowsAbsolutePathSelection() throws IOException {
        DataSet dataSet = createSampleDataSet();
        Path rootDir = tempDir.resolve("datasets");
        Path absoluteFile = tempDir.resolve("custom").resolve("my_dataset.csv");
        FileSaveDataSetDataAccessObject dao = new FileSaveDataSetDataAccessObject(rootDir.toString());

        dao.save(absoluteFile.toString(), dataSet);

        assertTrue(Files.exists(absoluteFile), "CSV should be written to the absolute path when provided");
        List<String> lines = Files.readAllLines(absoluteFile);
        assertEquals("Name,Age", lines.get(0));
    }
}
