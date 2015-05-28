package cz.vutbr.fit.jdbc.temp;

import cz.vutbr.fit.jdbc.temp.difference.TempDifference;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * trieda sa sprava ako abstraktna tovaren
 * @author Filip Fekiac
 */
public class TempConnection implements Connection {
    private TempImplementation impl = null;
    public TempDifference diff = null;
    
    protected List<TempConnection> anotherConnections = new LinkedList<>();
    
    public TempConnection(Properties _con, ConnectionType _type) {
        impl = _type.instantiateImplementation(_con);
    }
    
    protected TempConnection() {}
    
    public void setImpl(TempImplementation _impl) {
        this.impl = _impl;
    }

    public List<TempConnection> getAnotherConnections() {
        return anotherConnections;
    }
    
    /**
     * define if this class is supported with specified database
     * @return 
     */
    public boolean isValid() {
        return this.impl.getTempConnection().isValid();
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        return this.impl.getTempConnection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.impl.getTempConnection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.impl.getTempConnection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.impl.getTempConnection().nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.impl.getTempConnection().setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.impl.getTempConnection().getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        this.impl.getTempConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {
        this.impl.getTempConnection().rollback();
    }

    @Override
    public void close() throws SQLException {
        this.impl.getTempConnection().close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.impl.getTempConnection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.impl.getTempConnection().getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.impl.getTempConnection().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.impl.getTempConnection().isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.impl.getTempConnection().setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.impl.getTempConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.impl.getTempConnection().setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.impl.getTempConnection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.impl.getTempConnection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.impl.getTempConnection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.impl.getTempConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.impl.getTempConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.impl.getTempConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.impl.getTempConnection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.impl.getTempConnection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.impl.getTempConnection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.impl.getTempConnection().getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.impl.getTempConnection().setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.impl.getTempConnection().setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.impl.getTempConnection().rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.impl.getTempConnection().releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.impl.getTempConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.impl.getTempConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.impl.getTempConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.impl.getTempConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.impl.getTempConnection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.impl.getTempConnection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.impl.getTempConnection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.impl.getTempConnection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.impl.getTempConnection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.impl.getTempConnection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.impl.getTempConnection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.impl.getTempConnection().setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.impl.getTempConnection().setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.impl.getTempConnection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.impl.getTempConnection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.impl.getTempConnection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.impl.getTempConnection().createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.impl.getTempConnection().setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.impl.getTempConnection().getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.impl.getTempConnection().abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.impl.getTempConnection().setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.impl.getTempConnection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.impl.getTempConnection().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.impl.getTempConnection().isWrapperFor(iface);
    }
    
}
