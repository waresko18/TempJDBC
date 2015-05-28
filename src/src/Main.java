
import cz.vutbr.fit.jdbc.temp.ConnectionType;
import cz.vutbr.fit.jdbc.temp.TempConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import tsql2lib.TSQL2Types;
import tsql2lib.TypeMapper;

/**
 *
 * @author Filip Fekiac
 */
public class Main {
    public static void main(String[] args) {
        Properties p = new Properties();
        p.setProperty("username", "c##janicek");
        p.setProperty("password", "Totojmnh1");
        p.setProperty("url", "jdbc:oracle:thin:@localhost:1521:databaza");
        p.setProperty("driver", "oracle.jdbc.driver.OracleDriver");
        p.setProperty("path", "C:\\Users\\ACER.ACER-PC\\disk\\sukromne(f182)\\o2like\\JDBC\\lib\\class");
        
        TempConnection c = new TempConnection(p, ConnectionType.TSQL2LIB);
        TempConnection cc = new TempConnection(p, ConnectionType.TIMEDB);
        TempConnection ccc = new TempConnection(p, ConnectionType.ORACLEVALIDTIME);
        try {
            Statement s = c.createStatement();
            //s.executeQuery("DROP TABLE Lekarnici7");
            s.close();
            s = c.createStatement();
            s.executeQuery("CREATE TABLE Lekarnikci7 ( "
+"id " + TSQL2Types.PERIOD + " NOT NULL PRIMARY KEY, "
+"jmeno " + TypeMapper.get(TSQL2Types.VARCHAR) + "(32) NOT NULL, "
+"specializace "+TypeMapper.get(TSQL2Types.VARCHAR)+"(32) NOT NULL"
+" ) AS VALID STATE DAY");
            
            
            Statement ss = cc.createStatement();
            ss.executeQuery("DROP TABLE nefungujette7;");
            ss.close();
            ss = cc.createStatement();
            ss.executeQuery("CREATE TABLE nefungujette7(X INTEGER) AS VALIDTIME;");
            
            Statement sss = ccc.createStatement();
            sss.executeQuery("DROP TABLE oracle_valid");
            sss.close();
            
            sss = ccc.createStatement();
            sss.executeQuery("CREATE TABLE oracle_valid(id INTEGER NOT NULL PRIMARY KEY, PERIOD FOR new_period)");
            
            s.close();
            ss.close();
            sss.close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
