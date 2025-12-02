package use_case.visualization.model;

import java.util.Map;

public class DataPoint {
    private final double x;
    private final double y;
    private final Map<String, Object> meta;

    public DataPoint(double x, double y, Map<String, Object> meta) {
        this.x = x;
        this.y = y;
        this.meta = meta;  // e.g. row index, label
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Map<String, Object> getMeta() { return meta; }
}
