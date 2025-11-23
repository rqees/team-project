package use_case.load_csv;

import entity.Column;
import entity.DataRow;
import entity.DataType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadInteractor implements LoadInputBoundary {
    private final LoadOutputBoundary loadPresenter;

    public LoadInteractor(LoadOutputBoundary loadPresenter) {
        this.loadPresenter = loadPresenter;
    }

    @Override
    public void execute(LoadInputData loadInputData) {
        if (loadInputData.isFailed()) {

        }
        else {
            List<String> lines = loadInputData.getLines();
            List<DataRow> rows = getRows(lines);
            List<Column> columns = getColumns(lines);
        }
    }

    private static List<DataRow> getRows(List<String> lines) {
        List<DataRow> rows = new ArrayList<>();
        for (String line : lines) {
            List<String> cells = Arrays.asList(line.split(",", -1));
            rows.add(new DataRow(cells));
        }
        return rows;
    }

    private static List<Column> getColumns(List<String> lines) {
        int columnCount = 0;
        for (String line : lines) {
            String[] cells = line.split(",", -1);
            if (cells.length > columnCount) {
                columnCount = cells.length;
            }
        }

        List<List<String>> columnCells = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            columnCells.add(new ArrayList<>());
        }

        for (String line : lines) {
            String[] cells = line.split(",", -1);
            for (int i = 0; i < columnCount; i++) {
                if (i < cells.length) {
                    columnCells.get(i).add(cells[i]);
                } else {
                    columnCells.get(i).add("");
                }
            }
        }

        // Create Column objects with guessed datatype
        List<Column> columns = new ArrayList<>();
        for (List<String> cells : columnCells) {
            DataType type = guessDataType(cells);
            columns.add(new Column(cells, type));
        }
        return columns;
    }

    private static DataType guessDataType(List<String> cells) {
        int numNumeric = 0;
        int numBoolean = 0;
        int numDate = 0;
        int numCategorical = 0;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String cell : cells) {
            String value = cell.trim();

            if (value.matches("-?\\d+(\\.\\d+)?")) {
                numNumeric += 1;
            }

            else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                numBoolean += 1;
            }

            else if (!value.isEmpty()) {
                try {
                    LocalDate.parse(value, dateFormatter);
                    numDate += 1;
                } catch (DateTimeParseException ignored) {}
            }

            else {
                numCategorical += 1;
            }
        }

        if (numNumeric >= numBoolean && numNumeric >= numDate && numNumeric >= numCategorical) {
            return DataType.NUMERIC;
        }
        if (numBoolean >= numDate &&  numBoolean >= numCategorical) {
            return DataType.BOOLEAN;
        }
        if (numDate >= numCategorical) {
            return DataType.DATE;
        }
        return DataType.CATEGORICAL;
    }
}
