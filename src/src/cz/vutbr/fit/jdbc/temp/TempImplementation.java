package cz.vutbr.fit.jdbc.temp;

/**
 * abstraktna tovaren
 * @author Filip Fekiac
 */
public abstract class TempImplementation {
    private TempConnection tempConnection;
    protected ConnectionType type;

    public TempImplementation(ConnectionType _type) {
        type = _type;
    }
    
    public TempConnection getTempConnection() {
        return tempConnection;
    }

    public void setTempConnection(TempConnection tempConnection) {
        this.tempConnection = tempConnection;
    }
}
