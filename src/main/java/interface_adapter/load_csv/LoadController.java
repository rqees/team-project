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
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            failed = false;
        } catch (IOException ex) {
            failed = true;
        }

        final LoadInputData loadInputData = new LoadInputData(lines, failed);
        csvLoadUseCaseInteractor.execute(loadInputData);
    }
}
