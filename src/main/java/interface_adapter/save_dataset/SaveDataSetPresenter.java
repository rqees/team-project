package interface_adapter.save_dataset;

import use_case.save_dataset.SaveDataSetOutputBoundary;
import use_case.save_dataset.SaveDataSetOutputData;

import javax.swing.*;
import java.awt.*;

public class SaveDataSetPresenter implements SaveDataSetOutputBoundary {

    private final Component parent;
    private final boolean useDialogs;

    public SaveDataSetPresenter() {
        this.parent = null;
        this.useDialogs = false;
    }

    public SaveDataSetPresenter(Component parent) {
        this.parent = parent;
        this.useDialogs = true;
    }

    @Override
    public void present(SaveDataSetOutputData outputData) {
        String message = outputData.getMessage();

        if (!useDialogs) {
            // Fallback / test mode
            if (outputData.isSuccess()) {
                System.out.println("[SAVE SUCCESS] " + message);
            } else {
                System.err.println("[SAVE FAILURE] " + message);
            }
            return;
        }

        int messageType = outputData.isSuccess()
                ? JOptionPane.INFORMATION_MESSAGE
                : JOptionPane.ERROR_MESSAGE;

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Save Dataset",
                messageType
        );
    }
}
