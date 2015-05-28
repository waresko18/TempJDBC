
import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultRowAdapterInterface;

public class ResultRowAdapter implements ResultRowAdapterInterface {

    protected ResultRow passthru = null;

    /**
     * Constructor
     * @param passthru adapted object implementing TDBCInterface
     */
    public ResultRowAdapter(ResultRow passthru) {
        this.passthru = passthru;
    }

    @Override
    public int getLength() {
        
        return passthru.getLength();
    }

    @Override
    public String getColumnValue(int col) {
        return passthru.getColumnValue(col);
    }

    @Override
    public String getColumnType(int col) {
        return passthru.getColumnType(col);
    }

    @Override
    public ResultRowAdapterInterface getNext() {
        return new ResultRowAdapter(passthru.getNext());
    }
}
