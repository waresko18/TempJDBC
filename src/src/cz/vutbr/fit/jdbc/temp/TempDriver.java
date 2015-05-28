package cz.vutbr.fit.jdbc.temp;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * implementacia ovladaca pre textovy prefix
 * @author Filip Fekiac
 */
public class TempDriver implements Driver {

    public static final String URL_PREFIX = "jdbc:TempLib:";

    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;

    static {
        try {
            TempDriver driverInst = new TempDriver();
            DriverManager.registerDriver(driverInst);
        } catch (SQLException e) {}
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            throw new TempSQLException("Invalid URL");
        }
        
        url = url.replaceFirst(URL_PREFIX, "");
        info.setProperty("url", url);
        
        String type = info.getProperty("type");
        if (type == null) {
            throw new TempSQLException("No type property set");
        }      
        return new TempConnection(info, ConnectionType.valueOf(type));
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        url = url.replaceFirst(URL_PREFIX, "");
        Driver driver = DriverManager.getDriver(url);
        return driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
