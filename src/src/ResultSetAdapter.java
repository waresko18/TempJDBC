
import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultRowAdapterInterface;
import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultSetAdapterInterface;
import java.util.Vector;

public class ResultSetAdapter implements ResultSetAdapterInterface {

    private ResultSet passthru = null;

    /**
     * Constructor
     * @param passthru adapted object implementing TDBCInterface
     */
    public ResultSetAdapter(ResultSet passthru) {
        this.passthru = passthru;
    }

    @Override
    public ResultRowAdapterInterface firstRow() {
        ResultRow row = passthru.firstRow();
        if (row == null) {
            return null;
        }
        return new ResultRowAdapter(row);
    }

    @Override
    public ResultRowAdapterInterface nextRow(ResultRowAdapterInterface row) {
        ResultRow r = passthru.nextRow((ResultRow)((ResultRowAdapter)row).passthru);
        if (r == null) {
            return null;
        }
        return new ResultRowAdapter(r);
    }

    @Override
    public String createOutput() {
        return passthru.createOutput();
    }

    @Override
    public String createString() {
        return passthru.createString();
    }

    @Override
    public Vector<String> getHeaders() {
        return passthru.getHeaders();
    }
}
