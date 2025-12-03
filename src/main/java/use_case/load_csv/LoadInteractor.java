package use_case.load_csv;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import use_case.dataset.CurrentTableGateway;

public class LoadInteractor implements LoadInputBoundary {
    private final LoadOutputBoundary loadPresenter;
    private final CurrentTableGateway tableGateway;

    public LoadInteractor(LoadOutputBoundary loadPresenter, CurrentTableGateway tableGateway) {
        this.loadPresenter = loadPresenter;
        this.tableGateway = tableGateway;
    }

    @Override
    public void execute(LoadInputData loadInputData) {
        if (loadInputData.isFailed()) {
            loadPresenter.prepareFail(loadInputData.getErrorMessage());
        }
        else {
            final List<String> lines = loadInputData.getLines();
            final List<Column> columns = getColumns(lines);
            final List<DataRow> rows = getRows(lines);
            final DataSet table = new DataSet(rows, columns);

            tableGateway.save(table);
            loadPresenter.prepareSuccess();
        }
    }

    private static List<Column> getColumns(List<String> lines) {
        int columnCount = 0;
        final String delimiter = ",";
        for (String line : lines) {
            final String[] cells = line.split(delimiter, -1);
            if (cells.length > columnCount) {
                columnCount = cells.length;
            }
        }

        final List<List<String>> columnCells = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            columnCells.add(new ArrayList<>());
        }

        final String[] headers = lines.get(0).split(delimiter, -1);
        lines.remove(0);

        for (String line : lines) {
            final String[] cells = line.split(delimiter, -1);
            for (int i = 0; i < columnCount; i++) {
                if (i < cells.length) {
                    columnCells.get(i).add(cells[i]);
                }
                else {
                    columnCells.get(i).add("");
                }
            }
        }

        // Create Column objects with guessed datatype
        final List<Column> columns = new ArrayList<>();
        for (int i = 0; i < columnCells.size(); i++) {
            final DataType type = guessDataType(columnCells.get(i));
            columns.add(new Column(columnCells.get(i), type, headers[i]));
        }
        return columns;
    }

    private static DataType guessDataType(List<String> cells) {
        int numNumeric = 0;
        int numBoolean = 0;
        int numDate = 0;
        int numCategorical = 0;

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String cell : cells) {
            final String value = cell.trim();

            if (value.matches("-?\\d+(\\.\\d+)?")) {
                numNumeric += 1;
            }

            else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                numBoolean += 1;
            }

            else if (!value.isEmpty()) {
                try {
                    LocalDate.parse(value, dateFormatter);
                    numDate += 1;
                }
                catch (DateTimeParseException ignored) {
                    numCategorical += 1;
                }
            }
        }

        if (numNumeric >= numBoolean && numNumeric >= numDate && numNumeric >= numCategorical) {
            return DataType.NUMERIC;
        }
        if (numBoolean >= numDate && numBoolean >= numCategorical) {
            return DataType.BOOLEAN;
        }
        if (numDate >= numCategorical) {
            return DataType.DATE;
        }
        return DataType.CATEGORICAL;
    }

    private static List<DataRow> getRows(List<String> lines) {
        final List<DataRow> rows = new ArrayList<>();
        for (String line : lines) {
            final List<String> cells = Arrays.asList(line.split(",", -1));
            rows.add(new DataRow(cells));
        }
        return rows;
    }
}
