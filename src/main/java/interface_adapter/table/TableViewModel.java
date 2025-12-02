package interface_adapter.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TableViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private TableState state = new TableState();

    public void setState(TableState state) {
        TableState oldState = this.state;
        this.state = state;
        support.firePropertyChange("state", oldState, state);
    }

    public TableState getState() {
        return state;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}