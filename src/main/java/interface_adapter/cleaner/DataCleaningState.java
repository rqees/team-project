package interface_adapter.cleaner;

import entity.MissingCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the UI-facing state for Data Cleaning.
 */
public class DataCleaningState {
    private int lastEditedRowIndex = -1;
    private int lastEditedColIndex = -1;
    private String lastCleanedValue;

    private int lastEditedHeaderColIndex = -1;
    private String lastEditedHeaderValue;

    private String headerErrorMessage;

    private List<MissingCell> missingCells = new ArrayList<>();

    public int getLastEditedRowIndex() { return lastEditedRowIndex; }
    public void setLastEditedRowIndex(int i) { this.lastEditedRowIndex = i; }

    public int getLastEditedColIndex() { return lastEditedColIndex; }
    public void setLastEditedColIndex(int i) { this.lastEditedColIndex = i; }

    public String getLastCleanedValue() { return lastCleanedValue; }
    public void setLastCleanedValue(String v) { this.lastCleanedValue = v; }

    public int getLastEditedHeaderColIndex() { return lastEditedHeaderColIndex; }
    public void setLastEditedHeaderColIndex(int i) { this.lastEditedHeaderColIndex = i; }

    public String getLastEditedHeaderValue() { return lastEditedHeaderValue; }
    public void setLastEditedHeaderValue(String s) { this.lastEditedHeaderValue = s; }

    public String getHeaderErrorMessage() { return headerErrorMessage; }
    public void setHeaderErrorMessage(String msg) { this.headerErrorMessage = msg; }

    public List<MissingCell> getMissingCells() { return missingCells; }
    public void setMissingCells(List<MissingCell> list) { this.missingCells = list; }
}

