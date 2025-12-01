// src/main/java/app/DataAnalysisAppBuilder.java
package app;

import data_access.InMemoryTableGateway;
import data_access.InMemoryDataSubsetGateway;
import data_access.InMemorySummaryReportGateway;
import data_access.SampleDataLoader;

import interface_adapter.ViewManagerModel;

import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;

import interface_adapter.table.TableController;
import interface_adapter.table.TablePresenter;
import interface_adapter.table.TableViewModel;

import interface_adapter.visualization.VisualizationController;
import interface_adapter.visualization.VisualizationPresenter;
import interface_adapter.visualization.VisualizationViewModel;

import use_case.dataset.CurrentTableGateway;

import use_case.search.SearchInputBoundary;
import use_case.search.SearchInteractor;
import use_case.search.SearchOutputBoundary;

import use_case.table.DisplayTableInputBoundary;
import use_case.table.DisplayTableInteractor;

import use_case.visualization.gateway.DataSubsetGateway;
import use_case.visualization.gateway.SummaryReportGateway;
import use_case.visualization.interactor.VisualizationInteractor;
import use_case.visualization.io.VisualizationInputBoundary;
import use_case.visualization.io.VisualizationOutputBoundary;
import use_case.visualization.model.PlotKindModelFactory;

import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;

/**
 * Assembles the Clean Architecture "engine" for the Data Analysis app.
 * Creates and wires ViewModels, Controllers, Use Case Interactors, Gateways, and the main View.
 */
public class DataAnalysisAppBuilder {

    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private DataSetTableView dataSetTableView;

    private SearchViewModel searchViewModel;
    private TableViewModel tableViewModel;
    private VisualizationViewModel visualizationViewModel;

    private final CurrentTableGateway tableGateway;

    private final DataSubsetGateway dataSubsetGateway;
    private final SummaryReportGateway summaryReportGateway;

    public DataAnalysisAppBuilder() {
        cardPanel.setLayout(cardLayout);

        // Single current dataset in memory
        this.tableGateway = new InMemoryTableGateway();

        // Visualization gateways, based on the single current dataset
        this.dataSubsetGateway = new InMemoryDataSubsetGateway(tableGateway);
        this.summaryReportGateway = new InMemorySummaryReportGateway();
    }

    /**
     * Creates the main dataset table view and registers it with the card panel.
     */
    public DataAnalysisAppBuilder addDataSetTableView() {
        searchViewModel = new SearchViewModel();
        tableViewModel = new TableViewModel();
        visualizationViewModel = new VisualizationViewModel();

        dataSetTableView = new DataSetTableView(
                searchViewModel,
                tableViewModel,
                visualizationViewModel
        );

        cardPanel.add(dataSetTableView, dataSetTableView.getViewName());
        return this;
    }

    /**
     * Wires the Search use case:
     *  - SearchInteractor
     *  - SearchPresenter
     *  - SearchController
     */
    public DataAnalysisAppBuilder addSearchUseCase() {
        SearchOutputBoundary searchOutputBoundary =
                new SearchPresenter(viewManagerModel, searchViewModel);

        SearchInputBoundary searchInteractor =
                new SearchInteractor(searchOutputBoundary);

        SearchController searchController =
                new SearchController(searchInteractor);

        dataSetTableView.setSearchController(searchController);
        return this;
    }

    /**
     * Wires the Table Display use case:
     *  - DisplayTableInteractor
     *  - TablePresenter
     *  - TableController
     */
    public DataAnalysisAppBuilder addTableDisplayUseCase() {
        TablePresenter tablePresenter = new TablePresenter(tableViewModel);

        DisplayTableInputBoundary displayInteractor =
                new DisplayTableInteractor(tableGateway, tablePresenter);

        TableController tableController =
                new TableController(displayInteractor);

        dataSetTableView.setTableController(tableController);
        return this;
    }

    /**
     * Wires the Visualization use case:
     *  - VisualizationInteractor
     *  - VisualizationPresenter
     *  - VisualizationController
     *
     * Uses:
     *  - DataSubsetGateway (to load numeric subset data)
     *  - SummaryReportGateway (to load SummaryReport for highlighting)
     *  - PlotKindModelFactory (to choose the right VisualizationModel)
     */
    public DataAnalysisAppBuilder addVisualizationUseCase() {
        VisualizationOutputBoundary presenter =
                new VisualizationPresenter(visualizationViewModel);

        PlotKindModelFactory plotKindModelFactory =
                new PlotKindModelFactory(); // assumes a no-arg ctor that registers point + heatmap factories

        VisualizationInputBoundary interactor =
                new VisualizationInteractor(
                        dataSubsetGateway,
                        summaryReportGateway,
                        presenter,
                        plotKindModelFactory
                );

        VisualizationController controller =
                new VisualizationController(interactor);

        dataSetTableView.setVisualizationController(controller);
        return this;
    }

    /**
     * Loads sample data into the current table gateway.
     * Temporary – will be replaced by an Import use case.
     */
    public DataAnalysisAppBuilder loadSampleData() {
        SampleDataLoader.loadSampleData(tableGateway);
        return this;
    }

    /**
     * Builds the main JFrame, attaches the card panel,
     * and sets the initial active view.
     */
    public JFrame build() {
        JFrame application = new JFrame("Data Analysis Platform");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(cardPanel);

        viewManagerModel.setActiveViewName(dataSetTableView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }

    public DataSetTableView getDataSetTableView() {
        return dataSetTableView;
    }
}
