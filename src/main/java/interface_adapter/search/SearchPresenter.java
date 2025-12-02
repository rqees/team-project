package interface_adapter.search;

import interface_adapter.ViewManagerModel;
import use_case.search.SearchOutputBoundary;
import use_case.search.SearchOutputData;

/**
 * The Presenter for the Search Use Case.
 */
public class SearchPresenter implements SearchOutputBoundary {
    private final SearchViewModel searchViewModel;
    private final ViewManagerModel viewManagerModel;

    public SearchPresenter(ViewManagerModel viewManagerModel, SearchViewModel searchViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.searchViewModel = searchViewModel;
    }

    @Override
    public void prepareSuccessView(SearchOutputData outputData) {
        final SearchState searchState = searchViewModel.getState();
        searchState.setRow(outputData.getRow());
        searchState.setColumn(outputData.getColumn());
        searchState.setFound(true);
        searchState.setErrorMessage(null);

        searchViewModel.setState(searchState);
        searchViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final SearchState searchState = searchViewModel.getState();
        searchState.setFound(false);
        searchState.setErrorMessage(errorMessage);

        searchViewModel.setState(searchState);
        searchViewModel.firePropertyChange();
    }
}
