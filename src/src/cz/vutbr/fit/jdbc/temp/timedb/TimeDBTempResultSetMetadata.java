package cz.vutbr.fit.jdbc.temp.timedb;

import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultRowAdapterInterface;
import cz.vutbr.fit.jdbc.temp.timedb.connector.ResultSetAdapterInterface;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

/**
 * trieda pre ziskanie metadat je potrebne implementovat ako JDBC rozhranie
 * @author Filip Fekiac
 */
public class TimeDBTempResultSetMetadata implements ResultSetMetaData {
    private final ResultSetAdapterInterface rsai;
    private Vector<String> colNames = null;
    
    public TimeDBTempResultSetMetadata(ResultSetAdapterInterface _rsai) {
        rsai = _rsai;
    }    
    
    @Override
    public int getColumnCount() throws SQLException {
        // vrati pocet stlpcov
        ResultRowAdapterInterface row = rsai.firstRow();
        if (row == null) {
            return 0;
        }
        return row.getLength();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return ResultSetMetaData.columnNullableUnknown;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        // vrati typ v danom stlpci
        ResultRowAdapterInterface row = rsai.firstRow();
        if (row == null) {
            return "";
        }
        return row.getColumnType(column);
    }
    
    @Override
    public boolean isSigned(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        // pouzi doimplementovanu metodu pre ziskanie vysledku
        if (colNames == null) {
            colNames = rsai.getHeaders();
        }
        
        return colNames.elementAt(column-1);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getScale(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getTableName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
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
