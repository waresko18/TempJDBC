package cz.vutbr.fit.jdbc.temp.timedb.connector;

public interface TDBCAdapterInterface {

    /**
     * type of DBMS (Oracle, Sybase, Cloudscape's JBMS)
     */
    public static int cDbmsOracle = 1;
    public static int cDbmsSybase = 2;
    public static int cDbmsCloudscapeJBMS = 3;

    /**
     * Set preferences for connection to database with TimeDB extension
     * @param Path path to initDB scripts for metadata of TimeDB
     * @param DBMS type of DBMS used as a backend (see cDbms... constants)
     * @param JDBCDriver classname of JDBCDriver
     * @param URL connection URL without login and password
     * @return true iff everything is ok
     */
    public boolean setPrefs(String Path, int DBMS, String JDBCDriver,
                            String URL);

    /**
     * Initialise DB with Metadata
     * @return true iff everything is ok
     */
    public boolean createDB();

    /**
     * Uninitialise DB with Metadata
     * @return true iff everything is ok
     */
    public boolean clearDB();

    /**
     * Open Database
     * @param Login login name to the database
     * @param Password password to the database
     * @return true iff everything is ok
     */
    public boolean openDB(String Login, String Password);

    /**
     * Close Database
     */
    public void closeDB();

    /**
     * Execute an ATSQL statement
     * @param stmt statement with ATSQL query or command
     * @return true iff everything is ok
     */
    public ResultSetAdapterInterface execute(String stmt);

}
