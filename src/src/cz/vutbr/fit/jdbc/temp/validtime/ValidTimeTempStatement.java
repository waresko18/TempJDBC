package cz.vutbr.fit.jdbc.temp.validtime;

import cz.vutbr.fit.jdbc.temp.validtime.diff.StatementDifferencer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 *
 * @author Filip Fekiac
 */
public class ValidTimeTempStatement implements Statement {
    protected ValidTimeTempConnection connection;
    protected Statement parent;
    protected ValidTimeTempResultSet results;
    protected StatementDifferencer translator;
    private SQLWarning warning = null;

    public ValidTimeTempStatement(ValidTimeTempConnection _connection, Statement _parent) {
        this.connection = _connection;
        this.parent = _parent;
    }
    
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        translator = new StatementDifferencer(connection);
        results = new ValidTimeTempResultSet(parent.executeQuery(sql));
        translator.difference(sql, results);
        return results;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        translator = new StatementDifferencer(connection);
        int ret = parent.executeUpdate(sql);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public void close() throws SQLException {
        parent.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return parent.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        parent.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return parent.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        parent.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return parent.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        parent.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        parent.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (warning == null) {
            return parent.getWarnings();
        }
        return warning;
    }

    @Override
    public void clearWarnings() throws SQLException {
        parent.clearWarnings();
        warning = null;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        translator = new StatementDifferencer(connection);
        boolean ret = parent.execute(sql);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return results;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return parent.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return parent.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        parent.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        parent.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
	return parent.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return parent.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return parent.getResultSetType();
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
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return parent.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
         return parent.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        translator = new StatementDifferencer(connection);
        int ret = parent.executeUpdate(sql, autoGeneratedKeys);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {translator = new StatementDifferencer(connection);
        int ret = parent.executeUpdate(sql, columnIndexes);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        translator = new StatementDifferencer(connection);
        int ret = parent.executeUpdate(sql, columnNames);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        translator = new StatementDifferencer(connection);
        boolean ret = parent.execute(sql, autoGeneratedKeys);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        translator = new StatementDifferencer(connection);
        boolean ret = parent.execute(sql, columnIndexes);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        translator = new StatementDifferencer(connection);
        boolean ret = parent.execute(sql, columnNames);
        translator.difference(sql, null);
        return ret;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return parent.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return parent.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        parent.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return parent.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
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
