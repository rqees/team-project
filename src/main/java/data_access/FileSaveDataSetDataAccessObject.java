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
public final class FileSaveDataSetDataAccessObject implements SaveDataSetDataAccessInterface {

    /**
     * Root directory where datasets are stored.
     */
    private final File rootDir;

    public FileSaveDataSetDataAccessObject(final String rootDirPath) {
        this.rootDir = new File(rootDirPath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    private File fileFor(final String id) {
        File target = new File(id);

        if (!target.isAbsolute()) {
            target = new File(rootDir, id);
        }

        if (!target.getName().toLowerCase().endsWith(".csv")) {
            target = new File(target.getParentFile(), target.getName() + ".csv");
        }

        final File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        return target;
    }

    @Override
    public void save(final String id, final DataSet dataSet) throws IOException {
        final File file = fileFor(id);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            final List<String> headers = new ArrayList<>();
            for (final Column col : dataSet.getColumns()) {
                headers.add(col.getHeader());
            }
            writer.write(String.join(",", headers));
            writer.newLine();

            for (final DataRow row : dataSet.getRows()) {
                writer.write(String.join(",", row.getCells()));
                writer.newLine();
            }
        }
    }
}
