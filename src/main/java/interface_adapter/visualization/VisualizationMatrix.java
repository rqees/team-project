package interface_adapter.visualization;

import java.util.List;

/**
 * Matrix data structure for heatmap visualization.
 * Moved to interface_adapter layer to avoid use_case dependencies in VisualizationState.
 */
public class VisualizationMatrix {
    private final double[][] values;
    private final List<String> rowLabels;
    private final List<String> colLabels;

    public VisualizationMatrix(double[][] values, List<String> rowLabels, List<String> colLabels) {
        this.values = values;
        this.rowLabels = rowLabels;
        this.colLabels = colLabels;
    }

    public double[][] getValues() { return values; }
    public List<String> getRowLabels() { return rowLabels; }
    public List<String> getColLabels() { return colLabels; }
}

