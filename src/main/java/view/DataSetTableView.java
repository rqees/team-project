package view;

import entity.DataSet;
import entity.DataRow;
import entity.Column;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DataSetTableView extends JFrame {

    private static final String FONT_NAME = "Arial";
    private static final int DEFAULT_COLUMN_WIDTH = 100;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    public DataSetTableView() {
        setTitle("Data Analysis - Table View");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font(FONT_NAME, Font.BOLD, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Dataset View");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        topPanel.add(titleLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel statusLabel = new JLabel("Ready");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void displayDataSet(DataSet dataSet) {
        if (dataSet == null) {
            JOptionPane.showMessageDialog(this,
                    "No dataset to display",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear existing data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        List<Column> columns = dataSet.getColumns();
        List<DataRow> rows = dataSet.getRows();

        if (columns.isEmpty() || rows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Dataset is empty",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Set column headers (using generic names)
        int numColumns = columns.size();
        for (int i = 0; i < numColumns; i++) {
            tableModel.addColumn("Column " + (i + 1));
        }

        // Add rows
        for (DataRow row : rows) {
            List<String> cells = row.getCells();
            tableModel.addRow(cells.toArray(new String[0]));
        }

        // Auto-resize columns to fit content
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(DEFAULT_COLUMN_WIDTH);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and show the GUI with sample data
            DataSetTableView view = new DataSetTableView();

            // Create sample data
            java.util.List<DataRow> rows = java.util.Arrays.asList(
                    new DataRow(java.util.Arrays.asList("John", "25", "Engineer", "New York")),
                    new DataRow(java.util.Arrays.asList("Jane", "30", "Doctor", "Boston")),
                    new DataRow(java.util.Arrays.asList("Bob", "28", "Teacher", "Chicago")),
                    new DataRow(java.util.Arrays.asList("Alice", "32", "Designer", "Seattle")),
                    new DataRow(java.util.Arrays.asList("Charlie", "27", "Developer", "Austin"))
            );

            java.util.List<Column> columns = java.util.Arrays.asList(
                    new Column(java.util.Arrays.asList("John", "Jane", "Bob", "Alice", "Charlie")),
                    new Column(java.util.Arrays.asList("25", "30", "28", "32", "27")),
                    new Column(java.util.Arrays.asList("Engineer", "Doctor", "Teacher", "Designer", "Developer")),
                    new Column(java.util.Arrays.asList("New York", "Boston", "Chicago", "Seattle", "Austin"))
            );

            DataSet sampleData = new DataSet(rows, columns);

            view.setVisible(true);
            view.displayDataSet(sampleData);
        });
    }
}