package entity;

import java.util.List;

public class CorrelationMatrixMetric implements SummaryMetric {
    private final MetricType metricType;
    private final DataSubsetSpec selectedSubset;
    private final double[][] correlationMatrix;
    private final List<String> variableNames;

    public CorrelationMatrixMetric(MetricType metricType, DataSubsetSpec selectedSubset, double[][] correlationMatrix, List<String> variableNames) {
        this.metricType = metricType;
        this.selectedSubset = selectedSubset;
        this.correlationMatrix = correlationMatrix;
        this.variableNames = variableNames;
    }


    @Override
    public MetricType getMetricType() {
        return metricType;
    }

    @Override
    public DataSubsetSpec getSelectedSubset() {
        return selectedSubset;
    }

    public double[][] getCorrelationMatrix() {
        return correlationMatrix;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }
}
