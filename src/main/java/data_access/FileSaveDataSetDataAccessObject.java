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
        File target = new File(id);

        if (!target.isAbsolute()) {
            target = new File(rootDir, id);
        }

        if (!target.getName().toLowerCase().endsWith(".csv")) {
            target = new File(target.getParentFile(), target.getName() + ".csv");
        }

        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        return target;
    }

    @Override
    public void save(String id, DataSet dataSet) throws IOException {
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
        }
    }
}
