package cz.vutbr.fit.jdbc.temp.difference;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * rozhranie ktore musi implementovat trieda pre stieranie rozdielov
 * @author Filip Fekiac
 */
public interface TempDifference {
    public void createTableDiff(Connection con, CreateTableInfo info)  throws SQLException;
    public void dropTableDiff(Connection con, CreateTableInfo info) throws SQLException;
}
