package interface_adapter.search;

import use_case.search.SearchInputBoundary;
import use_case.search.SearchInputData;

/**
 * Controller for the Search Use Case.
 */
public class SearchController {
    private final SearchInputBoundary searchInteractor;

    public SearchController(SearchInputBoundary searchInteractor) {
        this.searchInteractor = searchInteractor;
    }

    /**
     * Executes the Search Use Case.
     * @param searchTerm the term to search for
     * @param tableData the data to search through
     * @param startRow the row to start searching from
     * @param startColumn the column to start searching from
     */
    public void execute(String searchTerm, String[][] tableData, int startRow, int startColumn) {
        final SearchInputData searchInputData = new SearchInputData(
                searchTerm, tableData, startRow, startColumn);
        searchInteractor.execute(searchInputData);
    }
}
