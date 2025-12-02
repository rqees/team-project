
import data_access.InMemoryDataSubsetGateway;
import data_access.InMemorySummaryReportGateway;
import data_access.InMemoryTableGateway;
import data_access.SampleDataLoader;
import entity.DataSubsetSpec;
import interface_adapter.visualization.VisualizationController;
import interface_adapter.visualization.VisualizationPresenter;
import interface_adapter.visualization.VisualizationViewModel;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import use_case.dataset.CurrentTableGateway;
import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;
import use_case.visualization.interactor.VisualizationInteractor;
import use_case.visualization.io.VisualizationInputBoundary;
import use_case.visualization.io.VisualizationInputData;
import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.model.PlotKind;
import use_case.visualization.model.PlotKindModelFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test for the visualization use case.
 * Exercises the end-to-end flow using sample data without requiring a GUI.
 */
public class VisualizationUseCaseTest {

    @Test
    public void testVisualizationUseCaseWithSampleData() {
        // Set up gateways
        CurrentTableGateway tableGateway = new InMemoryTableGateway();
        DataSubsetGateway dataSubsetGateway = new InMemoryDataSubsetGateway(tableGateway);
        SummaryReportGateway summaryReportGateway = new InMemorySummaryReportGateway();

        // Load sample data
        SampleDataLoader.loadSampleData(tableGateway);

        // Set up visualization use case components
        VisualizationViewModel visualizationViewModel = new VisualizationViewModel();
        VisualizationOutputBoundary presenter = new VisualizationPresenter(visualizationViewModel);
        PlotKindModelFactory plotKindModelFactory = new PlotKindModelFactory();
        VisualizationInputBoundary interactor = new VisualizationInteractor(
                dataSubsetGateway,
                summaryReportGateway,
                presenter,
                plotKindModelFactory
        );
        VisualizationController controller = new VisualizationController(interactor, tableGateway, presenter);

        // Create visualization input data
        // Using numeric columns from sample data: Age and Net Worth
        List<String> columnNames = Arrays.asList("Age", "Net Worth");
        List<Integer> rowIndices = Arrays.asList(0, 1, 2, 3, 4); // All 5 rows
        DataSubsetSpec subsetSpec = new DataSubsetSpec("test-subset", columnNames, rowIndices);

        List<String> xColumns = Arrays.asList("Age");
        List<String> yColumns = Arrays.asList("Net Worth");
        int summaryReportId = -1; // No summary report for this test

        VisualizationInputData inputData = new VisualizationInputData(
                summaryReportId,
                PlotKind.SCATTER,
                subsetSpec,
                xColumns,
                yColumns,
                null,
                "Age vs Net Worth"
        );

        // Exercise the visualization use case - should not throw any exceptions
        assertDoesNotThrow(() -> {
            controller.visualize(inputData);
        });

        // Get the chart from the view model and display it
        var state = visualizationViewModel.getState();
        assertNotNull(state, "Visualization state should not be null");
        assertTrue(state.hasChart(), "Visualization should have a chart");
        
        XYChart chart = state.getXyChart();
        assertNotNull(chart, "Chart should not be null");
        
        // Display the chart in a Swing window
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Visualization Test - " + state.getTitle());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // Create and add the chart panel
            XChartPanel<XYChart> chartPanel = new XChartPanel<>(chart);
            frame.add(chartPanel, BorderLayout.CENTER);
            
            frame.pack();
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Close the window after 30 seconds (adjust or remove timer if you want it to stay open)
            Timer timer = new Timer(30000, e -> frame.dispose());
            timer.setRepeats(false);
            timer.start();
        });
        
        // Give Swing time to display the window
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

