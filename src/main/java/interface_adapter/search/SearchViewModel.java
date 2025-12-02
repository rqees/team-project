package interface_adapter.search;

import interface_adapter.ViewModel;

/**
 * The View Model for Search functionality.
 */
public class SearchViewModel extends ViewModel<SearchState> {

    public SearchViewModel() {
        super("search");
        setState(new SearchState());
    }
}
