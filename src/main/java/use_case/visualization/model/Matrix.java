package use_case.visualization.model;

import java.util.List;

public class Matrix {
    private final double[][] values;
    private final List<String> rowLabels;
    private final List<String> colLabels;

    public Matrix(double[][] values, List<String> rowLabels, List<String> colLabels) {
        this.values = values;
        this.rowLabels = rowLabels;
        this.colLabels = colLabels;
    }

    public double[][] getValues() { return values; }
    public List<String> getRowLabels() { return rowLabels; }
    public List<String> getColLabels() { return colLabels; }
}
