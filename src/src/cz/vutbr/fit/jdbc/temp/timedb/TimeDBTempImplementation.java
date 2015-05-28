package cz.vutbr.fit.jdbc.temp.timedb;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempImplementation;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementuje tovaren pre abstraktnu tovaren
 * @author Filip Fekiac
 */
public class TimeDBTempImplementation extends TempImplementation {
    public TimeDBTempImplementation(Properties _con) throws SQLException, ClassNotFoundException {
        super(ConnectionType.TIMEDB);
        setTempConnection(new TimeDBTempConnection(_con));
    }
}
