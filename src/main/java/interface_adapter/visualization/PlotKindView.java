package interface_adapter.visualization;

/**
 * View-layer enum for plot types.
 * This is the public-facing type used by views and view models.
 * The controller maps this to use_case.visualization.model.PlotKind internally.
 */
public enum PlotKindView {
    SCATTER,
    LINE,
    BAR,
    HISTOGRAM,
    HEATMAP
}

