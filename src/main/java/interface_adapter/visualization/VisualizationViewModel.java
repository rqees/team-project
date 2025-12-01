
// src/main/java/interface_adapter/visualization/VisualizationViewModel.java
package interface_adapter.visualization;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the visualization feature.
 * Stores the current VisualizationState and notifies listeners on change.
 */
public class VisualizationViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private VisualizationState state = new VisualizationState(
            null, null, "Visualization", null
    );

    public VisualizationState getState() {
        return state;
    }

    public void setState(VisualizationState newState) {
        VisualizationState oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
