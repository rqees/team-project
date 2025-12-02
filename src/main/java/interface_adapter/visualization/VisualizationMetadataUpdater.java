package interface_adapter.visualization;

import interface_adapter.table.TableState;
import interface_adapter.table.TableViewModel;
import use_case.dataset.CurrentTableGateway;
import entity.Column;
import entity.DataSet;
import entity.DataType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically updates visualization metadata when table data changes.
 * Listens to TableViewModel state changes and updates VisualizationViewModel accordingly.
 * This removes orchestration responsibility from the view.
 */
public class VisualizationMetadataUpdater implements PropertyChangeListener {
    
    private final VisualizationPresenter visualizationPresenter;
    private final CurrentTableGateway tableGateway;
    
    public VisualizationMetadataUpdater(VisualizationPresenter visualizationPresenter,
                                       CurrentTableGateway tableGateway) {
        this.visualizationPresenter = visualizationPresenter;
        this.tableGateway = tableGateway;
    }
    
    /**
     * Register this updater to listen to TableViewModel changes.
     */
    public void register(TableViewModel tableViewModel) {
        tableViewModel.addPropertyChangeListener(this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && evt.getNewValue() instanceof TableState) {
            TableState tableState = (TableState) evt.getNewValue();
            // Only update metadata if table data was successfully loaded (no error)
            if (tableState.getErrorMessage() == null && tableState.getColumnHeaders() != null) {
                updateMetadata();
            }
        }
    }
    
    private void updateMetadata() {
        if (tableGateway == null || visualizationPresenter == null) {
            return;
        }

        DataSet dataSet = tableGateway.load();
        if (dataSet == null) {
            visualizationPresenter.updateColumnMetadata(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            return;
        }

        List<String> numericColumns = new ArrayList<>();
        List<String> categoricalColumns = new ArrayList<>();
        List<String> allColumns = new ArrayList<>();

        for (Column column : dataSet.getColumns()) {
            String header = column.getHeader();
            allColumns.add(header);
            if (column.getDataType() == DataType.NUMERIC) {
                numericColumns.add(header);
            } else if (column.getDataType() == DataType.CATEGORICAL) {
                categoricalColumns.add(header);
            }
        }

        visualizationPresenter.updateColumnMetadata(
            numericColumns,
            categoricalColumns,
            allColumns
        );
    }
}

