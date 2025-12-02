package interface_adapter.cleaner;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Data Cleaning use case.
 **/
public class DataCleaningViewModel extends ViewModel<DataCleaningState>{

    public DataCleaningViewModel() {
        // view name used by ViewManagerModel to switch screens if needed
        super("data_cleaning_view");
        // default initial state
        setState(new DataCleaningState());
    }

}
