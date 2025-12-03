package use_case.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SearchInteractor.
 */
class SearchInteractorTest {
    private FakeSearchPresenter fakePresenter;
    private SearchInteractor interactor;

    @BeforeEach
    void setUp() {
        fakePresenter = new FakeSearchPresenter();
        interactor = new SearchInteractor(fakePresenter);
    }

    @Test
    void testExecute_NullSearchTerm_CallsFailView() {
        // Arrange
        String[][] tableData = {{"A", "B"}, {"C", "D"}};
        SearchInputData inputData = new SearchInputData(null, tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.failViewCalled);
        assertEquals("Please enter a search term", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_EmptySearchTerm_CallsFailView() {
        // Arrange
        String[][] tableData = {{"A", "B"}, {"C", "D"}};
        SearchInputData inputData = new SearchInputData("   ", tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.failViewCalled);
        assertEquals("Please enter a search term", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_FoundInCurrentRow_AfterStartColumn() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana", "Cherry"},
                {"Dog", "Elephant", "Fox"}
        };
        SearchInputData inputData = new SearchInputData("cherry", tableData, 0, 1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(2, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_FoundInNextRow() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana"},
                {"Cherry", "Date"},
                {"Elephant", "Fox"}
        };
        SearchInputData inputData = new SearchInputData("elephant", tableData, 0, 1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(2, fakePresenter.outputData.getRow());
        assertEquals(0, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_WrapsAroundToBeginning() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana", "Cherry"},
                {"Dog", "Elephant", "Fox"}
        };
        SearchInputData inputData = new SearchInputData("banana", tableData, 1, 2);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(1, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_CaseInsensitiveSearch() {
        // Arrange
        String[][] tableData = {
                {"APPLE", "banana", "ChErRy"}
        };
        SearchInputData inputData = new SearchInputData("BaNaNa", tableData, 0, -1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(1, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_PartialMatch() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana", "Cherry"}
        };
        SearchInputData inputData = new SearchInputData("ana", tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(1, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_NotFound() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana"},
                {"Cherry", "Date"}
        };
        SearchInputData inputData = new SearchInputData("Zebra", tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.failViewCalled);
        assertEquals("Search term not found: Zebra", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_NullCellValue() {
        // Arrange
        String[][] tableData = {
                {"Apple", null, "Cherry"},
                {"Dog", "Elephant", "Fox"}
        };
        SearchInputData inputData = new SearchInputData("elephant", tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(1, fakePresenter.outputData.getRow());
        assertEquals(1, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_StartFromMiddleOfTable() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana", "Cherry"},
                {"Dog", "Elephant", "Fox"},
                {"Grape", "Honey", "Ice"}
        };
        SearchInputData inputData = new SearchInputData("honey", tableData, 1, 1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(2, fakePresenter.outputData.getRow());
        assertEquals(1, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_SingleCellTable() {
        // Arrange
        String[][] tableData = {{"Apple"}};
        SearchInputData inputData = new SearchInputData("apple", tableData, 0, -1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(0, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_SearchFromLastCell() {
        // Arrange
        String[][] tableData = {
                {"Apple", "Banana"},
                {"Cherry", "Date"}
        };
        SearchInputData inputData = new SearchInputData("apple", tableData, 1, 1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(0, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    @Test
    void testExecute_EmptyTable() {
        // Arrange
        String[][] tableData = {};
        SearchInputData inputData = new SearchInputData("test", tableData, 0, 0);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.failViewCalled);
        assertEquals("Search term not found: test", fakePresenter.errorMessage);
        assertFalse(fakePresenter.successViewCalled);
    }

    @Test
    void testExecute_WhitespaceInSearchTerm() {
        // Arrange
        String[][] tableData = {
                {"Hello World", "Test"}
        };
        SearchInputData inputData = new SearchInputData("hello world", tableData, 0, -1);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(fakePresenter.successViewCalled);
        assertEquals(0, fakePresenter.outputData.getRow());
        assertEquals(0, fakePresenter.outputData.getColumn());
        assertTrue(fakePresenter.outputData.isFound());
    }

    /**
     * Fake implementation of SearchOutputBoundary for testing.
     * Records method calls and captures data.
     */
    private static class FakeSearchPresenter implements SearchOutputBoundary {
        boolean successViewCalled = false;
        boolean failViewCalled = false;
        SearchOutputData outputData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(SearchOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failViewCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}