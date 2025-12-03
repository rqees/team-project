package interface_adapter.save_dataset;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * View model for save dataset results.
 */
public final class SaveDataSetViewModel {
    /**
     * Supports property change notifications to observers.
     */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Latest message about the save attempt.
     */
    private String message;
    /**
     * Nullable so the first save result (success or failure) triggers a change event.
     */
    private Boolean success;

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setMessage(final String newMessage) {
        final String old = this.message;
        this.message = newMessage;
        support.firePropertyChange("message", old, newMessage);
    }

    public void setSuccess(final boolean newSuccess) {
        final Boolean old = this.success;
        this.success = newSuccess;
        support.firePropertyChange("success", old, Boolean.valueOf(newSuccess));
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}
