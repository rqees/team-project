    package use_case.visualization.data;


    import java.util.List;
    import java.util.Map;

    public class DataSubsetData {
        // column name -> list of numeric values
        private final Map<String, List<Double>> numericColumns;
        // column name -> list of categorical values (as strings)
        private final Map<String, List<String>> categoricalColumns;

        public DataSubsetData(Map<String, List<Double>> numericColumns) {
            this(numericColumns, Map.of());
        }
        
        public DataSubsetData(Map<String, List<Double>> numericColumns,
                             Map<String, List<String>> categoricalColumns) {
            this.numericColumns = numericColumns;
            this.categoricalColumns = categoricalColumns;
        }

        public Map<String, List<Double>> getNumericColumns() {
            return numericColumns;
        }
        
        public Map<String, List<String>> getCategoricalColumns() {
            return categoricalColumns;
        }
    }