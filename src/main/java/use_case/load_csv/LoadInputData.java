package use_case.load_csv;

import java.util.List;

public class LoadInputData {
    private final List<String> lines;
    private final boolean failed;

    public LoadInputData(List<String> lines, boolean failed) {
        this.lines = lines;
        this.failed = failed;
    }

    List<String> getLines() {
        return lines;
    }

    boolean isFailed() {
        return failed;
    }
}
