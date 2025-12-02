package interface_adapter.visualization;

import java.util.List;

/**
 * Helper class containing visualization validation rules.
 * These rules determine which plot types are valid for given column selections,
 * and what column types are required for each plot type.
 */
public class VisualizationRules {

    /**
     * Check if a plot type is enabled for the given number of selected columns.
     * 
     * @param plotKind The plot type to check
     * @param numSelectedColumns Number of columns selected
     * @return true if the plot type is valid for this number of columns
     */
    public static boolean isPlotTypeEnabled(PlotKindView plotKind, int numSelectedColumns) {
        // Heatmap requires at least 2 columns
        if (plotKind == PlotKindView.HEATMAP) {
            return numSelectedColumns >= 2;
        }
        // Other plot types can handle 1+ columns
        return numSelectedColumns >= 1;
    }

    /**
     * Check if a plot type requires a categorical X-axis.
     * 
     * @param plotKind The plot type to check
     * @return true if the plot requires a categorical X-axis
     */
    public static boolean requiresCategoricalXAxis(PlotKindView plotKind) {
        return plotKind == PlotKindView.BAR;
    }

    /**
     * Check if a plot type requires a numeric X-axis.
     * 
     * @param plotKind The plot type to check
     * @return true if the plot requires a numeric X-axis
     */
    public static boolean requiresNumericXAxis(PlotKindView plotKind) {
        return plotKind == PlotKindView.SCATTER 
            || plotKind == PlotKindView.LINE 
            || plotKind == PlotKindView.HISTOGRAM
            || plotKind == PlotKindView.HEATMAP;
    }

    /**
     * Get the list of valid X-axis column names for a plot type.
     * Filters the provided column names based on plot type requirements.
     * 
     * @param plotKind The plot type
     * @param numericColumns List of numeric column names
     * @param categoricalColumns List of categorical column names
     * @return List of valid X-axis column names for this plot type
     */
    public static List<String> getValidXAxisColumns(PlotKindView plotKind, 
                                                    List<String> numericColumns,
                                                    List<String> categoricalColumns) {
        if (requiresCategoricalXAxis(plotKind)) {
            return categoricalColumns;
        } else if (requiresNumericXAxis(plotKind)) {
            return numericColumns;
        }
        // Default: return all columns (shouldn't happen with current plot types)
        return numericColumns;
    }

    /**
     * Check if a visualization configuration is valid.
     * 
     * @param plotKind The plot type
     * @param numSelectedColumns Number of selected columns
     * @param xAxisColumn Selected X-axis column name (can be null)
     * @param yColumns List of selected Y-axis column names
     * @return true if the configuration is valid
     */
    public static boolean isValidConfiguration(PlotKindView plotKind,
                                             int numSelectedColumns,
                                             String xAxisColumn,
                                             List<String> yColumns) {
        // Check plot type is enabled for number of columns
        if (!isPlotTypeEnabled(plotKind, numSelectedColumns)) {
            return false;
        }

        // Check X-axis is selected
        if (xAxisColumn == null || xAxisColumn.equals("(Select column)")) {
            return false;
        }

        // Check at least one Y-axis is selected
        if (yColumns == null || yColumns.isEmpty()) {
            return false;
        }

        // Heatmap requires at least 2 Y-axis columns
        if (plotKind == PlotKindView.HEATMAP && yColumns.size() < 2) {
            return false;
        }

        return true;
    }
}

