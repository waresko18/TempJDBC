package cz.vutbr.fit.jdbc.temp.validtime.diff;

import java.sql.SQLException;


/**
 *
 * @author Filip Fekiac
 */
public interface IDifferencer {
    public void difference() throws SQLException;
}
