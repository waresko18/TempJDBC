package cz.vutbr.fit.jdbc.temp.validtime.diff;

import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TempDifference;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import cz.vutbr.fit.jdbc.temp.validtime.Settings;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * trieda implementuje metody stierajuce rozdiely medzi operaciami v inach systemoch
 * upravuje ukladane metadata pri vykonani DB dopytu v inom systeme tak aby vyzerali 
 * @author Filip Fekiac
 */
public class ValidTimeDifference implements TempDifference {

    @Override
    public void createTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        // jedna sa o tabulku so spravovanym casom preto sa vytvoria potrebne stlpce
        if (info.validTimeSupport == TimeType.STATE || info.validTimeSupport == TimeType.EVENT) {
            stmt.execute("ALTER TABLE " + info.tableName 
                    + " ADD (PERIOD FOR " + Settings.DefaultPeriodName + ")");
            stmt.execute("INSERT INTO " + Settings.ValidTimeTables + ""
                    + "(table_name, start_column, end_column) VALUES "
                    + "('" + info.tableName.toUpperCase() + "', "
                    + "'" + Settings.DefaultPeriodNameRaw + "_start', "
                    + "'" + Settings.DefaultPeriodNameRaw + "_end')");
        }
        stmt.close();
    }
    
    @Override
    public void dropTableDiff(Connection _con, CreateTableInfo info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        // odstranenie zaznamov pre tabulku z metadat systemu
        boolean s = stmt.execute("DELETE FROM " + Settings.ValidTimeTables 
                + " WHERE table_name = "
                + "'" + info.tableName.toUpperCase() + "'");
        stmt.close();
    }
    
}
