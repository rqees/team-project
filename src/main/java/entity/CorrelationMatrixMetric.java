package entity;

import java.util.List;

public class CorrelationMatrixMetric implements SummaryMetric {
    private final MetricType metric_type;
    private final DataSubsetSpec selected_subset;
    private final double[][] correlationMatrix;
    private final List<String> variableNames;

    public CorrelationMatrixMetric(MetricType metricType, DataSubsetSpec selectedSubset, double[][] correlationMatrix, List<String> variableNames) {
        metric_type = metricType;
        selected_subset = selectedSubset;
        this.correlationMatrix = correlationMatrix;
        this.variableNames = variableNames;
    }


    @Override
    public MetricType getMetricType() {
        return metric_type;
    }

    @Override
    public DataSubsetSpec getSelected_subset() {
        return selected_subset;
    }

    public double[][] getCorrelationMatrix() {
        return correlationMatrix;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }
}
