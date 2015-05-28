
import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultSetAdapterInterface;
import cz.vutbr.fit.jdbc.temp.timedb.connector.TDBCAdapterInterface;

public class TDBCAdapter implements TDBCAdapterInterface {

    private TDBCInterface passthru = null;

    /**
     * Constructor
     * @param passthru adapted object implementing TDBCInterface
     */
    public TDBCAdapter(TDBCInterface passthru) {
        this.passthru = passthru;
    }

    @Override
    public boolean setPrefs(String Path, int DBMS, String JDBCDriver,
                            String URL) {
        return passthru.setPrefs(Path, DBMS, JDBCDriver, URL);
    }

    @Override
    public boolean createDB() {
        return passthru.createDB();
    }

    @Override
    public boolean clearDB() {
        return passthru.clearDB();
    }

    @Override
    public boolean openDB(String Login, String Password) {
        return passthru.openDB(Login, Password);
    }

    @Override
    public void closeDB() {
        passthru.closeDB();
    }

    @Override
    public ResultSetAdapterInterface execute(String stmt) {
        return new ResultSetAdapter(passthru.execute(stmt));
    }

}