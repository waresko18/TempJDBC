package cz.vutbr.fit.jdbc.temp.validtime.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.validtime.Settings;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempConnection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * trieda obsluhuje operaciu mazania tabulky ma za ulohu prevod
 * dopytu do vseobecnej podoby pouzivanej systemom
 * @author Filip Fekiac
 */
public class DropTableDifferencer implements IDifferencer {

    private final CreateTableInfo tableInfo;
    private final ValidTimeTempConnection connection;

    public DropTableDifferencer(String _name, ValidTimeTempConnection connection) {
        this.connection = connection;
        this.tableInfo = new CreateTableInfo();
        this.tableInfo.tableName = _name;
    }

    @Override
    public void difference() throws SQLException {
        Statement stmt;
        // vymaze vsetky zaznamy pre danu tabulku v tabulke metadat
        stmt = connection.getDBConnection().createStatement();
        stmt.execute("DELETE FROM " + Settings.ValidTimeTables
                + " WHERE table_name = '" + tableInfo.tableName.toUpperCase() + "'");
        stmt.close();

        // metody pre stiranie rozdielov
        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.dropTableDiff(connection.getDBConnection(), tableInfo);
        }

        // vymaze trigger
        TriggersDifference.deleteTableTriggers(connection.getDBConnection(), tableInfo);
    }

}
