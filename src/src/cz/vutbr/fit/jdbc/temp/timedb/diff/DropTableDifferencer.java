package cz.vutbr.fit.jdbc.temp.timedb.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempConnection;
import java.sql.SQLException;

/**
 * trieda obsluhuje operaciu mazania tabulky ma za ulohu prevod
 * dopytu do vseobecnej podoby pouzivanej systemom
 * @author Filip Fekiac
 */
public class DropTableDifferencer implements IDifferencer {
    private final CreateTableInfo tableInfo = new CreateTableInfo();
    private final TimeDBTempConnection connection;
    
    public DropTableDifferencer(String _name, TimeDBTempConnection _con) {
        tableInfo.tableName = _name;
        connection = _con;
    }
    
    @Override
    public void difference() throws SQLException {
        // zavolaju sa metody pre stieranie rozdielov vsetkych podporovanych systemov
        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.dropTableDiff(connection.getDBConnection(), tableInfo);
        }
        
        // odstrani sa trigger
        TriggersDifference.deleteTableTriggers(connection.getDBConnection(), tableInfo);
    }
    
}
