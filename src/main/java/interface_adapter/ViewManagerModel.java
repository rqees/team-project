package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Model for the View Manager, managing which view is currently active.
 */
public class ViewManagerModel {
    private String activeViewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getActiveViewName() {
        return activeViewName;
    }

    public void setActiveViewName(String activeViewName) {
        this.activeViewName = activeViewName;
    }

    public void firePropertyChange() {
        support.firePropertyChange("view", null, this.activeViewName);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
