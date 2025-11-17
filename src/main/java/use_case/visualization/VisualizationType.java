package use_case.visualization;

/**
 * Types of visualizations that the platform can support.
 * This is an application-specific concept (use case layer),
 * not tied to any particular charting library.
 */
public enum VisualizationType {
    HISTOGRAM,
    SCATTER,
    BAR
    // You can add HEATMAP, LINE, etc. later as needed.
}
