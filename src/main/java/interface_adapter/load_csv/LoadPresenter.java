package interface_adapter.load_csv;

import use_case.load_csv.LoadOutputBoundary;

public class LoadPresenter implements LoadOutputBoundary {

    public LoadPresenter() {

    }

    @Override
    public void prepareFail(String errorMessage) {
        // TODO tell view or viewmodel or whatever to display this fail message
//        JOptionPane.showMessageDialog(this,
//                "Error reading file: " + errorMessage,
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void prepareSuccess() {
        // TODO file loaded correctly
    }
}
