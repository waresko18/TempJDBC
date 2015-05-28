package cz.vutbr.fit.jdbc.temp.tsql2lib.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.tsql2lib.TSQL2LIBTempConnection;
import java.sql.SQLException;
import tsql2lib.parser.SimpleNode;

/**
 * trieda obsluhuje operaciu mazania tabulky ma za ulohu prevod
 * dopytu do vseobecnej podoby pouzivanej systemom
 * @author Filip Fekiac
 */
public class DropTableDifferencer implements IDifferencer {

    private final CreateTableInfo tableInfo;
    private final TSQL2LIBTempConnection connection;

    public DropTableDifferencer(TSQL2LIBTempConnection connection) {
        this.connection = connection;
        this.tableInfo = new CreateTableInfo();
    }

    @Override
    public void difference(SimpleNode root) throws SQLException {
        SimpleNode node;
        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            node = (SimpleNode) root.jjtGetChild(i);
            if ("TableReference".equals(node.toString())) {
                tableInfo.tableName = node.jjtGetFirstToken().image;
            }
        }

        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.dropTableDiff(connection.getDBConnection(), tableInfo);
        }
        
        TriggersDifference.deleteTableTriggers(connection.getDBConnection(), tableInfo);
    }

}
