package app;

import interface_adapter.ViewManagerModel;
import interface_adapter.search.SearchController;
import interface_adapter.search.SearchPresenter;
import interface_adapter.search.SearchViewModel;
import use_case.search.SearchInputBoundary;
import use_case.search.SearchInteractor;
import use_case.search.SearchOutputBoundary;
import view.DataSetTableView;

import javax.swing.*;
import java.awt.*;

public class DataAnalysisAppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private DataSetTableView dataSetTableView;
    private SearchViewModel searchViewModel;

    public DataAnalysisAppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public DataAnalysisAppBuilder addDataSetTableView() {
        searchViewModel = new SearchViewModel();
        dataSetTableView = new DataSetTableView(searchViewModel);
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
