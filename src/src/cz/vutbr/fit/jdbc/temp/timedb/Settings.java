package cz.vutbr.fit.jdbc.temp.timedb;

/**
 * Trieda sprostredkovava vsetky konstatntne definovane pomenovania potrebne pri behu pre tento system
 * @author Filip Fekiac
 */
public class Settings {
    public static char QUOTE = '"';
    public static char STRING_QUOTE = '\'';
    public static String TableTypesNameRaw = "TABLE_TYPES"; // tabulka typov
    public static String TableTypesName = "";
    public static String TableViewSchemesNameRaw = "TABLE_VIEW_SCHEMES"; // tabulka pre DB schemu
    public static String TableViewSchemesName = "";
    // nazvy stlpcov
    public static String ValidTimeStartColumnNameRaw = "vts_timeDB";
    public static String ValidTimeEndColumnNameRaw = "vte_timeDB";
    public static String ValidTimeStartColumnName = "";
    public static String ValidTimeEndColumnName = "";
    public static String TransactionTimeStartColumnNameRaw = "tts_timeDB";
    public static String TransactionTimeEndColumnNameRaw = "tte_timeDB";
    public static String TransactionTimeStartColumnName = "";
    public static String TransactionTimeEndColumnName = "";

    // inicializacia premennych pre konkretny databazovy system
    public static void init() {
        TableTypesName = QUOTE + TableTypesNameRaw + QUOTE;
        TableViewSchemesName = QUOTE + TableViewSchemesNameRaw + QUOTE;
        ValidTimeStartColumnName = QUOTE + ValidTimeStartColumnNameRaw.toUpperCase() + QUOTE;
        ValidTimeEndColumnName = QUOTE + ValidTimeEndColumnNameRaw.toUpperCase() + QUOTE;
        TransactionTimeStartColumnName = QUOTE + TransactionTimeStartColumnNameRaw.toUpperCase() + QUOTE;
        TransactionTimeEndColumnName = QUOTE + TransactionTimeEndColumnNameRaw.toUpperCase() + QUOTE;
    }
}
