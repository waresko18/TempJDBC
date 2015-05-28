package cz.vutbr.fit.jdbc.temp.tsql2lib.diff;

import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TempDifference;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import tsql2lib.Settings;

/**
 * trieda implementuje metody stierajuce rozdiely medzi operaciami v inach systemoch
 * upravuje ukladane metadata pri vykonani DB dopytu v inom systeme tak aby vyzerali 
 * @author Filip Fekiac
 */
public class TSQL2LIBDifference implements TempDifference {

    @Override
    public void createTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        // vytvori stlpce v danej tabulke pre ulozenie potrebnych informacii o case
        if (info.validTimeSupport == TimeType.STATE) {
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.ValidTimeStartColumnName 
                    + " NUMBER(20) INVISIBLE");
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.ValidTimeEndColumnName 
                    + " NUMBER(20) INVISIBLE");
        } else if (info.validTimeSupport == TimeType.EVENT) {
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.ValidTimeStartColumnName 
                    + " NUMBER(20) INVISIBLE");
        }
        if (info.transactionTimeSupport == TimeType.STATE) {
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.TransactionTimeStartColumnName 
                    + " NUMBER(20) INVISIBLE");
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD " + Settings.TransactionTimeEndColumnName 
                    + " NUMBER(20) INVISIBLE");
        }
        // ulozi metadata pre dany system
        String sql = "INSERT INTO " + 
                Settings.TemporalSpecTableName + "("
                + "table_name, valid_time, valid_time_scale, "
                + "transaction_time, vacuum_cutoff, "
                + "vacuum_cutoff_relative) VALUES ("
                + "'" + info.tableName.toUpperCase() + "', "
                + "'" + info.validTimeSupport.toString() + "', "
                + "'" + info.validTimeScale.toString() + "', "
                + "'" + info.transactionTimeSupport.toString() + "', "
                + info.vacuumCutOff + ", "
                + ((info.vacuumCutOffRelative)?"1":"0") + ")";
        stmt.execute(sql);
        
        // vytvorenie SURROGATE
        for (Map.Entry<String, Long> item : info._surrogates.entrySet()) {
            stmt.execute("INSERT INTO " + 
                    Settings.SurrogateTableName + "("
                    + "table_name, column_name, next_value) VALUES ("
                    + "'" + info.tableName + "', '" + item.getKey() + "', "
                    + "" + item.getValue() + ")");
        }
        
        stmt.close();
    }
    
    @Override
    public void dropTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        // vymazanie vsetkych zazanmov pre dany system z metadat pre mazanu tabulku
        stmt.execute("DELETE FROM " + Settings.SurrogateTableName 
                + " WHERE upper(table_name) = "
                + "'" + info.tableName.toUpperCase() + "'");
        stmt.execute("DELETE FROM " + Settings.TemporalSpecTableName 
                + " WHERE upper(table_name) = "
                + "'" + info.tableName.toUpperCase() + "'");
        stmt.close();
    }
    
}
