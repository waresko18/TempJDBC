package cz.vutbr.fit.jdbc.temp.validtime;

/**
 * konstatny potrebne pri praci s tymto systemom
 * @author Filip Fekiac
 */
public class Settings {
    public static char QUOTE = '"';
    public static char STRING_QUOTE = '\'';
    public static String ValidTimeTablesRaw = "_ORACLE_VALIDTIME_TABLES"; // tabulka metadat
    public static String ValidTimeTables = "";
    public static String DefaultPeriodNameRaw = "MY_VALIDTIME_PERIOD"; // nazov periody ak nie je zadany
    public static String DefaultPeriodName = "";

    public static void init() {
        ValidTimeTables = QUOTE + ValidTimeTablesRaw + QUOTE;
        DefaultPeriodName = QUOTE + DefaultPeriodNameRaw + QUOTE;
    }
}
