package cz.vutbr.fit.jdbc.temp.validtime;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempImplementation;
import java.sql.SQLException;
import java.util.Properties;

/**
 * konkretna tovaren pre abstraktnu tovaren
 * @author Filip Fekiac
 */
public class ValidTimeTempImplementation extends TempImplementation {
    public ValidTimeTempImplementation(Properties _con) throws ClassNotFoundException, SQLException {
        super(ConnectionType.ORACLEVALIDTIME);
        setTempConnection(new ValidTimeTempConnection(_con));
    }
}
