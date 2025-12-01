package use_case.search;

/**
 * The Search Interactor.
 */
public class SearchInteractor implements SearchInputBoundary {
    private final SearchOutputBoundary searchPresenter;

    public SearchInteractor(SearchOutputBoundary searchPresenter) {
        this.searchPresenter = searchPresenter;
    }

    @Override
    public void execute(SearchInputData searchInputData) {
        final String searchTerm = searchInputData.getSearchTerm();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            searchPresenter.prepareFailView("Please enter a search term");
            return;
        }

        final String[][] tableData = searchInputData.getTableData();
        final int startRow = searchInputData.getStartRow();
        final int startCol = searchInputData.getStartColumn();

        // Search through all cells starting from the specified position
        for (int row = startRow; row < tableData.length; row++) {
            int colStart = (row == startRow) ? startCol + 1 : 0;
            for (int col = colStart; col < tableData[row].length; col++) {
                String value = tableData[row][col];
                if (value != null && value.toLowerCase().contains(searchTerm.toLowerCase())) {
                    // Found match
                    SearchOutputData outputData = new SearchOutputData(row, col, true);
                    searchPresenter.prepareSuccessView(outputData);
                    return;
                }
            }
        }

        // If not found from start position, wrap around and search from beginning
        for (int row = 0; row <= startRow; row++) {
            int colEnd = (row == startRow) ? startCol : tableData[row].length;
            for (int col = 0; col < colEnd; col++) {
                String value = tableData[row][col];
                if (value != null && value.toLowerCase().contains(searchTerm.toLowerCase())) {
                    SearchOutputData outputData = new SearchOutputData(row, col, true);
                    searchPresenter.prepareSuccessView(outputData);
                    return;
                }
            }
        }

        // Not found
        searchPresenter.prepareFailView("Search term not found: " + searchTerm);
    }
}
