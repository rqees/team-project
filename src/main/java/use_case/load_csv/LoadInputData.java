package use_case.load_csv;

import java.util.List;

public class LoadInputData {
    private final List<String> lines;
    private final boolean failed;
    private final String errorMessage;

    public LoadInputData(List<String> lines, boolean failed, String errorMessage) {
        this.lines = lines;
        this.failed = failed;
        this.errorMessage = errorMessage;
    }

    List<String> getLines() {
        return lines;
    }

    boolean isFailed() {
        return failed;
    }

    String getErrorMessage() {
        return errorMessage;
    }
}
