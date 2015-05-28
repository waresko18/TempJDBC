package cz.vutbr.fit.jdbc.temp;

import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempConnection;
import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempImplementation;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempConnection;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempImplementation;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempConnection;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempImplementation;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * definicia podporovanych systemov
 * @author Filip Fekiac
 */
public enum ConnectionType {
    // definicia jednotlivych tovarni
    TSQL2LIB(TSQL2LIBTempConnection.class, TSQL2LIBTempImplementation.class),
    TIMEDB(TimeDBTempConnection.class, TimeDBTempImplementation.class),
    ORACLEVALIDTIME(ValidTimeTempConnection.class, ValidTimeTempImplementation.class);

    // ---------- DO NOT CHANGE ----------- //
    private Class connection;
    private Class implementation;

    private ConnectionType(Class _conn, Class _impl) {
        connection = _conn;
        implementation = _impl;
    }

    /***
     * vrati triedu implementujucu pripojeniepre dany typ systemu
     * @return 
     */
    public Class getMyClass() {
        return connection;
    }

    /***
     * pripojenie na DB pre danu databazu
     * @param _con vlastnosti pripojenia
     * @return 
     */
    public TempImplementation instantiateImplementation(Properties _con) {

        try {
            try {
                return (TempImplementation) implementation.getConstructor(Properties.class).newInstance(_con);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InstantiationException ex) {
            Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ConnectionType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /***
     * vrati vsetky ostatne DB systemy okrem mojho zvoleneho
     * @return 
     */
    public List<Class> getAnotherClasses() {
        List<Class> result = new LinkedList<>();
        for (ConnectionType item : ConnectionType.values()) {
            if (!item.equals(this)) {
                result.add(item.connection);
            }
        }
        return result;
    }
    
    /***
     * vytvori spojenie na vsetky ostatne DB systemy okrem zvoleneho
     * @param _con
     * @return 
     */
    public List<TempConnection> instatiateAnotherConnections(Properties _con) {
        List<TempConnection> anotherConnections = new LinkedList<>();
        for (Class<TempConnection> item : getAnotherClasses()) {
            try {
                TempConnection tempCon = item.getConstructor(Properties.class, Boolean.class).newInstance(_con, Boolean.TRUE);
                if (!tempCon.isValid()) {
                    System.err.println("Unable to instantiate this pack.");
                    continue;
                }
                anotherConnections.add(tempCon);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                System.err.println(ex.getMessage());
                throw new UnsupportedOperationException("Not implemented required constructor.");
            }
        }
        return anotherConnections;
    }
}
