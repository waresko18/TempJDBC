package cz.vutbr.fit.xfekia00;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;

/**
 * pripojenie k temporalnej DB
 * @author Filip Fekiac
 */
public class DatabaseConnection implements ActionListener {
    private static DatabaseConnection instance = null;
    private Connection databaseConnection = null;
    public Properties properties = new Properties();
    private JComboBox<String> comboBox = null;
    public String databaseName = null;
    private final LinkedList<DatabaseActionListener> action = new LinkedList<>();
    
    
    private DatabaseConnection() {
        // parametre pripojenia
        properties.setProperty("username", "xxxxxxxx");
        properties.setProperty("password", "xxxxxxxx");
        properties.setProperty("url", "jdbc:oracle:thin:@localhost:1521:databaza");
        properties.setProperty("driver", "oracle.jdbc.driver.OracleDriver");
        properties.setProperty("path", "C:\\Users\\JDBC\\lib\\class");
    }
    
    /***
     * Pripojenie k DB
     * @return true ak sa podarilo pripojit inak false
     */
    public boolean connect() {
        if (isConnected()) { // ak je pripojeny tak ukonci predchadzajuce a vytvor nove
            try {
                databaseConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // pripojenie na zaklade zvoleneho systemu
        String value = (String) comboBox.getSelectedItem();
        databaseName = value;
        if (value.equals(DatabaseSelector.DEFAULT_CONNECTION)) {
            databaseConnection = initDefaultConnection();
            performAction();
            return isConnected();
        }
        ConnectionType connection = ConnectionType.valueOf(value);
        databaseConnection = new TempConnection(properties, connection);
        performAction();
        return isConnected();
    }
    
    /***
     * uzavretie pripojenia
     */
    public void close() {
        try {
            if (databaseConnection != null) {
                databaseConnection.close();
            }
        } catch (SQLException ex) {}
    }
    
    public Connection getConnection() {
        return databaseConnection;
    }
    
    public boolean isConnected() {
        return databaseConnection != null;
    }
    
    /***
     * jedna sa o singleton v aplikaciii
     * @param _comboBox selector pre databazu
     * @return 
     */
    public static DatabaseConnection init(JComboBox<String> _comboBox) {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        if (_comboBox != null) {
            instance.comboBox = _comboBox;
        }
        return instance;
    }
    
    /***
     * inicializacia pripojenia priamo na DB bez temporalneho systemu
     * @return 
     */
    private Connection initDefaultConnection() {
        try {
            Class.forName(properties.getProperty("driver"));
            Connection con = DriverManager.getConnection(
                properties.getProperty("url"), 
                properties.getProperty("username"), 
                properties.getProperty("password"));
            performAction();
            return con;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            System.err.println("Unable to connect to Default DB");
            performAction();
            return null;
        }
    }
    
    /***
     * registracia pre odber aktivit
     * @param a 
     */
    public void addActionListener(DatabaseActionListener a) {
        action.add(a);
    }
    
    /***
     * vykonanie akcie
     */
    private void performAction() {
        for (DatabaseActionListener item: action) {
            item.connectionPerformed(isConnected());
        }
    }
    
    /***
     * informacia o priebehu 
     * @param progress 
     */
    private void informProgress(double progress) {
        for (DatabaseActionListener item: action) {
            item.runProgress(progress);
        }
    }
    
    /***
     * vratenie vysledkov
     * @param res 
     */
    private void resultReturned(Results res) {
        for (DatabaseActionListener item: action) {
            item.runPerformed(res);
        }
    }
    
    /***
     * vlakno pre spustanie dopytov
     * @param arr 
     */
    private void startThreadRunStatements(ArrayList<String> arr) {
        Results res = new Results();
        try {
            for(int i = 0; i < arr.size(); i++) {
                Statement stmt = databaseConnection.createStatement();
                res.addResultSet(stmt.executeQuery(arr.get(i)), arr.get(i));
                informProgress(((double)(i+1))/arr.size()*100.0);
                stmt.close();
            }
            res.putToTheHistory();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            informProgress(100);
            resultReturned(res);
        }
    }
    
    /***
     * samotne vlakno kde bezia dopyty
     * @param arr 
     */
    public void runStatements(final ArrayList<String> arr) {
        informProgress(0);
        new Thread() {
            @Override
            public void run() {
                startThreadRunStatements(arr);
            }
        }.start();
    }
        
    /***
     * bola zmenena zvolena DB je potrebne znovu pripojit
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isConnected()) { // ak existuje pripojennie odpoj sa
            try {
                databaseConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // pripojenie na DB
        JComboBox combo = (JComboBox) e.getSource();
        String value = (String) combo.getSelectedItem();
        databaseName = value;
        if (value.equals(DatabaseSelector.DEFAULT_CONNECTION)) {
            databaseConnection = initDefaultConnection();
            performAction();
            return;
        }
        ConnectionType connection = ConnectionType.valueOf(value);
        databaseConnection = new TempConnection(properties, connection);
        performAction();
    }
}
