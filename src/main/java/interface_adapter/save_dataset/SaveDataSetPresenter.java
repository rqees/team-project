package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetOutputBoundary;
import use_case.save_dataset.SaveDataSetOutputData;

import javax.swing.*;
import java.awt.*;

/**
 * Presenter that surfaces save results via Swing dialogs.
 */
public class SaveDataSetPresenter implements SaveDataSetOutputBoundary {

    private final Component parent;

    /**
     * Creates a presenter that shows dialog boxes for save results. The parent component (JPanel)
     * is used for dialog placement and can be null when no specific anchor is desired.
     *
     * @param parent parent component for dialog placement
     */
    public SaveDataSetPresenter(Component parent) {
        this.parent = parent;
    }

    /**
     * Displays the save result to the user.
     *
     * @param outputData output data describing the save attempt
     */
    @Override
    public void present(SaveDataSetOutputData outputData) {
        String message = outputData.getMessage();
        int messageType;
        if (outputData.isSuccess()) {
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else {
            messageType = JOptionPane.ERROR_MESSAGE;
        }

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Save Dataset",
                messageType
        );
    }
}
