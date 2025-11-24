package data_access;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import use_case.dataset.DataSetDataAccessInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File-based implementation of DataSetDataAccessInterface.
 * Each DataSet is stored as a CSV file: <id>.csv in a root directory.
 * CSV format:
 *   - First line: column headers
 *   - Remaining lines: data rows, comma-separated cells
 */
public class FileDataAccessObject implements DataSetDataAccessInterface {
    private final File rootDir;

    /**
     * @param rootDirPath path to the directory where dataset CSV files will be stored
     */
    public FileDataAccessObject(String rootDirPath) {
        this.rootDir = new File(rootDirPath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    /**
     * Helper: build the File path for a given dataset id.
     * e.g., id = "project1" -> data/datasets/project1.csv
     */
    private File fileFor(String id) {
        return new File(rootDir, id + ".csv");
    }

    @Override
    public void saveDataSet(String id, DataSet dataSet) {
        File file = fileFor(id);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            // 1) Header row
            List<Column> columns = dataSet.getColumns();
            List<String> headers = new ArrayList<>();
            for (Column col : columns) {
                headers.add(col.getHeader());
            }
            String headerLine = String.join(",", headers);
            writer.write(headerLine);
            writer.newLine();

            // 2) Write each data row as comma-separated cells
            for (DataRow row : dataSet.getRows()) {
                List<String> cells = row.getCells();
                String line = String.join(",", cells); // commas between cells, no leading comma
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save DataSet with id " + id, e);
        }
    }

    @Override
    public DataSet loadDataSet(String id) {
        File file = fileFor(id);

        if (!file.exists()) {
            throw new RuntimeException("DataSet file not found for id " + id);
        }

        List<String> headerTokens = new ArrayList<>();
        List<DataRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            // Read header
            if (line == null) {
                throw new RuntimeException("Empty dataset file for id " + id);
            } else {
                // First line has headers
                String[] headerArray = line.split(",", -1);
                Collections.addAll(headerTokens, headerArray);
            }


            // Remaining lines have data rows
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                List<String> cells = new ArrayList<>();
                Collections.addAll(cells, tokens);
                rows.add(new DataRow(cells));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load DataSet with id " + id, e);
        }
//
//        // Build columns from rows and infer DataType  ASK GROUP ABOUT THIS!!!
//        List<Column> columns = new ArrayList<>();
//
//        if (!rows.isEmpty()) {
//            int numCols = rows.get(0).getCells().size();
//
//            for (int c = 0; c < numCols; c++) {
//                List<String> columnCells = new ArrayList<>();
//                for (DataRow row : rows) {
//                    columnCells.add(row.getCells().get(c));
//                }
//
//                String header = (c < headerTokens.size()) ? headerTokens.get(c) : "";
//
//                DataType dataType = inferDataType(columnCells);
//
//                columns.add(new Column(columnCells, dataType, header));
//            }
//        }
//
//        return new DataSet(rows, columns);
    }

    @Override
    public boolean exists(String id) {
        File file = fileFor(id);
        return file.exists();
    }

//
//    Group has not decided on DataType inferring + transpose is someone elses role, therefore, AI generated this helper
//    as a placeholder just so the "logic" is there and to remind me to discuss this in lab.
//
//    /**
//     * Very simple heuristic:
//     * - If all non-empty cells parse as "true"/"false" → BOOLEAN
//     * - Else if all non-empty cells parse as Double → NUMERIC
//     * - Otherwise → CATEGORICAL
//     * (DATE detection can be added later if needed.)
//     */
//    private DataType inferDataType(List<String> columnCells) {
//        boolean allBoolean = true;
//        boolean allNumeric = true;
//
//        for (String cell : columnCells) {
//            if (cell == null || cell.isBlank()) {
//                continue; // ignore missing cells
//            }
//
//            String lower = cell.toLowerCase();
//            if (!(lower.equals("true") || lower.equals("false"))) {
//                allBoolean = false;
//            }
//
//            try {
//                Double.parseDouble(cell);
//            } catch (NumberFormatException e) {
//                allNumeric = false;
//            }
//        }
//
//        if (allBoolean) {
//            return DataType.BOOLEAN;
//        }
//        if (allNumeric) {
//            return DataType.NUMERIC;
//        }
//        // DATE detection could go here later if needed
//        return DataType.CATEGORICAL;
//    }
}
