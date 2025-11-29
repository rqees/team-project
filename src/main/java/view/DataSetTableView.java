package view;

import entity.Column;
import entity.DataRow;
import entity.DataSet;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchState;
import interface_adapter.search.SearchViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Main table view for the Data Analysis Program.
 * Implements Use Case 2: Table Format Display
 */
public class DataSetTableView extends JPanel implements PropertyChangeListener {

    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 24;
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final String FONT_NAME = "Arial";
    private static final int DEFAULT_COLUMN_WIDTH = 120;

    private final String viewName = "dataset table";

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private DataSet currentDataSet;

    private JTextField searchField;
    private JButton searchButton;
    private JSlider zoomSlider;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JLabel zoomLabel;
    private int currentFontSize = DEFAULT_FONT_SIZE;

    private SearchController searchController;
    private final SearchViewModel searchViewModel;

    public DataSetTableView(SearchViewModel searchViewModel) {
        this.searchViewModel = searchViewModel;
        this.searchViewModel.addPropertyChangeListener(this);

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only for now
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font(FONT_NAME, Font.PLAIN, currentFontSize));
        dataTable.setRowHeight(currentFontSize + 16);
        dataTable.getTableHeader().setFont(new Font(FONT_NAME, Font.BOLD, currentFontSize + 1));
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setGridColor(new Color(230, 230, 230));

        tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        searchField = new JTextField(20);
        searchField.setFont(new Font(FONT_NAME, Font.PLAIN, 12));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        searchButton.setFocusPainted(false);

        zoomSlider = new JSlider(MIN_FONT_SIZE, MAX_FONT_SIZE, DEFAULT_FONT_SIZE);
        zoomSlider.setPreferredSize(new Dimension(150, 25));
        zoomSlider.setMajorTickSpacing(4);
        zoomSlider.setMinorTickSpacing(2);
        zoomSlider.setPaintTicks(true);

        zoomInButton = new JButton("+");
        zoomInButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        zoomInButton.setFocusPainted(false);
        zoomInButton.setPreferredSize(new Dimension(45, 25));

        zoomOutButton = new JButton("-");
        zoomOutButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        zoomOutButton.setFocusPainted(false);
        zoomOutButton.setPreferredSize(new Dimension(45, 25));

        zoomLabel = new JLabel("100%");
        zoomLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        zoomLabel.setPreferredSize(new Dimension(45, 25));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - Title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        topPanel.setBackground(new Color(240, 240, 245));

        JLabel titleLabel = new JLabel("Data Analysis Platform");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 80));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(240, 240, 245));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // Center panel - Table with border
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        bottomPanel.setBackground(new Color(240, 240, 245));

        // Zoom controls panel (bottom right)
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        zoomPanel.setBackground(new Color(240, 240, 245));
        zoomPanel.add(new JLabel("Zoom: "));
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(zoomSlider);
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomLabel);

        bottomPanel.add(zoomPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        zoomSlider.addChangeListener(e -> {
            if (!zoomSlider.getValueIsAdjusting()) {
                currentFontSize = zoomSlider.getValue();
                updateTableZoom();
            }
        });

        zoomInButton.addActionListener(e -> {
            if (currentFontSize < MAX_FONT_SIZE) {
                currentFontSize++;
                zoomSlider.setValue(currentFontSize);
                updateTableZoom();
            }
        });

        zoomOutButton.addActionListener(e -> {
            if (currentFontSize > MIN_FONT_SIZE) {
                currentFontSize--;
                zoomSlider.setValue(currentFontSize);
                updateTableZoom();
            }
        });
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();

        // Get current table data
        int rowCount = dataTable.getRowCount();
        int colCount = dataTable.getColumnCount();
        String[][] tableData = new String[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                Object value = dataTable.getValueAt(i, j);
                tableData[i][j] = value != null ? value.toString() : "";
            }
        }

        // Get current selection to start search from there
        int startRow = dataTable.getSelectedRow();
        int startCol = dataTable.getSelectedColumn();

        if (startRow == -1) startRow = 0;
        if (startCol == -1) startCol = -1;

        // Delegate to controller
        searchController.execute(searchTerm, tableData, startRow, startCol);
    }


    private void updateTableZoom() {
        dataTable.setFont(new Font(FONT_NAME, Font.PLAIN, currentFontSize));
        dataTable.setRowHeight(currentFontSize + 16);
        dataTable.getTableHeader().setFont(new Font(FONT_NAME, Font.BOLD, currentFontSize + 1));

        int scaledWidth = (int) (DEFAULT_COLUMN_WIDTH * (currentFontSize / (double) DEFAULT_FONT_SIZE));
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(scaledWidth);
        }

        int zoomPercentage = (int) ((currentFontSize / (double) DEFAULT_FONT_SIZE) * 100);
        zoomLabel.setText(zoomPercentage + "%");

        dataTable.revalidate();
        dataTable.repaint();
    }

    /**
     * Display a DataSet in the table format.
     */
    public void displayDataSet(DataSet dataSet) {
        if (dataSet == null) {
            JOptionPane.showMessageDialog(this,
                    "No dataset to display",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.currentDataSet = dataSet;
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

        for (Column column : columns) {
            tableModel.addColumn(column.getHeader());
        }

        for (DataRow row : rows) {
            List<String> cells = row.getCells();
            tableModel.addRow(cells.toArray(new String[0]));
        }

        int scaledWidth = (int) (DEFAULT_COLUMN_WIDTH * (currentFontSize / (double) DEFAULT_FONT_SIZE));
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(scaledWidth);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final SearchState state = (SearchState) evt.getNewValue();

            if (state.isFound()) {
                // Highlight the found cell
                int row = state.getRow();
                int col = state.getColumn();

                dataTable.setRowSelectionInterval(row, row);
                dataTable.setColumnSelectionInterval(col, col);
                dataTable.scrollRectToVisible(dataTable.getCellRect(row, col, true));
            } else if (state.getErrorMessage() != null) {
                // Show error message
                JOptionPane.showMessageDialog(this,
                        state.getErrorMessage(),
                        "Search",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }
}