package cz.vutbr.fit.jdbc.temp.tsql2lib.diff;

import cz.vutbr.fit.jdbc.temp.difference.TempDifferenceException;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempConnection;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempResultSet;
import java.sql.SQLException;
import tsql2lib.parser.SimpleNode;

/**
 * parsovanie prikazov do jednotlivych kategorii
 * @author Filip Fekiac
 */
public class StatementDifferencer {
    private final TSQL2LIBTempConnection connection;
    private IDifferencer diff;

    public StatementDifferencer(TSQL2LIBTempConnection connection) {
        this.connection = connection;
    }

    public void difference(SimpleNode root, TSQL2LIBTempResultSet rs) throws TempDifferenceException, SQLException {
        SimpleNode node = root;
        do {
            String nodeType = node.toString();
            if ("CreateTableStatement".equals(nodeType)) { // vytvorenie tabulky
                this.diff = new CreateTableDifferencer(this.connection);
                this.diff.difference(node);
                return;
            }
            if ("InsertStatement".equals(nodeType)) { // insert
                return;
            }
            if ("UpdateStatement".equals(nodeType)) { // update
                return;
            }
            if ("DeleteStatement".equals(nodeType)) { // delete
                return;
            }
            if ("SelectStatement".equals(nodeType)) { // select
                return;
            }
            if ("DropStatement".equals(nodeType)) { // vymazanie tabulky
                this.diff = new DropTableDifferencer(this.connection);
                this.diff.difference(node);
                return;
            }
            if (node.jjtGetNumChildren() == 0) {
                return;
            }
            node = (SimpleNode) node.jjtGetChild(0);
        } while (node != null);
    }
}
