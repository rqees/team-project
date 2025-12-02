package view;

import interface_adapter.load_csv.LoadController;
import interface_adapter.load_csv.LoadViewModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchState;
import interface_adapter.search.SearchViewModel;
import interface_adapter.table.TableController;
import interface_adapter.table.TableState;
import interface_adapter.table.TableViewModel;
import interface_adapter.save_dataset.SaveDataSetController;
import use_case.dataset.CurrentTableGateway;
import use_case.visualization.io.VisualizationInputData;
import use_case.visualization.model.PlotKind;
import entity.Column;
import entity.DataSet;
import entity.DataSubsetSpec;
import entity.DataType;

// >>> visualization
import interface_adapter.visualization.VisualizationController;
import interface_adapter.visualization.VisualizationState;
import interface_adapter.visualization.VisualizationViewModel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
// <<< visualization



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Main table view for the Data Analysis Program.
 * Implements Use Case 2: Table Format Display
 */
public class DataSetTableView extends JPanel implements PropertyChangeListener {

    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 24;
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final String FONT_NAME = "Segoe UI"; // Modern font
    private static final int DEFAULT_COLUMN_WIDTH = 120;
    
    // Modern dark theme colors
    private static final Color BG_DARK = new Color(30, 30, 35);
    private static final Color BG_MEDIUM = new Color(40, 40, 45);
    private static final Color BG_LIGHT = new Color(50, 50, 55);
    private static final Color FG_PRIMARY = new Color(220, 220, 230);
    private static final Color FG_SECONDARY = new Color(180, 180, 190);
    private static final Color ACCENT = new Color(100, 150, 255);
    private static final Color SELECTED_COLUMN = new Color(100, 150, 255, 100); // Semi-transparent accent

    private final String viewName = "dataset table";

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    private JMenuBar menuBar;
    private JMenu importMenu;
    private JMenuItem loadCSVItem;
    private JMenuItem kaggleItem;
    private JMenu saveMenu;
    private JMenuItem saveAsItem;
    private JMenu visualizationMenu;
    private JPanel statsPanel;
    private JTextArea statsTextArea;

        // >>> visualization
    /** Panel that holds the current visualization (XChart or heatmap). */
    private JPanel visualizationPanel;
    /** XChartPanel used to render XYChart charts. */
    private XChartPanel<XYChart> chartPanel;
    // <<< visualization



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

    private LoadController loadController;
    private final LoadViewModel loadViewModel;
    private SaveDataSetController saveController;

        // >>> visualization
        private VisualizationController visualizationController;
        private final VisualizationViewModel visualizationViewModel;
        // <<< visualization
        
        // Column selection for visualization
        private final Set<Integer> selectedColumns = new HashSet<>();
        private CurrentTableGateway tableGateway;
        private DataSubsetSpec currentSubsetSpec;
        
        // Visualization controls - Role-based
        private JPanel visualizationControlPanel;
        private JComboBox<PlotKind> plotTypeComboBox;
        private JComboBox<String> xAxisComboBox;
        private JPanel yAxisPanel;
        private java.util.List<JComboBox<String>> yAxisComboBoxes;
        private JButton addYAxisButton;
        private JComboBox<String> colorByComboBox;
        private JButton visualizeButton;
        private JLabel selectedColumnsLabel;

    public DataSetTableView(SearchViewModel searchViewModel, TableViewModel tableViewModel, LoadViewModel loadViewModel, VisualizationViewModel visualizationViewModel) {
        this.searchViewModel = searchViewModel;
        this.searchViewModel.addPropertyChangeListener(this);

        this.tableViewModel = tableViewModel;
        this.tableViewModel.addPropertyChangeListener(this);

        this.loadViewModel = loadViewModel;
        
        this.visualizationViewModel = visualizationViewModel;
        this.visualizationViewModel.addPropertyChangeListener(this);
        // <<< visualization

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
        dataTable.setGridColor(BG_LIGHT);
        dataTable.setBackground(BG_DARK);
        dataTable.setForeground(FG_PRIMARY);
        dataTable.setSelectionBackground(ACCENT);
        dataTable.setSelectionForeground(Color.WHITE);
        
        // Set custom header renderer for column selection
        JTableHeader header = dataTable.getTableHeader();
        header.setDefaultRenderer(new SelectableColumnHeaderRenderer());
        header.addMouseListener(new ColumnHeaderMouseListener());

        tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBackground(BG_DARK);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(BG_DARK);

        searchField = new JTextField(20);
        searchField.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        searchField.setBackground(BG_MEDIUM);
        searchField.setForeground(FG_PRIMARY);
        searchField.setCaretColor(ACCENT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        searchButton.setFocusPainted(false);
        searchButton.setBackground(BG_LIGHT);
        searchButton.setForeground(FG_PRIMARY);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        zoomSlider = new JSlider(MIN_FONT_SIZE, MAX_FONT_SIZE, DEFAULT_FONT_SIZE);
        zoomSlider.setPreferredSize(new Dimension(150, 25));
        zoomSlider.setMajorTickSpacing(4);
        zoomSlider.setMinorTickSpacing(2);
        zoomSlider.setPaintTicks(true);

        zoomInButton = new JButton("+");
        zoomInButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        zoomInButton.setFocusPainted(false);
        zoomInButton.setPreferredSize(new Dimension(45, 25));
        zoomInButton.setBackground(BG_LIGHT);
        zoomInButton.setForeground(FG_PRIMARY);
        zoomInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        zoomOutButton = new JButton("-");
        zoomOutButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        zoomOutButton.setFocusPainted(false);
        zoomOutButton.setPreferredSize(new Dimension(45, 25));
        zoomOutButton.setBackground(BG_LIGHT);
        zoomOutButton.setForeground(FG_PRIMARY);
        zoomOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        zoomLabel = new JLabel("100%");
        zoomLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        zoomLabel.setPreferredSize(new Dimension(45, 25));
        zoomLabel.setForeground(FG_PRIMARY);

        menuBar = new JMenuBar();
        menuBar.setBackground(BG_MEDIUM);
        menuBar.setForeground(FG_PRIMARY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        importMenu = new JMenu("Import");
        importMenu.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        loadCSVItem = new JMenuItem("Load from CSV");
        loadCSVItem.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        kaggleItem = new JMenuItem("Load from Kaggle");
        kaggleItem.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        importMenu.add(loadCSVItem);
        importMenu.add(kaggleItem);

        saveMenu = new JMenu("Save");
        saveMenu.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        saveAsItem = new JMenuItem("Save Dataset...");
        saveAsItem.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        saveMenu.add(saveAsItem);

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
        statsTextArea.setBackground(BG_MEDIUM);
        statsTextArea.setForeground(FG_PRIMARY);
        statsTextArea.setBorder(BorderFactory.createLineBorder(BG_LIGHT, 1));
        statsTextArea.setCaretColor(ACCENT);

        statsPanel = new JPanel(new BorderLayout(5, 5));
        statsPanel.setBackground(BG_DARK);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BG_LIGHT, 1),
            "Summary Statistics",
            0, 0,
            new Font(FONT_NAME, Font.BOLD, 12),
            FG_PRIMARY
        ));
        JScrollPane statsScrollPane = new JScrollPane(statsTextArea);
        statsScrollPane.setBackground(BG_DARK);
        statsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        statsPanel.setPreferredSize(new Dimension(250, 0));
    
            // >>> visualization: create panel that will hold the chart / heatmap
            visualizationPanel = new JPanel(new BorderLayout());
            visualizationPanel.setBackground(BG_DARK);
            visualizationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_LIGHT, 1),
                "Visualization",
                0, 0,
                new Font(FONT_NAME, Font.BOLD, 12),
                FG_PRIMARY
            ));
            visualizationPanel.setPreferredSize(new Dimension(0, 250)); // height at bottom
            
            // Create visualization control panel - Role-based
            visualizationControlPanel = new JPanel();
            visualizationControlPanel.setLayout(new BoxLayout(visualizationControlPanel, BoxLayout.Y_AXIS));
            visualizationControlPanel.setBackground(BG_DARK);
            visualizationControlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_LIGHT, 1),
                "Visualization Configuration",
                0, 0,
                new Font(FONT_NAME, Font.BOLD, 12),
                FG_PRIMARY
            ));
            
            // Plot type selector (first)
            JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            plotTypePanel.setBackground(BG_DARK);
            JLabel plotTypeLabel = new JLabel("Plot Type:");
            plotTypeLabel.setForeground(FG_PRIMARY);
            plotTypePanel.add(plotTypeLabel);
            plotTypeComboBox = new JComboBox<>();
            plotTypeComboBox.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            plotTypeComboBox.setPreferredSize(new Dimension(120, 25));
            plotTypeComboBox.setBackground(BG_MEDIUM);
            plotTypeComboBox.setForeground(FG_PRIMARY);
            filterPlotTypes();
            plotTypeComboBox.addActionListener(e -> updateRoleSelectors());
            plotTypePanel.add(plotTypeComboBox);
            visualizationControlPanel.add(plotTypePanel);
            
            // X-Axis role selector
            JPanel xAxisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            xAxisPanel.setBackground(BG_DARK);
            JLabel xAxisLabel = new JLabel("X-Axis:");
            xAxisLabel.setForeground(FG_PRIMARY);
            xAxisPanel.add(xAxisLabel);
            xAxisComboBox = new JComboBox<>();
            xAxisComboBox.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            xAxisComboBox.setPreferredSize(new Dimension(150, 25));
            xAxisComboBox.setBackground(BG_MEDIUM);
            xAxisComboBox.setForeground(FG_PRIMARY);
            xAxisComboBox.addItem("(Select column)");
            xAxisPanel.add(xAxisComboBox);
            visualizationControlPanel.add(xAxisPanel);
            
            // Y-Axis role selector (supports multiple)
            JPanel yAxisContainer = new JPanel();
            yAxisContainer.setLayout(new BoxLayout(yAxisContainer, BoxLayout.Y_AXIS));
            yAxisContainer.setBackground(BG_DARK);
            JPanel yAxisHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            yAxisHeader.setBackground(BG_DARK);
            JLabel yAxisLabel = new JLabel("Y-Axis:");
            yAxisLabel.setForeground(FG_PRIMARY);
            yAxisHeader.add(yAxisLabel);
            addYAxisButton = new JButton("+");
            addYAxisButton.setFont(new Font(FONT_NAME, Font.BOLD, 12));
            addYAxisButton.setPreferredSize(new Dimension(30, 25));
            addYAxisButton.setBackground(BG_LIGHT);
            addYAxisButton.setForeground(FG_PRIMARY);
            addYAxisButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addYAxisButton.addActionListener(e -> addYAxisSelector());
            yAxisHeader.add(addYAxisButton);
            yAxisContainer.add(yAxisHeader);
            
            yAxisPanel = new JPanel();
            yAxisPanel.setLayout(new BoxLayout(yAxisPanel, BoxLayout.Y_AXIS));
            yAxisPanel.setBackground(BG_DARK);
            yAxisComboBoxes = new ArrayList<>();
            addYAxisSelector(); // Add first Y-axis selector
            yAxisContainer.add(yAxisPanel);
            visualizationControlPanel.add(yAxisContainer);
            
            // Color By role selector (optional)
            JPanel colorByPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            colorByPanel.setBackground(BG_DARK);
            JLabel colorByLabel = new JLabel("Color By:");
            colorByLabel.setForeground(FG_PRIMARY);
            colorByPanel.add(colorByLabel);
            colorByComboBox = new JComboBox<>();
            colorByComboBox.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
            colorByComboBox.setPreferredSize(new Dimension(150, 25));
            colorByComboBox.setBackground(BG_MEDIUM);
            colorByComboBox.setForeground(FG_PRIMARY);
            colorByComboBox.addItem("(None)");
            colorByPanel.add(colorByComboBox);
            visualizationControlPanel.add(colorByPanel);
            
            // Visualize button and status
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            buttonPanel.setBackground(BG_DARK);
            visualizeButton = new JButton("Visualize");
            visualizeButton.setFont(new Font(FONT_NAME, Font.BOLD, 12));
            visualizeButton.setFocusPainted(false);
            visualizeButton.setEnabled(false);
            visualizeButton.setBackground(ACCENT);
            visualizeButton.setForeground(Color.WHITE);
            visualizeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            visualizeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttonPanel.add(visualizeButton);
            
            selectedColumnsLabel = new JLabel("(Click column headers to select)");
            selectedColumnsLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 10));
            selectedColumnsLabel.setForeground(FG_SECONDARY);
            buttonPanel.add(selectedColumnsLabel);
            visualizationControlPanel.add(buttonPanel);
    
    
            // <<< visualization
        }
        private void layoutComponents() {
            setLayout(new BorderLayout(10, 10));
            setBackground(BG_DARK);
        
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
            topPanel.setBackground(BG_MEDIUM);
        
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
            leftPanel.setBackground(BG_MEDIUM);
        
            JLabel titleLabel = new JLabel("Data Analysis Platform");
            titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
            titleLabel.setForeground(FG_PRIMARY);
            leftPanel.add(titleLabel);
            leftPanel.add(menuBar);
        
            topPanel.add(leftPanel, BorderLayout.WEST);
        
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            searchPanel.setBackground(BG_MEDIUM);
            JLabel searchLabel = new JLabel("Search: ");
            searchLabel.setForeground(FG_PRIMARY);
            searchPanel.add(searchLabel);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            topPanel.add(searchPanel, BorderLayout.EAST);
        
            JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
            centerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
            centerPanel.setBackground(BG_DARK);
            centerPanel.add(tableScrollPane, BorderLayout.CENTER);
            centerPanel.add(statsPanel, BorderLayout.EAST);
        
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
            bottomPanel.setBackground(BG_DARK);
        
            JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            zoomPanel.setBackground(BG_DARK);
            JLabel zoomLabelText = new JLabel("Zoom: ");
            zoomLabelText.setForeground(FG_PRIMARY);
            zoomPanel.add(zoomLabelText);
            zoomPanel.add(zoomOutButton);
            zoomPanel.add(zoomSlider);
            zoomPanel.add(zoomInButton);
            zoomPanel.add(zoomLabel);
        
            bottomPanel.add(zoomPanel, BorderLayout.NORTH);
            
            // >>> ADD VISUALIZATION PANELS HERE
            // Create a panel to hold both visualization control and visualization display
            JPanel vizContainer = new JPanel(new BorderLayout(10, 10));
            vizContainer.setBackground(BG_DARK);
            vizContainer.add(visualizationControlPanel, BorderLayout.WEST);
            vizContainer.add(visualizationPanel, BorderLayout.CENTER);
            
            bottomPanel.add(vizContainer, BorderLayout.CENTER);
            // <<< END VISUALIZATION PANELS
        
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
                // Visualization button handler
                visualizeButton.addActionListener(e -> performVisualization());
        
                // Update controls when selection changes
                xAxisComboBox.addActionListener(e -> updateVisualizeButtonState());
                colorByComboBox.addActionListener(e -> updateVisualizeButtonState());
        

        saveAsItem.addActionListener(e -> promptSaveDialog());

        loadCSVItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(DataSetTableView.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                loadController.execute(file);
            }
        });

        kaggleItem.addActionListener(e -> {
//            TODO implement loadkaggleusecase
        });

        loadViewModel.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "errorMessage":
                    JOptionPane.showMessageDialog(this,
                            "Error reading file: " + loadViewModel.getErrorMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                case "success":
                    if (loadViewModel.isSuccess()) {
                        loadTable();
                    }
                    break;
            }
        });
    }

    private void promptSaveDialog() {
        if (saveController == null) {
            JOptionPane.showMessageDialog(this,
                    "Save is not available yet.",
                    "Save Dataset",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Dataset");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setSelectedFile(new File("dataset.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile == null || selectedFile.getName().trim().isEmpty()) {
            // Trigger existing validation path so the presenter shows the "empty ID" message.
            try {
                saveController.execute("");
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to save dataset: " + e.getMessage(),
                        "Save Dataset",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            return;
        }

        if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".csv");
        }

        if (selectedFile.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "File already exists. Overwrite?",
                    "Confirm Save",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            saveController.execute(selectedFile.getAbsolutePath());
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save dataset: " + e.getMessage(),
                    "Save Dataset",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
        
        // Update visualization controls when table data changes
        updateVisualizationControls();
    }
    
    private void updateVisualizationControls() {
        // Clear previous selections
        selectedColumns.clear();
        currentSubsetSpec = null;
        
        // Update role selectors based on current plot type
        updateRoleSelectors();
        
        updateSelectedColumnsLabel();
        updateVisualizeButtonState();
    }
    
    private void updateRoleSelectors() {
        PlotKind plotKind = (PlotKind) plotTypeComboBox.getSelectedItem();
        if (plotKind == null) {
            return;
        }
        
        // Get selected column names
        List<String> selectedColumnNames = getSelectedColumnNames();
        updateSubsetSpec(selectedColumnNames);
        if (selectedColumnNames.isEmpty()) {
            // Clear all selectors if no columns selected
            xAxisComboBox.removeAllItems();
            xAxisComboBox.addItem("(Select column)");
            colorByComboBox.removeAllItems();
            colorByComboBox.addItem("(None)");
            for (JComboBox<String> yCombo : yAxisComboBoxes) {
                yCombo.removeAllItems();
                yCombo.addItem("(Select column)");
            }
            return;
        }
        
        // Update X-axis selector - only show selected columns that match plot type requirements
        xAxisComboBox.removeAllItems();
        xAxisComboBox.addItem("(Select column)");
        
        List<String> xAxisColumns = getXAxisColumns(plotKind);
        for (String colName : selectedColumnNames) {
            if (xAxisColumns.contains(colName)) {
                xAxisComboBox.addItem(colName);
            }
        }
        
        // Update Color By selector - shows all categorical columns
        colorByComboBox.removeAllItems();
        colorByComboBox.addItem("(None)");
        List<String> categoricalColumns = getCategoricalColumnNames();
        for (String colName : categoricalColumns) {
            colorByComboBox.addItem(colName);
        }


        // Update Y-axis selectors - only show selected numeric columns
        List<String> numericColumns = getNumericColumnNames();
        for (JComboBox<String> yCombo : yAxisComboBoxes) {
            yCombo.removeAllItems();
            yCombo.addItem("(Select column)");
            for (String colName : selectedColumnNames) {
                if (numericColumns.contains(colName)) {
                    yCombo.addItem(colName);
                }
            }
        }
    }
    
    private void updateSubsetSpec(List<String> columnNames) {
        if (columnNames.isEmpty()) {
            currentSubsetSpec = null;
            return;
        }
        currentSubsetSpec = buildSubsetSpec(columnNames);
    }
    
    private DataSubsetSpec buildSubsetSpec(Collection<String> columnNames) {
        return new DataSubsetSpec(
                "visualization-subset",
                new ArrayList<>(columnNames),
                getAllRowIndices()
        );
    }
    
    private DataSubsetSpec buildVisualizationSubsetSpec(String xAxisColumn,
                                                        List<String> yColumns,
                                                        String colorByColumn) {
        Set<String> columns = new LinkedHashSet<>();
        if (currentSubsetSpec != null && currentSubsetSpec.getColumnNames() != null) {
            columns.addAll(currentSubsetSpec.getColumnNames());
        }
        columns.add(xAxisColumn);
        columns.addAll(yColumns);
        if (colorByColumn != null) {
            columns.add(colorByColumn);
        }

        List<Integer> rowIndices;
        if (currentSubsetSpec != null && currentSubsetSpec.getRowIndices() != null) {
            rowIndices = new ArrayList<>(currentSubsetSpec.getRowIndices());
        } else {
            rowIndices = getAllRowIndices();
        }

        return new DataSubsetSpec("visualization-subset", new ArrayList<>(columns), rowIndices);
    }


    
    private List<String> getSelectedColumnNames() {
        List<String> selectedNames = new ArrayList<>();
        if (dataTable.getColumnCount() == 0) {
            return selectedNames;
        }
        
        String[] headers = new String[dataTable.getColumnCount()];
        for (int i = 0; i < headers.length; i++) {
            headers[i] = (String) dataTable.getColumnModel().getColumn(i).getHeaderValue();
        }
        
        for (Integer colIndex : selectedColumns) {
            if (colIndex < headers.length) {
                selectedNames.add(headers[colIndex]);
            }
        }
        
        return selectedNames;
    }
    
    private List<String> getXAxisColumns(PlotKind plotKind) {
        // Bar charts need categorical X-axis, others need numeric
        if (plotKind == PlotKind.BAR) {
            return getCategoricalColumnNames();
        } else {
            return getNumericColumnNames();
        }
    }
    
    private List<String> getCategoricalColumnNames() {
        List<String> categoricalColumns = new ArrayList<>();
        if (tableGateway == null) {
            return categoricalColumns;
        }
        
        DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            return categoricalColumns;
        }
        
        for (Column column : dataSet.getColumns()) {
            if (column.getDataType() == DataType.CATEGORICAL) {
                categoricalColumns.add(column.getHeader());
            }
        }
        
        return categoricalColumns;
    }
    
    private void addYAxisSelector() {
        JPanel yAxisRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        yAxisRow.setBackground(BG_DARK);
        JComboBox<String> yCombo = new JComboBox<>();
        yCombo.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        yCombo.setPreferredSize(new Dimension(150, 25));
        yCombo.setBackground(BG_MEDIUM);
        yCombo.setForeground(FG_PRIMARY);
        yCombo.addItem("(Select column)");
        
        // Only show selected numeric columns
        List<String> selectedColumnNames = getSelectedColumnNames();
        List<String> numericColumns = getNumericColumnNames();
        for (String colName : selectedColumnNames) {
            if (numericColumns.contains(colName)) {
                yCombo.addItem(colName);
            }
        }
        
        yCombo.addActionListener(e -> updateVisualizeButtonState());
        yAxisComboBoxes.add(yCombo);
        yAxisRow.add(yCombo);
        
        // Add remove button if more than one Y-axis
        if (yAxisComboBoxes.size() > 1) {
            JButton removeButton = new JButton("×");
            removeButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
            removeButton.setPreferredSize(new Dimension(25, 25));
            removeButton.setBackground(new Color(200, 60, 60));
            removeButton.setForeground(Color.WHITE);
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeButton.addActionListener(e -> {
                yAxisPanel.remove(yAxisRow);
                yAxisComboBoxes.remove(yCombo);
                yAxisPanel.revalidate();
                yAxisPanel.repaint();
                updateVisualizeButtonState();
            });
            yAxisRow.add(removeButton);
        }
        
        yAxisPanel.add(yAxisRow);
        yAxisPanel.revalidate();
        yAxisPanel.repaint();
        updateVisualizeButtonState();
    }

    
    private List<String> getNumericColumnNames() {
        List<String> numericColumns = new ArrayList<>();
        if (tableGateway == null) {
            return numericColumns;
        }
        
        DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            return numericColumns;
        }
        
        for (Column column : dataSet.getColumns()) {
            if (column.getDataType() == DataType.NUMERIC) {
                numericColumns.add(column.getHeader());
            }
        }
        
        return numericColumns;
    }
    
    private void updateSelectedColumnsLabel() {
        if (selectedColumns.isEmpty()) {
            selectedColumnsLabel.setText("Selected: None (click column headers to select)");
            selectedColumnsLabel.setForeground(Color.GRAY);
        } else {
            String[] headers = new String[dataTable.getColumnCount()];
            for (int i = 0; i < headers.length; i++) {
                headers[i] = (String) dataTable.getColumnModel().getColumn(i).getHeaderValue();
            }
            
            List<String> selectedNames = new ArrayList<>();
            for (Integer colIndex : selectedColumns) {
                if (colIndex < headers.length) {
                    selectedNames.add(headers[colIndex]);
                }
            }
            selectedColumnsLabel.setText("Selected: " + String.join(", ", selectedNames));
            selectedColumnsLabel.setForeground(ACCENT);
        }
    }
    
    private void updateVisualizeButtonState() {
        // Early return if button not yet initialized
        if (visualizeButton == null || plotTypeComboBox == null || xAxisComboBox == null) {
            return;
        }
        
        PlotKind plotKind = (PlotKind) plotTypeComboBox.getSelectedItem();
        if (plotKind == null) {
            visualizeButton.setEnabled(false);
            return;
        }
        
        // Check X-axis is selected
        String xAxis = (String) xAxisComboBox.getSelectedItem();
        boolean xAxisValid = xAxis != null && !xAxis.equals("(Select column)");
        
        // Check at least one Y-axis is selected
        boolean yAxisValid = yAxisComboBoxes != null && yAxisComboBoxes.stream()
                .anyMatch(combo -> {
                    String selected = (String) combo.getSelectedItem();
                    return selected != null && !selected.equals("(Select column)");
                });
        
        // Heatmap requires at least 2 columns
        boolean plotTypeValid = true;
        if (plotKind == PlotKind.HEATMAP && yAxisComboBoxes != null) {
            long selectedYCount = yAxisComboBoxes.stream()
                    .filter(combo -> {
                        String selected = (String) combo.getSelectedItem();
                        return selected != null && !selected.equals("(Select column)");
                    })
                    .count();
            plotTypeValid = selectedYCount >= 2;
        }
        
        visualizeButton.setEnabled(xAxisValid && yAxisValid && plotTypeValid);
    }
    
    private void filterPlotTypes() {
        // Remove all items
        plotTypeComboBox.removeAllItems();
        
        // Add all plot types - we'll enable/disable based on selection
        plotTypeComboBox.addItem(PlotKind.SCATTER);
        plotTypeComboBox.addItem(PlotKind.LINE);
        plotTypeComboBox.addItem(PlotKind.BAR);
        plotTypeComboBox.addItem(PlotKind.HISTOGRAM);
        plotTypeComboBox.addItem(PlotKind.HEATMAP);
        
        // Update enabled state
        updatePlotTypeEnabledState();
    }
    
    private void updatePlotTypeEnabledState() {
        int numSelected = selectedColumns.size();
        
        // Create a custom renderer to grey out disabled items
        plotTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof PlotKind plotKind) {
                    boolean enabled = isPlotTypeEnabled(plotKind, numSelected);
                    c.setEnabled(enabled);
                    if (!enabled) {
                        c.setForeground(Color.GRAY);
                    }
                }
                
                return c;
            }
        });
    }
    
    private boolean isPlotTypeEnabled(PlotKind plotKind, int numSelected) {
        // Heatmap requires at least 2 columns
        if (plotKind == PlotKind.HEATMAP) {
            return numSelected >= 2;
        }
        // Other plot types can handle 1+ columns
        return numSelected >= 1;
    }
    
    private void performVisualization() {
        if (visualizationController == null || tableGateway == null) {
            JOptionPane.showMessageDialog(this,
                    "Visualization controller not available",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PlotKind plotKind = (PlotKind) plotTypeComboBox.getSelectedItem();
        if (plotKind == null) {
            return;
        }
        
        // Get X-axis column
        String xAxisColumn = (String) xAxisComboBox.getSelectedItem();
        if (xAxisColumn == null || xAxisColumn.equals("(Select column)")) {
            JOptionPane.showMessageDialog(this,
                    "Please select an X-axis column",
                    "No X-Axis Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get Y-axis columns
        List<String> yColumns = new ArrayList<>();
        for (JComboBox<String> yCombo : yAxisComboBoxes) {
            String selected = (String) yCombo.getSelectedItem();
            if (selected != null && !selected.equals("(Select column)")) {
                yColumns.add(selected);
            }
        }
        
        if (yColumns.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one Y-axis column",
                    "No Y-Axis Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get Color By column (optional)
        String colorByColumn = (String) colorByComboBox.getSelectedItem();
        if (colorByColumn != null && colorByColumn.equals("(None)")) {
            colorByColumn = null;
        }
        
        // Create DataSubsetSpec (include selected columns plus required role columns)
        DataSubsetSpec subsetSpec = buildVisualizationSubsetSpec(xAxisColumn, yColumns, colorByColumn);
        currentSubsetSpec = subsetSpec;



        int summaryReportId = -1;

        // Create title
        String title = plotKind.name() + ": " + xAxisColumn;
        if (!yColumns.isEmpty()) {
            title += " vs " + String.join(", ", yColumns);
        }
        if (colorByColumn != null) {
            title += " (by " + colorByColumn + ")";
        }
        
        // Create VisualizationInputData with role-based configuration
        VisualizationInputData inputData = new VisualizationInputData(
                summaryReportId,
                plotKind,
                subsetSpec,
                Arrays.asList(xAxisColumn),
                yColumns,
                colorByColumn,
                title
        );
        
        // Execute visualization
        visualizationController.visualize(inputData);
    }
    
    // Custom header renderer to highlight selected columns
    private class SelectableColumnHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (selectedColumns.contains(column)) {
                c.setBackground(SELECTED_COLUMN);
                c.setForeground(FG_PRIMARY);
            } else {
                c.setBackground(BG_MEDIUM);
                c.setForeground(FG_PRIMARY);
            }
            
            return c;
        }
    }
    
    // Mouse listener for column header clicks
    private class ColumnHeaderMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            int column = header.columnAtPoint(e.getPoint());
            
            if (column >= 0) {
                // Check if column is numeric
                String columnName = (String) dataTable.getColumnModel().getColumn(column).getHeaderValue();
                List<String> numericColumns = getNumericColumnNames();
                
                if (!numericColumns.contains(columnName)) {
                    JOptionPane.showMessageDialog(DataSetTableView.this,
                            "Only numeric columns can be visualized",
                            "Invalid Column",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Toggle selection
                if (selectedColumns.contains(column)) {
                    selectedColumns.remove(column);
                } else {
                    selectedColumns.add(column);
                }
                
                // Update UI
                header.repaint();
                updateSelectedColumnsLabel();
                updatePlotTypeEnabledState();
                updateRoleSelectors(); // Update dropdowns to show only selected columns
                updateVisualizeButtonState();
            }
        }
    }
    

    // >>> visualization: helper to update the chart panel from the ViewModel state
    private void displayChart(XYChart chart) {
        visualizationPanel.removeAll();

        if (chart != null) {
            chartPanel = new XChartPanel<>(chart);
            visualizationPanel.add(chartPanel, BorderLayout.CENTER);
        }

        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }
    // <<< visualization



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
            // >>> visualization: Handle VisualizationState
            else if (newValue instanceof VisualizationState) {
                final VisualizationState state = (VisualizationState) newValue;

                if (state.getErrorMessage() != null) {
                    JOptionPane.showMessageDialog(this,
                            state.getErrorMessage(),
                            "Visualization Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    // For now we support XYChart; heatmap can be added later.
                    displayChart(state.getXyChart());
                    // You could also use state.getTitle() to update a label if desired.
                }
            }
            // <<< visualization
        }
    }
    
    private List<Integer> getAllRowIndices() {
        int rowCount = dataTable.getRowCount();
        List<Integer> rowIndices = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rowIndices.add(i);
        }
        return rowIndices;
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

    public void setLoadController(LoadController loadController) {
        this.loadController = loadController;
    }

    public void setImportController(Object controller) {
        // TODO: implement when ImportController is created
    }

    public void setSaveController(SaveDataSetController controller) {
        this.saveController = controller;
    }

      // >>> visualization
      public void setVisualizationController(VisualizationController controller) {
        this.visualizationController = controller;
    }
    
    public void setTableGateway(CurrentTableGateway gateway) {
        this.tableGateway = gateway;
    }
    // <<< visualization


    public void updateSummaryStats(String stats) {
        statsTextArea.setText(stats);
    }

    public void loadTable() {
        if (tableController != null) {
            tableController.displayCurrentTable();
        }
    }
}
