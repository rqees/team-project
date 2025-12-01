package use_case.visualization.model;

public class Annotation {

    public enum AnnotationType {
        POINT, LINE_HORIZONTAL, LINE_VERTICAL
    }

    private final AnnotationType type;
    private final double x;
    private final double y;
    private final String label;

    public Annotation(AnnotationType type, double x, double y, String label) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public AnnotationType getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getLabel() { return label; }
}
