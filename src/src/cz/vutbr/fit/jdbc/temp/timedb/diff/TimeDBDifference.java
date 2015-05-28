package cz.vutbr.fit.jdbc.temp.timedb.diff;

import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TempDifference;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import cz.vutbr.fit.jdbc.temp.timedb.Settings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * trieda implementuje metody stierajuce rozdiely medzi operaciami v inach systemoch
 * upravuje ukladane metadata pri vykonani DB dopytu v inom systeme tak aby vyzerali 
 * rovnako
 * @author Filip Fekiac
 */
public class TimeDBDifference implements TempDifference{

    // volane pri operacii vytvarania novej tabulky
    @Override
    public void createTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        if (info.validTimeSupport == TimeType.STATE) { // jedna sa o cas platnosti
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.ValidTimeStartColumnName 
                    + " NUMBER(38) INVISIBLE");
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.ValidTimeEndColumnName 
                    + " NUMBER(38) INVISIBLE");
            stmt.execute("INSERT INTO "
                    + Settings.TableTypesName + "("
                    + "table_name, table_type, time_type) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', 0, 'validtime')");
            //index je dostatocny nakolko limit pre max pocet stlpcov je 1000
            stmt.execute("INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "'" + Settings.ValidTimeStartColumnNameRaw + "', "
                    + "'period', 'not_null', '-1', '10000')"); 
            //index je dostatocny nakolko limit pre max pocet stlpcov je 1000
            stmt.execute("INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "'" + Settings.ValidTimeStartColumnNameRaw + "', "
                    + "'period', 'not_null', '-1', '10001')"); 
        } else if (info.validTimeSupport == TimeType.EVENT) {
            stmt.execute("ALTER TABLE " + info.tableName
                    + " ADD " + Settings.ValidTimeStartColumnName 
                    + " NUMBER(38) INVISIBLE");
            //index je dostatocny nakolko limit pre max pocet stlpcov je 1000
            stmt.execute("INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "'" + Settings.ValidTimeStartColumnNameRaw + "', "
                    + "'period', 'not_null', '-1', '10000')"); 
        }
        
        // odkomentovat ak zacne podpora pre Transakcny cas
        if (info.transactionTimeSupport == TimeType.STATE) {
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.TransactionTimeStartColumnName 
                    + " NUMBER(38) INVISIBLE");
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.TransactionTimeEndColumnName 
                    + " NUMBER(38) INVISIBLE");
            /*
            stmt.execute("INSERT INTO "
                    + Settings.TableTypesName + "("
                    + "table_name, table_type, time_type) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "0, 'transactiontime')");
            //index je dostatocny nakolko limit pre max pocet stlpcov je 1000
            stmt.execute("INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName + "', "
                    + "'" + Settings.TransactionTimeStartColumnNameRaw + "', "
                    + "'period', 'not_null', '-1', '10002')"); 
            //index je dostatocny nakolko limit pre max pocet stlpcov je 1000
            stmt.execute("INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName + "', "
                    + "'" + Settings.TransactionTimeEndColumnNameRaw + "', "
                    + "'period', 'not_null', '-1', '10003')"); 
            */
        }
        if (info.transactionTimeSupport == TimeType.NONE && info.validTimeSupport == TimeType.NONE) {
            stmt.execute("INSERT INTO "
                    + Settings.TableTypesName + "("
                    + "table_name, table_type, time_type) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "0, 'snapshot')");
        }
        
        Statement stmtMeta;
        stmtMeta = _con.createStatement();
        ResultSet rs;
        rs = stmtMeta.executeQuery("SELECT * FROM " + info.tableName);
        ResultSetMetaData rsm = rs.getMetaData();
        int numOfColumns = rsm.getColumnCount();
        String columnName;
        for (int i = 1; i <= numOfColumns; i++) {
            columnName = rsm.getColumnName(i).toLowerCase();
            // preskakuje systemove stlpce aj ked by sa tu nemali objavit lebo su skryte
            if (columnName.equalsIgnoreCase(tsql2lib.Settings.TransactionTimeEndColumnNameRaw) || 
                    columnName.equalsIgnoreCase(tsql2lib.Settings.TransactionTimeStartColumnNameRaw) ||
                    columnName.equalsIgnoreCase(tsql2lib.Settings.ValidTimeStartColumnNameRaw) ||
                    columnName.equalsIgnoreCase(tsql2lib.Settings.ValidTimeEndColumnNameRaw) ||
                    columnName.equalsIgnoreCase(Settings.ValidTimeStartColumnNameRaw) ||
                    columnName.equalsIgnoreCase(Settings.ValidTimeEndColumnNameRaw) ||
                    columnName.equalsIgnoreCase(Settings.TransactionTimeStartColumnNameRaw) ||
                    columnName.equalsIgnoreCase(Settings.TransactionTimeEndColumnNameRaw)) {
                continue;
            }
            
            // ulozenie schemy DB            
            String str = "INSERT INTO "
                    + Settings.TableViewSchemesName + "("
                    + "table_name, column_name, data_type, nullable, "
                    + "column_length, column_id) VALUES ("
                    + "'" + info.tableName.toLowerCase() + "', "
                    + "'" + columnName + "', "
                    + "'" + getMyType(columnName, rsm.getColumnTypeName(i)) +"', "
                    + "'" + ((rsm.isNullable(i) == ResultSetMetaData.columnNullable)?"nullable":"not_null") + "', "
                    + "'" + rsm.getPrecision(i) + "', "
                    + "'" + String.valueOf(i) + "')";
            
            boolean s = stmt.execute(str);
        }
        rs.close();
        stmtMeta.close();
        stmt.close();
    }
    
    private String getMyType(String name, String type) {
        // datove typy je mozne ziskat z DB ale period je nutne rucne
        if (name.equalsIgnoreCase(Settings.TransactionTimeStartColumnNameRaw) || 
                name.equalsIgnoreCase(Settings.TransactionTimeEndColumnNameRaw) ||
                name.equalsIgnoreCase(Settings.ValidTimeStartColumnNameRaw) ||
                name.equalsIgnoreCase(Settings.ValidTimeEndColumnNameRaw) || 
                name.endsWith("s_timeDB") ||
                name.endsWith("e_timeDB")) {
            return "period";
        }
        type = type.toLowerCase();
        if (type.startsWith("varchar")) {
            return "varchar";
        }
        return type;
    }
    
    // rozdielove operacie pre mazanie tabulkzy len vymazu zaznami o danej tabulke z metadat
    @Override
    public void dropTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        stmt.execute("DELETE FROM " + Settings.TableTypesName 
                + " WHERE upper(table_name) = "
                + "'" + info.tableName.toUpperCase() + "'");
        stmt.execute("DELETE FROM " + Settings.TableViewSchemesName 
                + " WHERE upper(table_name) = "
                + "'" + info.tableName.toUpperCase() + "'");
        stmt.close();
    }
    
}
