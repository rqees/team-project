package data_access;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import use_case.save_dataset.SaveDataSetDataAccessInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves a DataSet as a CSV file.
 */
public class FileSaveDataSetDataAccessObject implements SaveDataSetDataAccessInterface {

    private final File rootDir;

    public FileSaveDataSetDataAccessObject(String rootDirPath) {
        this.rootDir = new File(rootDirPath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    private File fileFor(String id) {
        return new File(rootDir, id + ".csv");
    }

    @Override
    public void save(String id, DataSet dataSet) {
        File file = fileFor(id);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<String> headers = new ArrayList<>();
            for (Column col : dataSet.getColumns()) {
                headers.add(col.getHeader());
            }
            writer.write(String.join(",", headers));
            writer.newLine();

            for (DataRow row : dataSet.getRows()) {
                writer.write(String.join(",", row.getCells()));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving dataset '" + id + "'", e);
        }
    }
}
