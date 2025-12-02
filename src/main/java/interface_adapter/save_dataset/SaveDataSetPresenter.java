package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetOutputBoundary;
import use_case.save_dataset.SaveDataSetOutputData;

import javax.swing.*;
import java.awt.*;

/**
 * Presenter that surfaces save results either via dialogs or console output.
 */
public class SaveDataSetPresenter implements SaveDataSetOutputBoundary {

    private final Component parent;
    private final boolean useDialogs;

    /**
     * Creates a presenter that logs output to standard output (console) and standard error (console) (no dialogs).
     */
    public SaveDataSetPresenter() {
        this.parent = null;
        this.useDialogs = false;
    }

    /**
     * Creates a presenter that shows dialog boxes for save results.
     *
     * @param parent parent component for dialog placement
     */
    public SaveDataSetPresenter(Component parent) {
        this.parent = parent;
        this.useDialogs = true;
    }

    /**
     * Displays the save result to the user.
     *
     * @param outputData output data describing the save attempt
     */
    @Override
    public void present(SaveDataSetOutputData outputData) {
        String message = outputData.getMessage();

        if (!useDialogs) {
            if (outputData.isSuccess()) {
                System.out.println("[SAVE SUCCESS] " + message);
            } else {
                System.err.println("[SAVE FAILURE] " + message);
            }
            return;
        }

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
