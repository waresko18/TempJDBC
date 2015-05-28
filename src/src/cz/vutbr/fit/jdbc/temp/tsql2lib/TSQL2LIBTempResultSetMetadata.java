package cz.vutbr.fit.jdbc.temp.tsql2lib;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * zisakvanie metadat pre dany vysledok
 * @author Filip Fekiac
 */
public class TSQL2LIBTempResultSetMetadata implements ResultSetMetaData {
    private final ResultSetMetaData rsMeta;
    
    
    public TSQL2LIBTempResultSetMetadata(ResultSetMetaData _rsmd) {
        rsMeta = _rsmd;
    }
    
    @Override
    public int getColumnCount() throws SQLException {
        return rsMeta.getColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return rsMeta.isAutoIncrement(column);
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return rsMeta.isCaseSensitive(column);
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return rsMeta.isSearchable(column);
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return rsMeta.isCurrency(column);
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return rsMeta.isNullable(column);
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return rsMeta.isSigned(column);
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return rsMeta.getColumnDisplaySize(column);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return rsMeta.getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return rsMeta.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return rsMeta.getSchemaName(column);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return rsMeta.getPrecision(column);
    }

    @Override
    public int getScale(int column) throws SQLException {
        return rsMeta.getScale(column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return rsMeta.getTableName(column);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return rsMeta.getCatalogName(column);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return rsMeta.getColumnType(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return rsMeta.getColumnTypeName(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return rsMeta.isReadOnly(column);
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return rsMeta.isWritable(column);
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return rsMeta.isDefinitelyWritable(column);
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return rsMeta.getColumnClassName(column);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return rsMeta.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return rsMeta.isWrapperFor(iface);
    }
}
