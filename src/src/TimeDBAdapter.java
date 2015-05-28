
import cz.vutbr.fit.jdbc.temp.timedb.connector.TDBCAdapterInterface;

// must by in the unnamed package because of TDBCI's unnamed package (bug 4361575)

public class TimeDBAdapter {
    
    public static TDBCAdapterInterface initTimeDB() {
        // Create a new TDBCAdapter object with a new TDBCI object
        return new TDBCAdapter(new TDBCI());
    }

}
