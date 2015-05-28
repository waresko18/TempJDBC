package cz.vutbr.fit.xfekia00;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * vsetky vysledky z behu jedneho skriptu obsahuje zoznam vysledkov
 * @author Filip Fekiac
 */
public class Results {
    public final LinkedList<Result> results = new LinkedList<>();
    private final HistoryItem history;
    
    public Results() {
        history = new HistoryItem(DatabaseConnection.init(null).databaseName);
    }
    
    public void addResultSet(ResultSet rs, String sql) throws SQLException {
        Result res = new Result(history, results.size());
        res.setTitle(sql);
        if (res.setResultSet(rs)) {
            results.push(res);
        }
    }
    
    public void putToTheHistory() {
        History.init().insertHistoryItem(history);
    }
}
