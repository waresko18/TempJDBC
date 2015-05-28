package temporalcli;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Filip Fekiac
 */
public class TemporalCLI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        // pattern pre prikaz prepinania DB
        Pattern switchPattern = Pattern.compile("\\s*OPEN\\s+(?<database>[A-Za-z0-9]+)\\s*", Pattern.CASE_INSENSITIVE);
        // pattern pre prikaz ukoncenia prace
        Pattern quitPattern = Pattern.compile("\\s*QUIT\\s*", Pattern.CASE_INSENSITIVE);
        s.useDelimiter(";");

        // parametre pripojenia k DB
        Properties properties = new Properties();
        properties.setProperty("username", System.getProperty("username"));
        properties.setProperty("password", System.getProperty("password"));
        properties.setProperty("url", System.getProperty("url"));
        properties.setProperty("driver", System.getProperty("driver"));
        properties.setProperty("path", System.getProperty("path"));

        Matcher m;
        String token;
        Connection connection = null;

        while (true) { // nekonecne nacitanie prikazov
            token = s.next();

            m = switchPattern.matcher(token);
            if (m.find()) { // bol zadany prikaz OPEN tak sa pripoji na DB
                String db = m.group("database");
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {}
                }
                connection = new TempConnection(properties, ConnectionType.valueOf(db));
                System.out.println("Connection opened");
                continue;
            }

            m = quitPattern.matcher(token);
            if (m.find()) { // bol zadany prikaz QUIT tak sa ukonci
                break;
            }
            
            if (connection == null) { // kontrola ci je pripojene
                System.out.println("Open connection first.");
                continue;
            }
            
            try { // spustenie prikazu
                Statement stmt = connection.createStatement();
                try {
                    ResultSet rs = stmt.executeQuery(token);
                    ResultSetMetaData md = rs.getMetaData();
                    int size = md.getColumnCount();
                    System.out.println(); // vypis vysledku v textovej tabulke
                    for (int i = 1; i <= size; i++) {
                        System.out.print("|     " + md.getColumnName(i) + "     ");
                    }
                    System.out.println("|");
                    while(rs.next() && !rs.isAfterLast()) {
                        for (int i = 1; i <= size; i++) {
                            System.out.print("| " + rs.getString(i) + " ");
                        }
                        System.out.println("|");
                    }
                    System.out.println("Statement executed");
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    stmt.close();
                }
                stmt.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

        // odpojenie od DB
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
            }
        }
    }

}
