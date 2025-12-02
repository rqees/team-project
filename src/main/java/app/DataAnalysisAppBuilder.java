package app;

import data_access.InMemoryTableGateway;
import data_access.SampleDataLoader;
import interface_adapter.ViewManagerModel;
import interface_adapter.load_csv.LoadController;
import interface_adapter.load_csv.LoadPresenter;
import interface_adapter.load_csv.LoadViewModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import interface_adapter.save_dataset.SaveDataSetController;
import interface_adapter.save_dataset.SaveDataSetPresenter;
import interface_adapter.table.TableController;
import interface_adapter.table.TablePresenter;
import interface_adapter.table.TableViewModel;
import use_case.dataset.CurrentTableGateway;
import use_case.load_csv.LoadInputBoundary;
import use_case.load_csv.LoadInteractor;
import use_case.load_csv.LoadOutputBoundary;
import use_case.search.SearchInputBoundary;
import use_case.search.SearchInteractor;
import use_case.search.SearchOutputBoundary;
import use_case.save_dataset.SaveDataSetDataAccessInterface;
import use_case.save_dataset.SaveDataSetInputBoundary;
import use_case.save_dataset.SaveDataSetInteractor;
import use_case.save_dataset.SaveDataSetOutputBoundary;
import use_case.table.DisplayTableInputBoundary;
import use_case.table.DisplayTableInteractor;
import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;
import data_access.FileSaveDataSetDataAccessObject;

public class DataAnalysisAppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private DataSetTableView dataSetTableView;
    private SearchViewModel searchViewModel;
    private TableViewModel tableViewModel;
    private LoadViewModel loadViewModel;
    private final CurrentTableGateway tableGateway = new InMemoryTableGateway();

    public DataAnalysisAppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public DataAnalysisAppBuilder addDataSetTableView() {
        searchViewModel = new SearchViewModel();
        tableViewModel = new TableViewModel();
        loadViewModel = new LoadViewModel();
        dataSetTableView = new DataSetTableView(searchViewModel, tableViewModel, loadViewModel);
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

    public DataAnalysisAppBuilder addLoadUseCase() {
        final LoadOutputBoundary loadOutputBoundary = new LoadPresenter(loadViewModel);
        final LoadInputBoundary loadInteractor = new LoadInteractor(loadOutputBoundary, tableGateway);
        LoadController loadController = new LoadController(loadInteractor);
        dataSetTableView.setLoadController(loadController);
        return this;
    }

    public DataAnalysisAppBuilder addSaveUseCase() {
        final SaveDataSetOutputBoundary saveOutputBoundary = new SaveDataSetPresenter(dataSetTableView);
        final SaveDataSetDataAccessInterface saveDataAccess =
                new FileSaveDataSetDataAccessObject("saved_datasets");
        final SaveDataSetInputBoundary saveInteractor =
                new SaveDataSetInteractor(saveDataAccess, saveOutputBoundary, tableGateway);
        final SaveDataSetController saveController = new SaveDataSetController(saveInteractor);

        dataSetTableView.setSaveController(saveController);
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
