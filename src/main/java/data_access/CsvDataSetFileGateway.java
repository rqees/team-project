package data_access;

import entity.DataSet;
import entity.Column;
import entity.DataRow;
import use_case.dataset.DataSetGateway;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvDataSetFileGateway implements use_case.dataset.DataSetGateway {

    private final File rootDir;

    public CsvDataSetFileGateway(String rootDirPath) {
        this.rootDir = new File(rootDirPath);

        if (!this.rootDir.exists()) {
            this.rootDir.mkdir();
        }
    }

    //file path for the csv file.
    private File fileFor(String id) {
        return new File(rootDir, id + ".csv");
    }

    @Override
    // ask about headings of CSV storage in DataSet entity
    public void save(String id, DataSet dataSet) {
        File file = fileFor(id);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (DataRow row : dataSet.getRows()) {
                List<String> cells = row.getCells();
                String line = String.join(",", cells);
                writer.write(line);
                writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save DataSet with id" + id, e);
        }
    }

    @Override
    public DataSet load(String id) {
        return null;
    }

    @Override
    public boolean exists(String id) {
        return false;
    }
}
