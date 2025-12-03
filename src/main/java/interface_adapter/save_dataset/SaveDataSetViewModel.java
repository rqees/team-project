package interface_adapter.save_dataset;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * View model for save dataset results.
 */
public class SaveDataSetViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String message;
    /**
     * Nullable so the first save result (success or failure) triggers a change event.
     */
    private Boolean success;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setMessage(String message) {
        String old = this.message;
        this.message = message;
        support.firePropertyChange("message", old, message);
    }

    public void setSuccess(boolean success) {
        Boolean old = this.success;
        this.success = success;
        support.firePropertyChange("success", old, Boolean.valueOf(success));
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}
