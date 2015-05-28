package cz.vutbr.fit.jdbc.temp.validtime.diff;

import cz.vutbr.fit.jdbc.temp.difference.TempDifferenceException;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempConnection;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempResultSet;
import java.sql.SQLException;

/**
 * parsovanie prikazov do jednotlivych kategorii
 * @author Filip Fekiac
 */
public class StatementDifferencer {
    private ValidTimeTempConnection connection;
    private IDifferencer diff;

    public StatementDifferencer(ValidTimeTempConnection connection) {
        this.connection = connection;
    }

    public void difference(String str, ValidTimeTempResultSet rs) throws TempDifferenceException, SQLException {
        // remove sql comments
        str = str.replaceAll("(--.*)|(#.*)|(((/\\*)+?[\\w\\W]+?(\\*/)+))", "").trim();
        
        String[] splittedSQL;
        splittedSQL = str.split("\\s+|\\(|;", 4);
        if (splittedSQL.length >= 3 && 
                "CREATE".equals(splittedSQL[0].toUpperCase()) && 
                "TABLE".equals(splittedSQL[1].toUpperCase())) { //prikaz pre vytvorenie tabulky
            this.diff = new CreateTableDifferencer(str, splittedSQL[2].toUpperCase(), this.connection);
            this.diff.difference();
            return;
        }
        if (splittedSQL.length >= 3 && 
                "DROP".equals(splittedSQL[0].toUpperCase()) && 
                "TABLE".equals(splittedSQL[1].toUpperCase())) { // prikaz pre vymazanie tabulky
            this.diff = new DropTableDifferencer(splittedSQL[2].toUpperCase(), this.connection);
            this.diff.difference();
            return;
        }
        
        /*
         if (nodeType == "SelectStatement") {
         this._translator = new SelectStatementTranslator(this._con);
         return this._translator.translate(node);
         }*/
    }
}
