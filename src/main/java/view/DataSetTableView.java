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

// >>> visualization
import interface_adapter.visualization.PlotKindView;
import interface_adapter.visualization.VisualizationController;
import interface_adapter.visualization.VisualizationRules;
import interface_adapter.visualization.VisualizationState;
import interface_adapter.visualization.VisualizationViewModel;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
// <<< visualization
import interface_adapter.statistics.SummaryStatisticsController;
import interface_adapter.statistics.SummaryStatisticsState;
import interface_adapter.statistics.SummaryStatisticsViewModel;


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

    // Statistics components
    private SummaryStatisticsController statisticsController;
    private final SummaryStatisticsViewModel statisticsViewModel;

    // Statistics display
    private JTable statsTable;
    private DefaultTableModel statsTableModel;
    private JScrollPane statsScrollPane;
    private JButton calculateStatsButton;
        
        // Column selection for visualization
        private final Set<Integer> selectedColumns = new HashSet<>();
        
        // Visualization controls - Role-based
        private JPanel visualizationControlPanel;
        private JComboBox<PlotKindView> plotTypeComboBox;
        private JComboBox<String> xAxisComboBox;
        private JPanel yAxisPanel;
        private java.util.List<JComboBox<String>> yAxisComboBoxes;
        private JButton addYAxisButton;
        private JComboBox<String> colorByComboBox;
        private JButton visualizeButton;
        private JLabel selectedColumnsLabel;
        
        // Summary overlay controls
        private JButton showSummaryOverlayButton;
        private JPanel overlayCheckboxPanel;
        private JScrollPane overlayCheckboxScrollPane;
        private JCheckBox showMeanCheckBox;
        private JCheckBox showMedianCheckBox;
        private JCheckBox showStdDevBandCheckBox;
        private JCheckBox showMinMaxCheckBox;
        // private JCheckBox highlightOutliersCheckBox;
        private boolean overlayEnabled = false;

    public DataSetTableView(SearchViewModel searchViewModel, TableViewModel tableViewModel, LoadViewModel loadViewModel,
                            VisualizationViewModel visualizationViewModel, SummaryStatisticsViewModel statisticsViewModel) {
        this.searchViewModel = searchViewModel;
        this.searchViewModel.addPropertyChangeListener(this);

        this.tableViewModel = tableViewModel;
        this.tableViewModel.addPropertyChangeListener(this);

        this.loadViewModel = loadViewModel;
        
        this.visualizationViewModel = visualizationViewModel;
        this.visualizationViewModel.addPropertyChangeListener(this);
        // <<< visualization

        this.statisticsViewModel = statisticsViewModel;
        this.statisticsViewModel.addPropertyChangeListener(this);

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



        menuBar.add(importMenu);
        menuBar.add(saveMenu);

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
            
            // Summary Overlay Section
            JPanel overlaySection = new JPanel();
            overlaySection.setLayout(new BoxLayout(overlaySection, BoxLayout.Y_AXIS));
            overlaySection.setBackground(BG_DARK);
            overlaySection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_LIGHT, 1),
                "Summary Overlays",
                0, 0,
                new Font(FONT_NAME, Font.BOLD, 12),
                FG_PRIMARY
            ));
            
            // Show Summary Overlay button
            JPanel overlayButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            overlayButtonPanel.setBackground(BG_DARK);
            showSummaryOverlayButton = new JButton("Show Summary Overlay");
            showSummaryOverlayButton.setFont(new Font(FONT_NAME, Font.BOLD, 11));
            showSummaryOverlayButton.setFocusPainted(false);
            showSummaryOverlayButton.setEnabled(false);
            showSummaryOverlayButton.setBackground(BG_LIGHT);
            showSummaryOverlayButton.setForeground(FG_SECONDARY);
            showSummaryOverlayButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            showSummaryOverlayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            showSummaryOverlayButton.addActionListener(e -> toggleSummaryOverlay());
            overlayButtonPanel.add(showSummaryOverlayButton);
            overlaySection.add(overlayButtonPanel);
            
            // Overlay checkboxes panel
            overlayCheckboxPanel = new JPanel();
            overlayCheckboxPanel.setLayout(new BoxLayout(overlayCheckboxPanel, BoxLayout.Y_AXIS));
            overlayCheckboxPanel.setBackground(BG_DARK);
            overlayCheckboxPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            showMeanCheckBox = createOverlayCheckbox("Show mean");
            showMedianCheckBox = createOverlayCheckbox("Show median");
            showStdDevBandCheckBox = createOverlayCheckbox("Show std dev band");
            showMinMaxCheckBox = createOverlayCheckbox("Show min/max");
            // highlightOutliersCheckBox = createOverlayCheckbox("Highlight outliers");
            
            overlayCheckboxPanel.add(showMeanCheckBox);
            overlayCheckboxPanel.add(showMedianCheckBox);
            overlayCheckboxPanel.add(showStdDevBandCheckBox);
            overlayCheckboxPanel.add(showMinMaxCheckBox);
            // overlayCheckboxPanel.add(highlightOutliersCheckBox);
            
            // Make checkboxes initially disabled
            setOverlayCheckboxesEnabled(false);
            
            overlayCheckboxScrollPane = new JScrollPane(overlayCheckboxPanel);
            overlayCheckboxScrollPane.setBackground(BG_DARK);
            overlayCheckboxScrollPane.setBorder(BorderFactory.createEmptyBorder());
            overlayCheckboxScrollPane.setPreferredSize(new Dimension(0, 120));
            overlayCheckboxScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            overlayCheckboxScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            overlayCheckboxScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            overlaySection.add(overlayCheckboxScrollPane);
            
            visualizationControlPanel.add(overlaySection);
    
    
            // <<< visualization

        // ===== Statistics Panel (Table Format) =====
        statsPanel = new JPanel(new BorderLayout(5, 5));
        statsPanel.setBackground(BG_DARK);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_LIGHT, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title panel with calculate button
        JPanel statsTitlePanel = new JPanel(new BorderLayout());
        statsTitlePanel.setBackground(BG_DARK);

        JLabel statsTitle = new JLabel("Summary Statistics");
        statsTitle.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        statsTitle.setForeground(FG_PRIMARY);
        statsTitlePanel.add(statsTitle, BorderLayout.WEST);

        calculateStatsButton = new JButton("Calculate");
        calculateStatsButton.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        calculateStatsButton.setBackground(ACCENT);
        calculateStatsButton.setForeground(Color.WHITE);
        calculateStatsButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        calculateStatsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateStatsButton.setFocusPainted(false);
        statsTitlePanel.add(calculateStatsButton, BorderLayout.EAST);

        // Statistics table
        statsTableModel = new DefaultTableModel(
                new String[]{"Metric", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statsTable = new JTable(statsTableModel);
        statsTable.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statsTable.setBackground(BG_MEDIUM);
        statsTable.setForeground(FG_PRIMARY);
        statsTable.setGridColor(BG_LIGHT);
        statsTable.setRowHeight(24);
        statsTable.setShowGrid(true);
        statsTable.setIntercellSpacing(new Dimension(1, 1));

        // Style the header
        statsTable.getTableHeader().setBackground(BG_LIGHT);
        statsTable.getTableHeader().setForeground(FG_PRIMARY);
        statsTable.getTableHeader().setFont(new Font(FONT_NAME, Font.BOLD, 11));

        // Right-align the "Value" column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBackground(BG_MEDIUM);
        rightRenderer.setForeground(FG_PRIMARY);
        statsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        // Make "Metric" column wider
        statsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(120);

        statsScrollPane = new JScrollPane(statsTable);
        statsScrollPane.setBackground(BG_DARK);
        statsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        statsScrollPane.getViewport().setBackground(BG_MEDIUM);

        // Instructions label
        JLabel instructionsLabel = new JLabel(
                "<html><center>Click column headers to select<br/>then click Calculate</center></html>");
        instructionsLabel.setFont(new Font(FONT_NAME, Font.ITALIC, 10));
        instructionsLabel.setForeground(FG_SECONDARY);
        instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        statsPanel.add(statsTitlePanel, BorderLayout.NORTH);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        statsPanel.add(instructionsLabel, BorderLayout.SOUTH);
        statsPanel.setPreferredSize(new Dimension(280, 0));
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
        // Statistics calculate button handler
        calculateStatsButton.addActionListener(e -> performCalculateStatistics());
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

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile == null || selectedFile.isDirectory()) {
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
        
        // Update role selectors based on current plot type
        updateRoleSelectors();
        
        updateSelectedColumnsLabel();
        updateVisualizeButtonState();
    }
    
    private void updateRoleSelectors() {
        PlotKindView plotKind = (PlotKindView) plotTypeComboBox.getSelectedItem();
        if (plotKind == null) {
            return;
        }
        
        // Get column metadata from ViewModel state
        VisualizationState state = visualizationViewModel.getState();
        List<String> numericColumns = state.getNumericColumnNames();
        List<String> categoricalColumns = state.getCategoricalColumnNames();
        
        // Get selected column names
        List<String> selectedColumnNames = getSelectedColumnNames();
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
        
        List<String> validXAxisColumns = VisualizationRules.getValidXAxisColumns(
            plotKind, numericColumns, categoricalColumns);
        for (String colName : selectedColumnNames) {
            if (validXAxisColumns.contains(colName)) {
                xAxisComboBox.addItem(colName);
            }
        }
        
        // Update Color By selector - shows all categorical columns
        colorByComboBox.removeAllItems();
        colorByComboBox.addItem("(None)");
        for (String colName : categoricalColumns) {
            colorByComboBox.addItem(colName);
        }

        // Update Y-axis selectors - only show selected numeric columns
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

    private void performCalculateStatistics() {
        if (statisticsController == null) {
            JOptionPane.showMessageDialog(this,
                    "Statistics controller not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get selected columns
        List<String> selectedColumnNames = getSelectedColumnNames();

        if (selectedColumnNames.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select columns by clicking on column headers",
                    "No Columns Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Filter for numeric columns only
        VisualizationState state = visualizationViewModel.getState();
        List<String> numericColumns = state.getNumericColumnNames();
        List<String> selectedNumericColumns = new ArrayList<>();
        for (String colName : selectedColumnNames) {
            if (numericColumns.contains(colName)) {
                selectedNumericColumns.add(colName);
            }
        }

        if (selectedNumericColumns.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No numeric columns selected. Please select numeric columns.",
                    "No Numeric Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Calculate statistics for all rows of selected columns
        int totalRows = dataTable.getRowCount();
        statisticsController.calculateStatisticsAllRows(
                1,  // dataSubsetId
                "Summary Statistics",  // reportName
                "current-dataset",  // datasetId
                selectedNumericColumns,
                totalRows
        );
    }

    private void displayStatisticsTable(Map<String, SummaryStatisticsState.ColumnStatistics> columnStats) {
        if (columnStats == null || columnStats.isEmpty()) {
            statsTableModel.setRowCount(0);
            statsTableModel.addRow(new Object[]{"No data", "N/A"});
            return;
        }

        statsTableModel.setRowCount(0);

        // Group statistics by column
        for (Map.Entry<String, SummaryStatisticsState.ColumnStatistics> entry : columnStats.entrySet()) {
            String columnName = entry.getKey();
            SummaryStatisticsState.ColumnStatistics stats = entry.getValue();

            // Add column header row (bold via HTML)
            statsTableModel.addRow(new Object[]{
                    "<html><b>" + columnName + "</b></html>",
                    ""
            });

            // Add statistics rows
            statsTableModel.addRow(new Object[]{"  Mean", stats.getMean()});
            statsTableModel.addRow(new Object[]{"  Median", stats.getMedian()});
            statsTableModel.addRow(new Object[]{"  Std Dev", stats.getStandardDeviation()});
            statsTableModel.addRow(new Object[]{"  Min", stats.getMin()});
            statsTableModel.addRow(new Object[]{"  Max", stats.getMax()});
            statsTableModel.addRow(new Object[]{"  Count", stats.getCount()});

            // Add separator row
            if (columnStats.size() > 1) {
                statsTableModel.addRow(new Object[]{"", ""});
            }
        }
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
        VisualizationState state = visualizationViewModel.getState();
        List<String> numericColumns = state.getNumericColumnNames();
        List<String> selectedColumnNames = getSelectedColumnNames();
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
        
        PlotKindView plotKind = (PlotKindView) plotTypeComboBox.getSelectedItem();
        if (plotKind == null) {
            visualizeButton.setEnabled(false);
            return;
        }
        
        // Get X-axis selection
        String xAxis = (String) xAxisComboBox.getSelectedItem();
        
        // Get Y-axis selections
        List<String> yColumns = new ArrayList<>();
        if (yAxisComboBoxes != null) {
            for (JComboBox<String> yCombo : yAxisComboBoxes) {
                String selected = (String) yCombo.getSelectedItem();
                if (selected != null && !selected.equals("(Select column)")) {
                    yColumns.add(selected);
                }
            }
        }
        
        // Use VisualizationRules to validate configuration
        int numSelected = selectedColumns.size();
        boolean isValid = VisualizationRules.isValidConfiguration(
            plotKind, numSelected, xAxis, yColumns);
        
        visualizeButton.setEnabled(isValid);
    }
    
    private void filterPlotTypes() {
        // Remove all items
        plotTypeComboBox.removeAllItems();
        
        // Add all plot types - we'll enable/disable based on selection
        plotTypeComboBox.addItem(PlotKindView.SCATTER);
        plotTypeComboBox.addItem(PlotKindView.LINE);
        plotTypeComboBox.addItem(PlotKindView.BAR);
        plotTypeComboBox.addItem(PlotKindView.HISTOGRAM);
        plotTypeComboBox.addItem(PlotKindView.HEATMAP);
        
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
                
                if (value instanceof PlotKindView plotKind) {
                    boolean enabled = VisualizationRules.isPlotTypeEnabled(plotKind, numSelected);
                    c.setEnabled(enabled);
                    if (!enabled) {
                        c.setForeground(Color.GRAY);
                    }
                }
                
                return c;
            }
        });
    }
    
    private void performVisualization() {
        if (visualizationController == null) {
            JOptionPane.showMessageDialog(this,
                    "Visualization controller not available",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PlotKindView plotKind = (PlotKindView) plotTypeComboBox.getSelectedItem();
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
        
        // Get selected column indices
        List<Integer> selectedColumnIndices = new ArrayList<>(selectedColumns);
        
        // Get all row indices (null means all rows)
        List<Integer> rowIndices = getAllRowIndices();
        
        // Call controller with primitive data - it will construct DataSubsetSpec and VisualizationInputData
        visualizationController.visualizeWithPrimitiveData(
                plotKind,
                selectedColumnIndices,
                xAxisColumn,
                yColumns,
                colorByColumn,
                rowIndices
        );
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
                // Check if column is numeric (using ViewModel state)
                String columnName = (String) dataTable.getColumnModel().getColumn(column).getHeaderValue();
                VisualizationState state = visualizationViewModel.getState();
                
                if (!state.isNumericColumn(columnName)) {
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
            
            // Clear any existing overlays when a new chart is displayed
            // Overlays will be reapplied if overlay is enabled
            if (overlayEnabled && chartPanel != null && chartPanel.getChart() != null) {
                applySummaryOverlays();
            }
        }

        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }
    // <<< visualization
    
    // >>> summary overlay helper methods
    private JCheckBox createOverlayCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font(FONT_NAME, Font.PLAIN, 10));
        checkbox.setBackground(BG_DARK);
        checkbox.setForeground(FG_PRIMARY);
        checkbox.setFocusPainted(false);
        checkbox.addActionListener(e -> updateOverlays());
        return checkbox;
    }
    
    private void setOverlayCheckboxesEnabled(boolean enabled) {
        showMeanCheckBox.setEnabled(enabled);
        showMedianCheckBox.setEnabled(enabled);
        showStdDevBandCheckBox.setEnabled(enabled);
        showMinMaxCheckBox.setEnabled(enabled);
        // highlightOutliersCheckBox.setEnabled(enabled);
    }
    
    private void toggleSummaryOverlay() {
        overlayEnabled = !overlayEnabled;
        
        if (overlayEnabled) {
            showSummaryOverlayButton.setText("Hide Summary Overlay");
            showSummaryOverlayButton.setBackground(ACCENT);
            showSummaryOverlayButton.setForeground(Color.WHITE);
            setOverlayCheckboxesEnabled(true);
            applySummaryOverlays();
        } else {
            showSummaryOverlayButton.setText("Show Summary Overlay");
            showSummaryOverlayButton.setBackground(BG_LIGHT);
            showSummaryOverlayButton.setForeground(FG_SECONDARY);
            setOverlayCheckboxesEnabled(false);
            removeSummaryOverlays();
        }
    }
    
    private void removeSummaryOverlays() {
        if (chartPanel == null || chartPanel.getChart() == null) {
            return;
        }
        
        XYChart chart = chartPanel.getChart();
        
        // Remove all overlay series (series that start with overlay prefixes)
        List<String> seriesToRemove = new ArrayList<>();
        for (XYSeries series : chart.getSeriesMap().values()) {
            String seriesName = series.getName();
            if (seriesName.startsWith("overlay_mean_") ||
                seriesName.startsWith("overlay_median_") ||
                seriesName.startsWith("overlay_stddev_upper_") ||
                seriesName.startsWith("overlay_stddev_lower_") ||
                seriesName.startsWith("overlay_min_") ||
                seriesName.startsWith("overlay_max_") ||
                seriesName.startsWith("overlay_outlier_")) {
                seriesToRemove.add(seriesName);
            }
        }
        
        for (String seriesName : seriesToRemove) {
            chart.removeSeries(seriesName);
        }
        
        chartPanel.repaint();
    }
    
    private void applySummaryOverlays() {
        if (chartPanel == null || chartPanel.getChart() == null) {
            return;
        }
        
        XYChart chart = chartPanel.getChart();
        SummaryStatisticsState state = statisticsViewModel.getState();
        
        // Remove existing overlays first
        removeSummaryOverlays();
        
        // Check if we have valid statistics
        if (state == null || state.getColumnStats() == null || state.getColumnStats().isEmpty()) {
            return;
        }
        
        // Get the current chart's X and Y axis ranges
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        
        // Find X range from existing series
        for (XYSeries series : chart.getSeriesMap().values()) {
            double[] xData = series.getXData();
            if (xData != null && xData.length > 0) {
                for (double x : xData) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                }
            }
        }
        
        if (minX == Double.MAX_VALUE || maxX == -Double.MAX_VALUE) {
            // No data in chart, can't add overlays
            return;
        }
        
        // Get Y-axis columns from the current visualization
        Set<String> yColumnsSet = new HashSet<>();
        
        // Try to identify Y-axis columns from the chart series
        // (excluding overlay series and annotation series)
        for (XYSeries series : chart.getSeriesMap().values()) {
            String seriesName = series.getName();
            // Filter out overlay series, annotation series (hl-, vl-, ann-), and special series like "Outliers"
            if (!seriesName.startsWith("overlay_") && 
                !seriesName.startsWith("hl-") && 
                !seriesName.startsWith("vl-") && 
                !seriesName.startsWith("ann-") &&
                !seriesName.equals("Outliers") &&
                !seriesName.equals("Data")) {
                // Extract base column name from series name
                // When colorBy is set, series names are "YColumn (Group)", so extract just "YColumn"
                String baseColumnName = seriesName;
                int parenIndex = seriesName.indexOf(" (");
                if (parenIndex > 0) {
                    // Series name has format "YColumn (Group)", extract just "YColumn"
                    baseColumnName = seriesName.substring(0, parenIndex);
                }
                yColumnsSet.add(baseColumnName);
            }
        }
        
        List<String> yColumns = new ArrayList<>(yColumnsSet);
        
        // If we can't determine Y columns from series, use all numeric columns with stats
        // that are currently displayed in the chart
        if (yColumns.isEmpty()) {
            // Fallback: use all columns that have statistics
            yColumns.addAll(state.getColumnStats().keySet());
        }
        
        // Apply overlays for each Y column that has statistics
        for (String yColumnName : yColumns) {
            SummaryStatisticsState.ColumnStatistics stats = state.getColumnStats().get(yColumnName);
            if (stats == null) {
                continue;
            }
            
            try {
                double mean = parseDouble(stats.getMean());
                double median = parseDouble(stats.getMedian());
                double stdDev = parseDouble(stats.getStandardDeviation());
                double min = parseDouble(stats.getMin());
                double max = parseDouble(stats.getMax());
                
                // Mean line
                if (showMeanCheckBox.isSelected()) {
                    addHorizontalLine(chart, mean, minX, maxX, "overlay_mean_" + yColumnName, 
                                     "Mean (" + yColumnName + ")", new Color(100, 200, 100, 200));
                }
                
                // Median line
                if (showMedianCheckBox.isSelected()) {
                    addHorizontalLine(chart, median, minX, maxX, "overlay_median_" + yColumnName,
                                     "Median (" + yColumnName + ")", new Color(200, 150, 100, 200));
                }
                
                // Standard deviation band
                if (showStdDevBandCheckBox.isSelected()) {
                    addStdDevBand(chart, mean, stdDev, minX, maxX, yColumnName);
                }
                
                // Min/Max lines
                if (showMinMaxCheckBox.isSelected()) {
                    addHorizontalLine(chart, min, minX, maxX, "overlay_min_" + yColumnName,
                                     "Min (" + yColumnName + ")", new Color(150, 150, 255, 150));
                    addHorizontalLine(chart, max, minX, maxX, "overlay_max_" + yColumnName,
                                     "Max (" + yColumnName + ")", new Color(150, 150, 255, 150));
                }
                
                // Outliers (if information is available in state)
                // if (highlightOutliersCheckBox.isSelected()) {
                //     // TODO: Outlier detection requires additional data in ColumnStatistics
                //     // For now, this is a no-op as per requirements
                // }
                
            } catch (NumberFormatException e) {
                // Skip this column if values can't be parsed
                continue;
            }
        }
        
        chartPanel.repaint();
    }
    
    private void addHorizontalLine(XYChart chart, double yValue, double minX, double maxX, 
                                   String seriesName, String label, Color color) {
        double[] xData = {minX, maxX};
        double[] yData = {yValue, yValue};
        XYSeries series = chart.addSeries(seriesName, xData, yData);
        series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        series.setLineColor(color);
        series.setLineWidth(1.5f);
        series.setMarker(null);
        series.setShowInLegend(true);
    }
    
    private void addStdDevBand(XYChart chart, double mean, double stdDev, double minX, double maxX, String columnName) {
        // Create upper and lower bounds
        double upper = mean + stdDev;
        double lower = mean - stdDev;
        
        // Add upper bound line
        XYSeries upperSeries = chart.addSeries("overlay_stddev_upper_" + columnName, 
                                               new double[]{minX, maxX}, 
                                               new double[]{upper, upper});
        upperSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        upperSeries.setLineColor(new Color(255, 200, 100, 180));
        upperSeries.setLineWidth(1.0f);
        upperSeries.setLineStyle(new java.awt.BasicStroke(1.0f, java.awt.BasicStroke.CAP_BUTT, 
                                                          java.awt.BasicStroke.JOIN_MITER, 10.0f, 
                                                          new float[]{5.0f}, 0.0f));
        upperSeries.setMarker(null);
        upperSeries.setShowInLegend(false);
        
        // Add lower bound line
        XYSeries lowerSeries = chart.addSeries("overlay_stddev_lower_" + columnName,
                                               new double[]{minX, maxX},
                                               new double[]{lower, lower});
        lowerSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        lowerSeries.setLineColor(new Color(255, 200, 100, 180));
        lowerSeries.setLineWidth(1.0f);
        lowerSeries.setLineStyle(new java.awt.BasicStroke(1.0f, java.awt.BasicStroke.CAP_BUTT,
                                                          java.awt.BasicStroke.JOIN_MITER, 10.0f,
                                                          new float[]{5.0f}, 0.0f));
        lowerSeries.setMarker(null);
        lowerSeries.setShowInLegend(false);
        
        // Note: XChart doesn't have native area fill between two series,
        // so we use dashed lines to indicate the band boundaries
    }
    
    private void updateOverlays() {
        if (overlayEnabled && chartPanel != null && chartPanel.getChart() != null) {
            applySummaryOverlays();
        }
    }
    
    private double parseDouble(String value) throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Empty value");
        }
        return Double.parseDouble(value.trim());
    }
    
    private void updateOverlayButtonState(SummaryStatisticsState state) {
        boolean shouldEnable = false;
        
        if (state != null) {
            if (state.isCalculating()) {
                shouldEnable = false;
            } else if (state.getErrorMessage() != null) {
                shouldEnable = false;
            } else if (state.getColumnStats() != null && !state.getColumnStats().isEmpty()) {
                // Check if we have a chart displayed
                if (chartPanel != null && chartPanel.getChart() != null) {
                    shouldEnable = true;
                }
            }
        }
        
        showSummaryOverlayButton.setEnabled(shouldEnable);
        
        // If button becomes disabled, also disable overlay if it was enabled
        if (!shouldEnable && overlayEnabled) {
            overlayEnabled = false;
            showSummaryOverlayButton.setText("Show Summary Overlay");
            showSummaryOverlayButton.setBackground(BG_LIGHT);
            showSummaryOverlayButton.setForeground(FG_SECONDARY);
            setOverlayCheckboxesEnabled(false);
            removeSummaryOverlays();
        }
    }
    // <<< summary overlay helper methods



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
                    
                    // Update overlay button state when new chart is displayed
                    SummaryStatisticsState statsState = statisticsViewModel.getState();
                    updateOverlayButtonState(statsState);
                }
            }
            // <<< visualization

            else if (newValue instanceof SummaryStatisticsState) {
                final SummaryStatisticsState state = (SummaryStatisticsState) newValue;

                if (state.getErrorMessage() != null) {
                    JOptionPane.showMessageDialog(this,
                            state.getErrorMessage(),
                            "Statistics Error",
                            JOptionPane.ERROR_MESSAGE);
                    // Show error in table
                    statsTableModel.setRowCount(0);
                    statsTableModel.addRow(new Object[]{"Error", state.getErrorMessage()});
                    updateOverlayButtonState(state);
                } else if (state.isCalculating()) {
                    // Show loading state
                    statsTableModel.setRowCount(0);
                    statsTableModel.addRow(new Object[]{"Calculating...", ""});
                    updateOverlayButtonState(state);
                } else if (state.getColumnStats() != null) {
                    displayStatisticsTable(state.getColumnStats());
                    updateOverlayButtonState(state);
                    // If overlay is enabled, refresh it with new statistics
                    if (overlayEnabled) {
                        applySummaryOverlays();
                    }
                } else {
                    updateOverlayButtonState(state);
                }
            }
            //end
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
    // <<< visualization

    public void setStatisticsController(SummaryStatisticsController statisticsController) {
        this.statisticsController = statisticsController;
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
