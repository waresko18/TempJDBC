package cz.vutbr.fit.jdbc.temp.tsql2lib;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempImplementation;
import java.sql.SQLException;
import java.util.Properties;

/**
 * tovarenska trieda pre vztvorenie daneho szstemu
 * @author Filip Fekiac
 */
public class TSQL2LIBTempImplementation extends TempImplementation {
    public TSQL2LIBTempImplementation(Properties _con) throws ClassNotFoundException, SQLException {
        super(ConnectionType.TSQL2LIB);
        setTempConnection(new TSQL2LIBTempConnection(_con));
    }
}
