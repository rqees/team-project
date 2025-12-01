package interface_adapter.load_csv;

import use_case.load_csv.LoadInputBoundary;
import use_case.load_csv.LoadInputData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadController {
    private final LoadInputBoundary csvLoadUseCaseInteractor;

    public LoadController(LoadInputBoundary csvLoadUseCaseInteractor) {
        this.csvLoadUseCaseInteractor = csvLoadUseCaseInteractor;
    }

    public void execute(File file) {
        List<String> lines = new ArrayList<>();
        boolean failed;
        String errorMessage;

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String extension = "";
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1);
        }

        if (extension.equals("csv") || extension.equals("txt")) {
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
            } catch (IOException ex) {
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
