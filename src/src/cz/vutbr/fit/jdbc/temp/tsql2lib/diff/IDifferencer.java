package cz.vutbr.fit.jdbc.temp.tsql2lib.diff;

import java.sql.SQLException;
import tsql2lib.parser.SimpleNode;

/**
 *
 * @author Filip Fekiac
 */
public interface IDifferencer {    
    public void difference(SimpleNode node) throws SQLException;
}
