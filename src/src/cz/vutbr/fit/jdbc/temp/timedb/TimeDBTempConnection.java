package cz.vutbr.fit.jdbc.temp.timedb;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.TempSQLException;
import static cz.vutbr.fit.jdbc.temp.timedb.Settings.QUOTE;
import static cz.vutbr.fit.jdbc.temp.timedb.Settings.STRING_QUOTE;
import cz.vutbr.fit.jdbc.temp.timedb.connector.TDBCAdapterInterface;
import cz.vutbr.fit.jdbc.temp.timedb.diff.TimeDBDifference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * pripojenie na DB TimeDB jedna sa o COnnection a tovaren pre zmazanie rozdielov
 * @author Filip Fekiac
 */
public class TimeDBTempConnection extends TempConnection implements Connection {
    private TDBCAdapterInterface realConnection = null;
    private Connection DBConnection = null;
    private boolean isValid = true;

    public TimeDBTempConnection(Properties info) throws SQLException, ClassNotFoundException {
        this(info, false);
    }
    
    /***
     * kontruktor sa automaticky pripoji na vsetky potrebne DB systemy
     */
    public TimeDBTempConnection(Properties info, Boolean isSecondary) throws SQLException, ClassNotFoundException {
        super();

        // ziskanie parametrov pre pripojenie k DB
        String driver = info.getProperty("driver");
        if (driver == null) {
            throw new TempSQLException("No driver property set");
        }
        String user = info.getProperty("username");
        if (user == null) {
            throw new TempSQLException("No username property set");
        }
        String pass = info.getProperty("password");
        if (pass == null) {
            throw new TempSQLException("No password property set");
        }
        String url = info.getProperty("url");
        if (url == null) {
            throw new TempSQLException("No url property set");
        }
        String path = info.getProperty("path");
        if (path == null) {
            throw new TempSQLException("No path property set");
        }

        // vytvor spojenie na DB        
        Class.forName(driver);
        DBConnection = DriverManager.getConnection(url, user, pass);

        // detekuje aky databazovy server sa pouziva
        DatabaseMetaData dmd = DBConnection.getMetaData();
        int dbType = 1;
        String name = dmd.getDatabaseProductName().toLowerCase();
        if (name.contains("oracle")) {
            dbType = 1;
            QUOTE = '"';
            STRING_QUOTE = '\'';
        } else if (name.contains("ase")
                || name.contains("sql anywhere")
                || name.contains("sybase")
                || name.contains("adaptive server")) {
            dbType = 2;
            QUOTE = '"';
            STRING_QUOTE = '\'';
        } else if (name.contains("toursdb")
                || name.contains("cloudspace")
                || name.contains("apache derby")) {
            dbType = 3;
            QUOTE = '"';
            STRING_QUOTE = '\'';
        } else if (name.contains("mysql")){
            QUOTE = '`';
            STRING_QUOTE = '\'';
        } else {
            isValid = false;
        }
        //inicializuj konstanty na zaklade DB
        Settings.init();
        
        try {
            if (!isSecondary) { // jedna sa o primarne pripojenie pre ktore sa budu definovat dopyty
                // vykonanie volania cez adapter
                Class test = Class.forName("TimeDBAdapter");
                Method m = test.getDeclaredMethod("initTimeDB");
                realConnection = (TDBCAdapterInterface) m.invoke(null);
                realConnection.setPrefs(path, dbType, driver, url);
                init();
                if (!realConnection.openDB(user, pass)) {
                    isValid = false;
                    throw new SQLException("Unable to open connection to TimeDB");
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            isValid = false;
            throw new SQLException("Unable to open connection to TimeDB, because there is no TimeDBAdapter");
        }

        diff = new TimeDBDifference();
        
        if (!isSecondary) { 
            // pre druhorade preklenovacie pripojenia sa nevytvara dalsie preklenovanie by sa to cyklilo
            anotherConnections = ConnectionType.TIMEDB.instatiateAnotherConnections(info);
        }
    }

    /***
     * automaticka inicializacia databazy ak sa zisti ze neexistuju vsetky potrebne tabulky
     */
    private void init() {
        ResultSet tables = null;
        try {
            boolean exists = true;
            DatabaseMetaData dbm = DBConnection.getMetaData();
            // check if "Table_types and Table_VIEW_SCHEMES" tables are there
            tables = dbm.getTables(null, null, Settings.TableTypesNameRaw, null);
            exists = (tables.next() && tables.getString("TABLE_NAME").equalsIgnoreCase(Settings.TableTypesNameRaw));
            tables.close();
            tables = dbm.getTables(null, null, Settings.TableViewSchemesName, null);
            if (tables.next() && tables.getString("TABLE_NAME").equalsIgnoreCase(Settings.TableViewSchemesName)) {
                if (!exists) { // neexistuje aspon jedna z tabuliek preto vymaz schemu a vytvor nanovo
                    realConnection.clearDB();
                    realConnection.createDB();
                }
            } else {
                if(exists) {
                    realConnection.clearDB();
                }
                realConnection.createDB();
            }
        } catch (SQLException ex) {} finally {
            if (tables != null) {
                try {
                    tables.close();
                } catch (SQLException ex) {}
            }
        }
    }
    
    @Override
    public boolean isValid() {
        return isValid;
    }

    public TDBCAdapterInterface getRealConnection() {
        return realConnection;
    }

    public Connection getDBConnection() {
        return DBConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new TimeDBTempStatement(this);
    }

    @Override
    public void close() throws SQLException {
        if (DBConnection != null) {
            DBConnection.close();
        }
        if (realConnection != null) {
            realConnection.closeDB();
        }
        for (TempConnection item : anotherConnections) {
            item.close();
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return true;
    }

    @Override
    public void commit() throws SQLException {}
    
    // tieto metody nie je mozne podporovat nakolko nie je dostatok metod v rozhrani TimeDB
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
