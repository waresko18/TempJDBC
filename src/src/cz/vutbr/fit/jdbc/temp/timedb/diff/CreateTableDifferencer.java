package cz.vutbr.fit.jdbc.temp.timedb.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.timedb.Settings;
import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * trieda obsluhuje operaciu vytvarania novej tabulky ma za ulohu prevod
 * dopytu do vseobecnej podoby pouzivanej systemom
 * @author Filip Fekiac
 */
public class CreateTableDifferencer implements IDifferencer{
    private final CreateTableInfo tableInfo = new CreateTableInfo();
    private final TimeDBTempConnection connection;
    
    public CreateTableDifferencer(String _name, TimeDBTempConnection _con) {
        tableInfo.tableName = _name;
        connection = _con;
    }
    
    /***
     * metoda sa vola ked sa vytvara tabulka jej ulohou je zistit potrebne
     * informacie a ulozit ich do systemovej podoby. Navyse musi vykonat
     * skytie stlpcov s metadatami.
     */
    @Override
    public void difference() throws SQLException { 
        Statement stmt;
        stmt = connection.getDBConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT table_type, time_type "
                + "FROM " + Settings.TableTypesName
                + " WHERE upper(table_name) = '" 
                + tableInfo.tableName.toLowerCase() + "'");
        if (rs.next()) { // ziskal sa vysledok akeho je typu tabulka
            String type = rs.getString("time_type").toLowerCase();
            if (type.equals("validtime")) { // je typu s casom platnosti
                tableInfo.validTimeSupport = TimeType.STATE;
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.ValidTimeStartColumnName 
                    + " INVISIBLE");
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.ValidTimeEndColumnName 
                    + " INVISIBLE");
            } else if (type.equals("transactiontime")) { // je typu transakcny cas
                tableInfo.transactionTimeSupport = TimeType.STATE;
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.TransactionTimeStartColumnName 
                    + " INVISIBLE");
                stmt.execute("ALTER TABLE " + this.tableInfo.tableName 
                    + " MODIFY " + Settings.TransactionTimeEndColumnName 
                    + " INVISIBLE");
            }
        }
        
        rs.close();
        stmt.close();
        
        // nasledne sa zavolaju metody stierajuce roydiely pre kazdy podporovany system
        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.createTableDiff(connection.getDBConnection(), tableInfo);
        }
        
        // vytvori sa DB trigger
        TriggersDifference.createTableTriggers(connection.getDBConnection(), tableInfo);
    }

    
}
