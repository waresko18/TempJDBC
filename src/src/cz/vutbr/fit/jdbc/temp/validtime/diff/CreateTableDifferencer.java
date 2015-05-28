package cz.vutbr.fit.jdbc.temp.validtime.diff;

import cz.vutbr.fit.jdbc.temp.TempConnection;
import cz.vutbr.fit.jdbc.temp.difference.CreateTableInfo;
import cz.vutbr.fit.jdbc.temp.difference.TimeType;
import cz.vutbr.fit.jdbc.temp.difference.TriggersDifference;
import cz.vutbr.fit.jdbc.temp.validtime.Settings;
import cz.vutbr.fit.jdbc.temp.validtime.ValidTimeTempConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * trieda obsluhuje operaciu vytvarania novej tabulky ma za ulohu prevod
 * dopytu do vseobecnej podoby pouzivanej systemom
 * @author Filip Fekiac
 */
public class CreateTableDifferencer implements IDifferencer {

    private final CreateTableInfo tableInfo;
    private final ValidTimeTempConnection connection;
    private final String sql;

    public CreateTableDifferencer(String _sql, String _name, ValidTimeTempConnection connection) {
        this.connection = connection;
        this.tableInfo = new CreateTableInfo();
        this.tableInfo.tableName = _name;
        this.sql = _sql;
    }

    @Override
    public void difference() throws SQLException { 
        // zisti o aky typ tabulky sa jedna regexp ci je tam definicia PERIOD
        Pattern patern = Pattern.compile(".*period[\\s]+for[\\s]+"
                + "(?:"
                + "  (?:"
                + "    \""
                + "    (?<periodspecial>[^\"]{1,30})"
                + "    \""
                + "  )|"
                + "  (?<period>[^\",\\(\\)\\s]{1,30})"
                + ")"
                + "[\\s]*"
                + "(?:"
                + "  (?:"
                + "    \\([\\s]*"
                + "    (?:"
                + "      (?:"
                + "        \""
                + "        (?<startspecial>[^\"]{1,30})"
                + "        \""
                + "      )|"
                + "      (?<start>[^\",\\s]{1,30})"
                + "    )"
                + "    [\\s]*,[\\s]*"
                + "    (?:"
                + "      (?:"
                + "        \""
                + "        (?<endspecial>[^\"]{1,30})"
                + "        \""
                + "      )|"
                + "      (?<end>[^\"\\)\\s]{1,30})"
                + "    )"
                + "    [\\s]*\\)[\\s]*[,\\)]"
                + "  )|"
                + "  (?:,|\\))"
                + ")"
                + ".*", Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
        Matcher matcher = patern.matcher(sql);
        if(matcher.find()) {
            String periodName, startName, endName;
            if ((periodName = matcher.group("periodspecial")) != null && !periodName.isEmpty()) {
                tableInfo.validTimeSupport = TimeType.STATE;
            } else if ((periodName = matcher.group("period")) != null && !periodName.isEmpty()) {
                tableInfo.validTimeSupport = TimeType.STATE;
            } else {
                tableInfo.validTimeSupport = TimeType.NONE;
            }
            
            // stlpec so zaciatkom 
            if ((startName = matcher.group("startspecial")) != null && !startName.isEmpty()){}
            else if ((startName = matcher.group("start")) != null && !startName.isEmpty()){}
            else {
                startName = null;
            }
            
            // stlpec s koncom
            if ((endName = matcher.group("endspecial")) != null && !endName.isEmpty()){}
            else if ((endName = matcher.group("end")) != null && !endName.isEmpty()){}
            else {
                endName = null;
            }
            
            if (startName == null && endName == null) {
                startName = periodName + "_start";
                endName = periodName + "_end";
            } else if (startName != null ^ endName != null) { // odlisne hodnoty
                throw new SQLException("Invalid period definition");
            }
            
            //  ulozenie do tabulky s metadatami
            Statement stmt;
            stmt = connection.getDBConnection().createStatement();
            stmt.execute("INSERT INTO " + Settings.ValidTimeTables + ""
                    + "(table_name, "
                    + "start_column, "
                    + "end_column) VALUES ("
                    + "'" + tableInfo.tableName + "', "
                    + "'" + startName + "', "
                    + "'" + endName + "')");
            stmt.close();
        }
        
        // vyvolanie metod na stieranie rozdielov
        for (TempConnection con : connection.getAnotherConnections()) {
            con.diff.createTableDiff(connection.getDBConnection(), tableInfo);
        }

        // vytvor teigger
        TriggersDifference.createTableTriggers(connection.getDBConnection(), tableInfo);
    }
}
