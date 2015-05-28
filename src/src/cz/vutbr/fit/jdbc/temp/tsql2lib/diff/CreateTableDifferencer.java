package cz.vutbr.fit.jdbc.temp.tsql2lib.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.DateTimeScale;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import tsql2lib.Settings;
import tsql2lib.parser.SimpleNode;

/**
 * vytvaranie tabuliek
 * @author Filip Fekiac
 */
public class CreateTableDifferencer implements IDifferencer {

    private final CreateTableInfo tableInfo;
    private final TSQL2LIBTempConnection connection;

    public CreateTableDifferencer(TSQL2LIBTempConnection connection) {
        this.connection = connection;
        this.tableInfo = new CreateTableInfo();
    }

    @Override
    public void difference(SimpleNode root) throws SQLException {
        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            String nodeType = node.toString();
            if ("CreateTableName".equals(nodeType)) {
                this.tableInfo.tableName = node.getSourceString();
            }
        }
        
        try {
            // ziskaj priamo z DB typ casoveho udaja vacuuming atd.
            Statement stmt;
            stmt = connection.getDBConnection().createStatement();
            ResultSet rsInfo;
            String sql = "SELECT "
                    + "valid_time, valid_time_scale, transaction_time, "
                    + "vacuum_cutoff, vacuum_cutoff_relative "
                    + "FROM " + Settings.TemporalSpecTableName
                    + " WHERE upper(table_name) = "
                    + "'" + this.tableInfo.tableName.toUpperCase() + "'";
            rsInfo = stmt.executeQuery(sql);
            if (rsInfo.next()) {
                tableInfo.validTimeSupport = TimeType.valueOf(rsInfo.getString("valid_time"));
                tableInfo.validTimeScale = DateTimeScale.valueOf(rsInfo.getString("valid_time_scale"));
                tableInfo.transactionTimeSupport = TimeType.valueOf(rsInfo.getString("transaction_time"));
                tableInfo.vacuumCutOff = rsInfo.getLong("vacuum_cutoff");
                tableInfo.vacuumCutOffRelative = rsInfo.getBoolean("vacuum_cutoff_relative");
            }
            rsInfo.close();
            
            if(this.tableInfo.validTimeSupport == TimeType.STATE) {
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.ValidTimeStartColumnName 
                    + " INVISIBLE");
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.ValidTimeEndColumnName 
                    + " INVISIBLE");
            } else if (this.tableInfo.validTimeSupport == TimeType.EVENT) {
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.ValidTimeStartColumnName 
                    + " INVISIBLE");
            }
            if (this.tableInfo.transactionTimeSupport == TimeType.STATE) {
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.TransactionTimeStartColumnName 
                    + " INVISIBLE");
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.TransactionTimeEndColumnName 
                    + " INVISIBLE");
            }
            
            ResultSet rsSurro;
            rsSurro = stmt.executeQuery("SELECT column_name, next_value "
                    + "FROM " + Settings.SurrogateTableName
                    + " WHERE table_name = '" + this.tableInfo.tableName + "'");
            while (rsSurro.next()) {
                tableInfo._surrogates.put(rsSurro.getString("column_name"), rsSurro.getLong("next_value"));
            }
            rsSurro.close();
            
            stmt.close();
        } catch (SQLException ex) {
            System.err.println("Unable to connect to DB from tsql2lib");
        }

        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.createTableDiff(connection.getDBConnection(), tableInfo);
        }
        
        TriggersDifference.createTableTriggers(connection.getDBConnection(), tableInfo);
    }
}
