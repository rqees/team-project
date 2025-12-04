package interface_adapter.statistics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for Summary Statistics.
 */
public class SummaryStatisticsViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SummaryStatisticsState state = new SummaryStatisticsState();

    /**
     * state for summary stats.
     * @param state the state
     */
    public void setState(SummaryStatisticsState state) {
        final SummaryStatisticsState oldState = this.state;
        this.state = state;
        support.firePropertyChange("state", oldState, state);
    }

    public SummaryStatisticsState getState() {
        return state;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void firePropertyChange() {
        support.firePropertyChange("state", null, state);
    }
}
