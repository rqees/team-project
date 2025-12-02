package use_case.load_api;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import entity.DataType;
import use_case.dataset.CurrentTableGateway;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadAPIInteractor implements LoadAPIInputBoundary {
    private final LoadAPIOutputBoundary loadAPIPresenter;
    private final LoadAPIDataGateway loadAPIDataGateway;
    private final CurrentTableGateway tableGateway;

    public LoadAPIInteractor(LoadAPIOutputBoundary loadAPIPresenter,
                             LoadAPIDataGateway loadAPIDataGateway,
                             CurrentTableGateway tableGateway) {
        this.loadAPIPresenter = loadAPIPresenter;
        this.loadAPIDataGateway = loadAPIDataGateway;
        this.tableGateway = tableGateway;
    }

    @Override
    public void execute(LoadAPIInputData loadAPIInputData){
        String csv = loadAPIDataGateway.getCSV(loadAPIInputData.getDatasetName());
        if (csv.equals("Dataset not found.") || csv.equals("Dataset found, but no CSV resource available.") || csv.startsWith("Error: ")) {
            loadAPIPresenter.prepareFail(csv);
        }
        else {
            List<String> lines = new ArrayList<>(List.of(csv.split("\n", -1)));
            List<Column> columns = getColumns(lines);
            List<DataRow> rows = getRows(lines);
            DataSet table = new DataSet(rows, columns);

            tableGateway.save(table);
            loadAPIPresenter.prepareSuccess();
        }
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

        String[] headers = lines.get(0).split(",", -1);
        lines.remove(0);

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
        for (int i = 0; i < headers.length; i++){
            DataType type = guessDataType(columnCells.get(i));
            columns.add(new Column(columnCells.get(i), type, headers[i]));
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

    private static List<DataRow> getRows(List<String> lines) {
        List<DataRow> rows = new ArrayList<>();
        for (String line : lines) {
            List<String> cells = Arrays.asList(line.split(",", -1));
            rows.add(new DataRow(cells));
        }
        return rows;
    }
}