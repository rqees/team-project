package interface_adapter.load_api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoadApiViewModel {
    private String errorMessage;
    private boolean success;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Adds a listener to the view model.
     * @param listener PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Changes the error message and fires a property change.
     * @param errorMessage the error that occurred
     */
    public void setErrorMessage(String errorMessage) {
        final String old = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", old, errorMessage);
    }

    /**
     * Changes the success field and fires a property change.
     * @param success whether the interactor succeeded
     */
    public void setSuccess(boolean success) {
        final boolean old = this.success;
        this.success = success;
        support.firePropertyChange("success", old, success);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }
}
