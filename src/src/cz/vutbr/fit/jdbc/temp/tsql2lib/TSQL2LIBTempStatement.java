package cz.vutbr.fit.jdbc.temp.tsql2lib;

import cz.vutbr.fit.jdbc.temp.tsql2lib.diff.StatementDifferencer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import tsql2lib.TSQL2ResultSet;
import tsql2lib.TSQL2Statement;
import tsql2lib.parser.TSQL2ParserAdapter;

/**
 * trieda pre rozhranie JDBC implementujuca prikaz a spustajuca metody pre jeho internu analyzu
 * @author Filip Fekiac
 */
public class TSQL2LIBTempStatement implements Statement {
    protected TSQL2LIBTempConnection connection;
    protected TSQL2Statement parent;
    protected TSQL2ParserAdapter parser;
    protected TSQL2LIBTempResultSet results;
    protected StatementDifferencer translator;
    private SQLWarning warning = null;

    public TSQL2LIBTempStatement(TSQL2LIBTempConnection _connection, TSQL2Statement _parent) {
        this.connection = _connection;
        this.parent = _parent;
        parser = new TSQL2ParserAdapter();
        
    }
    
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        TSQL2ResultSet originalResults = (TSQL2ResultSet) parent.executeQuery(sql);
        results = new TSQL2LIBTempResultSet(originalResults);
        translator.difference(parser.parse(sql), results);
        return results;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        int ret = parent.executeUpdate(sql);
        translator.difference(parser.parse(sql), null);
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
        translator.difference(parser.parse(sql), null);
        return ret;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return results;
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
    public void setFetchDirection(int direction) throws SQLException {
        parent.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return parent.getFetchDirection();
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
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return parent.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        int ret = parent.executeUpdate(sql, autoGeneratedKeys);
        translator.difference(parser.parse(sql), null);
        return ret; 
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        int ret = parent.executeUpdate(sql, columnIndexes);
        translator.difference(parser.parse(sql), null);
        return ret;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        int ret = parent.executeUpdate(sql, columnNames);
        translator.difference(parser.parse(sql), null);
        return ret;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        boolean ret = parent.execute(sql, autoGeneratedKeys);
        translator.difference(parser.parse(sql), null);
        return ret;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        boolean ret = parent.execute(sql, columnIndexes);
        translator.difference(parser.parse(sql), null);
        return ret;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        translator = new StatementDifferencer(connection);
        
        boolean ret = parent.execute(sql, columnNames);
        translator.difference(parser.parse(sql), null);
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
