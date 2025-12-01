package entity;

public class OutlierPoint {

    private final int rowIndex;
    private final int colIndex;
    private final double zScore;

    public OutlierPoint(int rowIndex, int colIndex, double zScore) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.zScore = zScore;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public double getZScore() {
        return zScore;
    }
}

