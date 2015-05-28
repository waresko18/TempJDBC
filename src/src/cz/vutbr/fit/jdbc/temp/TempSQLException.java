package cz.vutbr.fit.jdbc.temp;

import java.sql.SQLException;

/**
 * vynimka ktora moze vzniknut
 * @author Filip Fekiac
 */
public class TempSQLException extends SQLException {
    public TempSQLException(String msg) {
        super(msg);
    }
}
