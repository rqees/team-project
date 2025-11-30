package app;

import data_access.InMemoryTableGateway;
import data_access.SampleDataLoader;
import interface_adapter.ViewManagerModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import interface_adapter.table.TableController;
import interface_adapter.table.TablePresenter;
import interface_adapter.table.TableViewModel;
import use_case.dataset.CurrentTableGateway;
import use_case.search.SearchInputBoundary;
import use_case.search.SearchInteractor;
import use_case.search.SearchOutputBoundary;
import use_case.table.DisplayTableInputBoundary;
import use_case.table.DisplayTableInteractor;
import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;

public class DataAnalysisAppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private DataSetTableView dataSetTableView;
    private SearchViewModel searchViewModel;
    private TableViewModel tableViewModel;
    private CurrentTableGateway tableGateway;

    public DataAnalysisAppBuilder() {
        cardPanel.setLayout(cardLayout);
        tableGateway = new InMemoryTableGateway();
    }

    public DataAnalysisAppBuilder addDataSetTableView() {
        searchViewModel = new SearchViewModel();
        tableViewModel = new TableViewModel();
        dataSetTableView = new DataSetTableView(searchViewModel, tableViewModel);
        cardPanel.add(dataSetTableView, dataSetTableView.getViewName());
        return this;
    }

    public DataAnalysisAppBuilder addSearchUseCase() {
        final SearchOutputBoundary searchOutputBoundary = new SearchPresenter(
                viewManagerModel, searchViewModel);
        final SearchInputBoundary searchInteractor = new SearchInteractor(searchOutputBoundary);

        final SearchController searchController = new SearchController(searchInteractor);
        dataSetTableView.setSearchController(searchController);
        return this;
    }

    public DataAnalysisAppBuilder addTableDisplayUseCase() {
        final TablePresenter tablePresenter = new TablePresenter(tableViewModel);
        final DisplayTableInputBoundary displayInteractor = new DisplayTableInteractor(
                tableGateway, tablePresenter);

        final TableController tableController = new TableController(displayInteractor);
        dataSetTableView.setTableController(tableController);
        return this;
    }

    /**
     * Loads sample data into the application.
     * This is temporary - will be replaced by Import use case.
     */
    public DataAnalysisAppBuilder loadSampleData() {
        SampleDataLoader.loadSampleData(tableGateway);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Data Analysis Platform");
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