package view;

import entity.DataSet;
import entity.DataRow;
import entity.Column;
import entity.DataType;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import use_case.cleaner.DataCleaningController;

public class DataSetTableView extends JFrame {

    private static final String FONT_NAME = "Arial";
    private static final int DEFAULT_COLUMN_WIDTH = 100;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private DataSet dataSet;
    private DataCleaningController controller;
    private boolean updatingFromCleaner = false;

    public DataSetTableView() {
        setTitle("Data Analysis - Table View");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
        attachTableListener();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // Make table editable
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

    /**
     * TableModelListener that forwards user edits to the controller.
     */
    private void attachTableListener() {
        tableModel.addTableModelListener(e -> {
            // Ignore events while we are programmatically updating the table
            if (updatingFromCleaner) {
                return;
            }


            if (e.getType() != TableModelEvent.UPDATE) {
                return;
            }


            int row = e.getFirstRow();
            int column = e.getColumn();


            // Sometimes UPDATE can be for all columns (-1)
            // only care about single cell edit
            if (row < 0 || column < 0) {
                return;
            }

            // if there is no controller wired up yet
            if (controller == null) {
                return;
            }


            Object value = tableModel.getValueAt(row, column);
            String rawValue = (value == null) ? null : value.toString();

            controller.handleUserEdit(row, column, rawValue);
        });
    }

    /**
     * Update the displayed column header in the JTable after a successful
     * header edit in the underlying DataSet.
     *
     * @param colIndex  the index of the column whose header should change
     * @param newHeader the new column header text to display
     */
    public void updateColumnHeader(int colIndex, String newHeader) {
        table.getColumnModel().getColumn(colIndex).setHeaderValue(newHeader);

        // Force the header to repaint so the change appears immediately
        table.getTableHeader().repaint();
    }

    public void displayDataSet(DataSet dataSet) {
        this.dataSet = dataSet;

        if (dataSet == null) {
            JOptionPane.showMessageDialog(this,
                    "No dataset to display",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        // change this to use actual header name
        for (Column column : columns) {
            tableModel.addColumn(column.getHeader());
        }

        for (DataRow row : rows) {
            List<String> cells = row.getCells();
            tableModel.addRow(cells.toArray(new String[0]));
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(DEFAULT_COLUMN_WIDTH);
        }
    }

    // helper for dataCleaningController
    public void setUpdatingFromCleaner(boolean updatingFromCleaner) {
        this.updatingFromCleaner = updatingFromCleaner;
    }

    public void updateCellFromCleaner(int rowIndex, int colIndex, String cleanedValue) {
        tableModel.setValueAt(cleanedValue, rowIndex, colIndex);
    }

    public void refreshFromDataSet(DataSet dataSet) {
        displayDataSet(dataSet);
    }

    public void setController(DataCleaningController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            DataSetTableView view = new DataSetTableView();

            java.util.List<DataRow> rows = java.util.Arrays.asList(
                    new DataRow(java.util.Arrays.asList("John", "25", "Engineer", "New York")),
                    new DataRow(java.util.Arrays.asList("Jane", "30", "Doctor", "Boston")),
                    new DataRow(java.util.Arrays.asList("Bob", "28", "Teacher", "Chicago")),
                    new DataRow(java.util.Arrays.asList("Alice", "32", "Designer", "Seattle")),
                    new DataRow(java.util.Arrays.asList("Charlie", "27", "Developer", "Austin"))
            );

            java.util.List<Column> columns = java.util.Arrays.asList(
                    new Column(java.util.Arrays.asList("John", "Jane", "Bob", "Alice", "Charlie"), DataType.CATEGORICAL, "Names"),
                    new Column(java.util.Arrays.asList("25", "30", "28", "32", "27"), DataType.NUMERIC, "Age"),
                    new Column(java.util.Arrays.asList("Engineer", "Doctor", "Teacher", "Designer", "Developer"), DataType.CATEGORICAL, "Occupation"),
                    new Column(java.util.Arrays.asList("New York", "Boston", "Chicago", "Seattle", "Austin"), DataType.CATEGORICAL, "Location")
            );

            DataSet sampleData = new DataSet(rows, columns);

            view.setVisible(true);
            view.displayDataSet(sampleData);
        });
    }
}