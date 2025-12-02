    package use_case.visualization.data;


    import java.util.List;
    import java.util.Map;

    public class DataSubsetData {
        // column name -> list of numeric values
        private final Map<String, List<Double>> numericColumns;

        public DataSubsetData(Map<String, List<Double>> numericColumns) {
            this.numericColumns = numericColumns;
        }

        public Map<String, List<Double>> getNumericColumns() {
            return numericColumns;
        }
    }