package cz.vutbr.fit.jdbc.temp.timedb;

import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultSetAdapterInterface;
import cz.vutbr.fit.jdbc.temp.timedb.diff.StatementDifferencer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * trieda implementujuca JDBC rozhranie pre TimeDB
 * @author Filip Fekiac
 */
public class TimeDBTempStatement implements Statement {
    private final TimeDBTempConnection connection;
    private final StatementDifferencer translator;
    private TimeDBTempResultSet resultSet = null;
    private boolean isClosed = false;
    private boolean autoClose = false;
            
    public TimeDBTempStatement (TimeDBTempConnection _con) {
        connection = _con;
        translator = new StatementDifferencer(connection);
    }
    
    // s dostupnymi metodami TimeDB je mozne implementovat len executeQuery a execute

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        // spustenie dopytu na TimeDB
        ResultSetAdapterInterface rsa = connection.getRealConnection().execute(sql+";");
        String test = rsa.createOutput();
        if (test.toLowerCase().startsWith("error")) { // vo vysledku nastala chyba
            throw new SQLException(rsa.createOutput());
        }
        resultSet = new TimeDBTempResultSet(rsa, this); // spracuj vysledok
        translator.difference(sql, resultSet); // zmaz vzniknute rozdiely
        if (autoClose) { //ak sa ma same uzavriet tak ukonci pripojenie
            close();
        }
        return resultSet;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        ResultSetAdapterInterface rsa = connection.getRealConnection().execute(sql);
        String test = rsa.createOutput();
        if (test.toLowerCase().startsWith("error")) { // vo vysledku nastala chyba
            return false;
        }
        resultSet = new TimeDBTempResultSet(rsa, this); // spracuj vysledok
        try {
            translator.difference(sql, resultSet); // zmaz rozdiely
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        resultSet.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return resultSet.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        resultSet.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return resultSet.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return resultSet.getConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return resultSet.getType();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        autoClose = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return autoClose;
    }
    
    @Override
    public void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        isClosed = true;
    }

    // tieto metody nie je mozne implementovat pomocou dostupnej sady operacii TimeDB
    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void cancel() throws SQLException {
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
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
     @Override
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public void addBatch(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int[] executeBatch() throws SQLException {
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
