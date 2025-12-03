package interface_adapter.load_csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import use_case.load_csv.LoadInputBoundary;
import use_case.load_csv.LoadInputData;

public class LoadController {
    private final LoadInputBoundary csvLoadUseCaseInteractor;

    public LoadController(LoadInputBoundary csvLoadUseCaseInteractor) {
        this.csvLoadUseCaseInteractor = csvLoadUseCaseInteractor;
    }

    /**
     * Executes the use case.
     * @param file the csv file to be read
     */
    public void execute(File file) {
        final List<String> lines = new ArrayList<>();
        boolean failed;
        String errorMessage;

        final String fileName = file.getName();
        final int dotIndex = fileName.lastIndexOf('.');
        String extension = "";
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }

        if ("csv".equals(extension) || "txt".equals(extension)) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                if (lines.isEmpty()) {
                    failed = true;
                    errorMessage = "File is empty";
                }
                else {
                    failed = false;
                    errorMessage = "";
                }
            }
            catch (IOException ex) {
                failed = true;
                errorMessage = ex.getMessage();
            }
        }
        else {
            failed = true;
            errorMessage = "Incorrect file format";
        }

        final LoadInputData loadInputData = new LoadInputData(lines, failed, errorMessage);
        csvLoadUseCaseInteractor.execute(loadInputData);
    }
}
