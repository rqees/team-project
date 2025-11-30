package view;

import interface_adapter.search.SearchController;
import interface_adapter.search.SearchState;
import interface_adapter.search.SearchViewModel;
import interface_adapter.table.TableController;
import interface_adapter.table.TableState;
import interface_adapter.table.TableViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private JMenuBar menuBar;
    private JMenu importMenu;
    private JMenu saveMenu;
    private JMenu visualizationMenu;
    private JPanel statsPanel;
    private JTextArea statsTextArea;

    private JTextField searchField;
    private JButton searchButton;
    private JSlider zoomSlider;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JLabel zoomLabel;
    private int currentFontSize = DEFAULT_FONT_SIZE;

    private SearchController searchController;
    private final SearchViewModel searchViewModel;

    private TableController tableController;
    private final TableViewModel tableViewModel;

    public DataSetTableView(SearchViewModel searchViewModel, TableViewModel tableViewModel) {
        this.searchViewModel = searchViewModel;
        this.searchViewModel.addPropertyChangeListener(this);

        this.tableViewModel = tableViewModel;
        this.tableViewModel.addPropertyChangeListener(this);

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 245));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        importMenu = new JMenu("Import");
        importMenu.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        JMenuItem loadCSVItem = new JMenuItem("Load from CSV");
        loadCSVItem.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        JMenuItem kaggleItem = new JMenuItem("Kaggle");
        kaggleItem.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        importMenu.add(loadCSVItem);
        importMenu.add(kaggleItem);

        saveMenu = new JMenu("Save");
        saveMenu.setFont(new Font(FONT_NAME, Font.BOLD, 11));

        visualizationMenu = new JMenu("Visualization");
        visualizationMenu.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        JMenuItem placeholder1Item = new JMenuItem("Placeholder1");
        placeholder1Item.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        JMenuItem placeholder2Item = new JMenuItem("Placeholder2");
        placeholder2Item.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        visualizationMenu.add(placeholder1Item);
        visualizationMenu.add(placeholder2Item);

        menuBar.add(importMenu);
        menuBar.add(saveMenu);
        menuBar.add(visualizationMenu);

        statsTextArea = new JTextArea(10, 20);
        statsTextArea.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        statsTextArea.setEditable(false);
        statsTextArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        statsPanel = new JPanel(new BorderLayout(5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));
        JScrollPane statsScrollPane = new JScrollPane(statsTextArea);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        statsPanel.setPreferredSize(new Dimension(250, 0));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        topPanel.setBackground(new Color(240, 240, 245));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(new Color(240, 240, 245));

        JLabel titleLabel = new JLabel("Data Analysis Platform");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 80));
        leftPanel.add(titleLabel);
        leftPanel.add(menuBar);

        topPanel.add(leftPanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(240, 240, 245));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        centerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        bottomPanel.setBackground(new Color(240, 240, 245));

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

        int rowCount = dataTable.getRowCount();
        int colCount = dataTable.getColumnCount();
        String[][] tableData = new String[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                Object value = dataTable.getValueAt(i, j);
                tableData[i][j] = value != null ? value.toString() : "";
            }
        }

        int startRow = dataTable.getSelectedRow();
        int startCol = dataTable.getSelectedColumn();

        if (startRow == -1) startRow = 0;
        if (startCol == -1) startCol = -1;

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

    private void displayTableData(String[] headers, String[][] rowData) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (String header : headers) {
            tableModel.addColumn(header);
        }

        for (String[] row : rowData) {
            tableModel.addRow(row);
        }

        int scaledWidth = (int) (DEFAULT_COLUMN_WIDTH * (currentFontSize / (double) DEFAULT_FONT_SIZE));
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setPreferredWidth(scaledWidth);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            Object newValue = evt.getNewValue();

            // Handle SearchState
            if (newValue instanceof SearchState) {
                final SearchState state = (SearchState) newValue;

                if (state.isFound()) {
                    int row = state.getRow();
                    int col = state.getColumn();

                    dataTable.setRowSelectionInterval(row, row);
                    dataTable.setColumnSelectionInterval(col, col);
                    dataTable.scrollRectToVisible(dataTable.getCellRect(row, col, true));
                } else if (state.getErrorMessage() != null) {
                    JOptionPane.showMessageDialog(this,
                            state.getErrorMessage(),
                            "Search",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            // Handle TableState
            else if (newValue instanceof TableState) {
                final TableState state = (TableState) newValue;

                if (state.getErrorMessage() != null) {
                    JOptionPane.showMessageDialog(this,
                            state.getErrorMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    displayTableData(state.getColumnHeaders(), state.getRowData());
                }
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setSearchController(SearchController searchController) {
        this.searchController = searchController;
    }

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

    public void setImportController(Object controller) {
        // TODO: implement when ImportController is created
    }

    public void setSaveController(Object controller) {
        // TODO: implement when SaveController is created
    }

    public void setVisualizationController(Object controller) {
        // TODO: implement when VisualizationController is created
    }

    public void updateSummaryStats(String stats) {
        statsTextArea.setText(stats);
    }

    public void loadTable() {
        if (tableController != null) {
            tableController.displayCurrentTable();
        }
    }
}