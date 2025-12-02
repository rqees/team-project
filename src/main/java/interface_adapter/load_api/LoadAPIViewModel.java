package interface_adapter.load_api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoadAPIViewModel {
    private String errorMessage;
    private boolean success;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    public void setErrorMessage(String errorMessage) {
        String old = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", old, errorMessage);
    }

    public void setSuccess(boolean success) {
        boolean old = this.success;
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