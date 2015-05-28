package cz.vutbr.fit.jdbc.temp.timedb.diff;

import cz.vutbr.fit.jdbc.temp.difference.TempDifferenceException;
import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempConnection;
import cz.vutbr.fit.jdbc.temp.timedb.TimeDBTempResultSet;
import java.sql.SQLException;

/**
 * trieda vykonava parsovanie zadaneho dopytu aby ho pridelila do spravnej kategorie na spracovanie
 * @author Filip Fekiac
 */
public class StatementDifferencer {

    private final TimeDBTempConnection connection;
    private IDifferencer diff;

    public StatementDifferencer(TimeDBTempConnection connection) {
        this.connection = connection;
    }

    public void difference(String str, TimeDBTempResultSet rs) throws TempDifferenceException, SQLException {
        // remove sql comments
        str = str.replaceAll("(--.*)|(#.*)|(((/\\*)+?[\\w\\W]+?(\\*/)+))", "").trim();
        
        String[] splittedSQL;
        splittedSQL = str.split("\\s+|\\(|;", 4);
        // jedna sa o vytvorenie tabulky
        if (splittedSQL.length >= 3 && 
                "CREATE".equals(splittedSQL[0].toUpperCase()) && 
                "TABLE".equals(splittedSQL[1].toUpperCase())) {
            this.diff = new CreateTableDifferencer(splittedSQL[2].toLowerCase(), this.connection);
            this.diff.difference();
        } else if (splittedSQL.length >= 3 && 
                "DROP".equals(splittedSQL[0].toUpperCase()) && 
                "TABLE".equals(splittedSQL[1].toUpperCase())) { // jedna sa o vymazanie
            this.diff = new DropTableDifferencer(splittedSQL[2].toLowerCase(), this.connection);
            this.diff.difference();
        }
    }
}
