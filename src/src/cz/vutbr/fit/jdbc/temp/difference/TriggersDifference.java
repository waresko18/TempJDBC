package cz.vutbr.fit.jdbc.temp.difference;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.TimeZone;

/**
 * implementacia triggera ukladaneho do DB
 * @author Filip Fekiac
 */
public class TriggersDifference {

    /***
     * vytvori trigger v DB pre obsluhu INSERT a UPDATE dopytov 
     * @param _con pripojenie na DB
     * @param _info informacie o tabulke
     * @throws SQLException 
     */
    public static void createTableTriggers(Connection _con, CreateTableInfo _info) throws SQLException {
        DatabaseMetaData meta = _con.getMetaData();
        String dbName = meta.getDatabaseProductName().toLowerCase();
        Statement stmt;
        stmt = _con.createStatement();
        int tz = TimeZone.getDefault().getOffset(new Date().getTime()) / 1000 / 60 / 60;
        if (dbName.contains("oracle")) { //ORACLE
            String startOracleColumn, endOracleColumn;
            // musi sa zastit nazov stlpcov pre oracle validtime
            String str = "SELECT start_column, end_column"
                    + " FROM " + cz.vutbr.fit.jdbc.temp.validtime.Settings.ValidTimeTables
                    + " WHERE table_name = '" + _info.tableName.toUpperCase() + "'";
            ResultSet rs = stmt.executeQuery(str);
            
            if (rs.next()) {
                startOracleColumn = rs.getString(1);
                endOracleColumn = rs.getString(2);
            } else { // nejaka chyba predpokladaj zakladny nazov
                startOracleColumn = cz.vutbr.fit.jdbc.temp.validtime.Settings.DefaultPeriodNameRaw + "_start";
                endOracleColumn = cz.vutbr.fit.jdbc.temp.validtime.Settings.DefaultPeriodNameRaw + "_end";
            }
            
            // definovanie triggera
            str = "CREATE TRIGGER trigger_" + _info.tableName
                    + " BEFORE INSERT OR UPDATE ON " + _info.tableName
                    + " FOR EACH ROW"
                    + " DECLARE"
                    + " temp TIMESTAMP;"
                    + " temp_year NUMERIC;"
                    + " temp_month NUMERIC;"
                    + " temp_day NUMERIC;"
                    + " temp_hour NUMERIC;"
                    + " temp_minute NUMERIC;"
                    + " temp_number NUMERIC;"
                    + " BEGIN"
                    + " CASE" 
                    + " WHEN INSERTING THEN ";

            if (_info.validTimeSupport == TimeType.STATE) {
                // jedna sa o vkladanie zaznamu do systemu TimeDB
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " IS NOT NULL THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour-1) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " temp := temp + numtodsinterval(" + (tz+1) + ", 'HOUR');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " ELSE"
                        // ide o hranicny zaciatocny casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " < 321408000000 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour-1) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " temp := temp + numtodsinterval(" + (tz+1) + ", 'HOUR');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        + " ELSE"
                        // ide o hranicny koncovy casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // vkladanie do systemu tsql2lib
                        + " ELSIF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " IS NOT NULL THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + startOracleColumn + " := temp;"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " < 253402297200 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeEndColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + endOracleColumn + " := temp;"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // vkladanie zaznamu do systemu Oracle Validtime
                        + " ELSIF :NEW." + startOracleColumn + " IS NOT NULL OR :NEW." + endOracleColumn + " IS NOT NULL THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + startOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + startOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + startOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + endOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + endOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + endOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " END IF;"
                        // ide o vkladanie neobmedzeneho intervalu ci iz priamo do DB alebo do Validtime
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " END IF;";
            } else if (_info.validTimeSupport == TimeType.EVENT) {
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " IS NOT NULL THEN "
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " temp := temp + numtodsinterval(" + (tz+1) + ", 'HOUR');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        + " ELSIF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " IS NOT NULL THEN "
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // vkladanie zaznamu do systemu Oracle Validtime
                        + " ELSIF :NEW." + startOracleColumn + " IS NOT NULL OR :NEW." + endOracleColumn + " IS NOT NULL THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + startOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + startOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + startOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        + " :NEW." + endOracleColumn + " := :NEW." + startOracleColumn + ";"
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " END IF;";
            }

            if (_info.transactionTimeSupport == TimeType.STATE) {
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " IS NOT NULL THEN "
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " temp := temp + numtodsinterval(" + (tz+1) + ", 'HOUR');"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := -62167222800;"
                        + " END IF;"
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " < 321408000000 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " temp := temp + numtodsinterval(" + (tz+1) + ", 'HOUR');"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " := 253402297200;"
                        + " END IF;"
                        + " ELSIF :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " IS NOT NULL THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " < 253402297200 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeEndColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " := 9223372036854775807;"
                        + " END IF;"
                        + " ELSE"
                        + " temp := SYS_EXTRACT_UTC(SYSTIMESTAMP);"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " := 253402297200;"
                        + " END IF;";
            }

            // update part
            str += " WHEN UPDATING THEN";
            if (_info.validTimeSupport == TimeType.STATE) {
                // jedna sa o system timeDB
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " != :OLD." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " OR :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " != :OLD." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " ELSE"
                        // ide o hranicny zaciatocny casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " < 321408000000 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        + " ELSE"
                        // ide o hranicny koncovy casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // jedna sa o system tsql2lib
                        + " ELSIF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " != :OLD." + tsql2lib.Settings.ValidTimeStartColumnName + " OR :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " != :OLD." + tsql2lib.Settings.ValidTimeEndColumnName + " THEN"
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + startOracleColumn + " := temp;"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " < 253402297200 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeEndColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + endOracleColumn + " := temp;"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // jedna sa o system oracle valid time
                        + " ELSIF :NEW." + startOracleColumn + " != :OLD." + startOracleColumn + " OR :NEW." + endOracleColumn + " != :OLD." + endOracleColumn + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + startOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + startOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + startOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + endOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + endOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + endOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " END IF;"
                        // ide o vkladanie neobmedzeneho intervalu ci iz priamo do DB alebo do Validtime
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " END IF;";
            } else if (_info.validTimeSupport == TimeType.EVENT) {
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " != :OLD." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        + " ELSE"
                        // ide o hranicny zaciatocny casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // system tsql2lib
                        + " ELSIF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " != :OLD." + tsql2lib.Settings.TransactionTimeStartColumnName + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + startOracleColumn + " := temp;"
                        + " :NEW." + endOracleColumn + " := temp;"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;"
                        // jedna sa o system oracle valid time
                        + " ELSIF :NEW." + startOracleColumn + " != :OLD." + startOracleColumn + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + startOracleColumn + " IS NOT NULL THEN "
                        + " temp := SYS_EXTRACT_UTC(:NEW." + startOracleColumn + ");"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " temp := :NEW." + startOracleColumn + ";"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        + " :NEW." + endOracleColumn + " := :NEW." + startOracleColumn + ";"
                       // ide o vkladanie neobmedzeneho intervalu ci iz priamo do DB alebo do Validtime
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " := -9223372036854775808;"
                        + " :NEW." + tsql2lib.Settings.ValidTimeStartColumnName + " := -62167222800;"
                        + " :NEW." + startOracleColumn + " := null;"
                        + " :NEW." + endOracleColumn + " := null;"
                        + " END IF;";
            }

            if (_info.transactionTimeSupport == TimeType.STATE) {
                str += " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " != :OLD." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeStartColumnName + " OR :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " != :OLD." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " >= 0 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " ELSE"
                        // ide o hranicny zaciatocny casovy udaj
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := -62167222800;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " < 321408000000 THEN "
                        + " temp_year := FLOOR(:NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + "/32140800);"
                        + " temp_number := :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + "-(temp_year*32140800);"
                        + " temp_month := FLOOR(temp_number/2678400);"
                        + " temp_number := temp_number-(temp_month*2678400);"
                        + " temp_day := FLOOR(temp_number/86400);"
                        + " temp_number := temp_number-(temp_day*86400);"
                        + " temp_hour := FLOOR(temp_number/3600);"
                        + " temp_number := temp_number-(temp_hour*3600);"
                        + " temp_minute := FLOOR(temp_number/60);"
                        + " temp_number := temp_number-(temp_minute*60);"
                        + " temp := to_timestamp((to_char(temp_year+1) || '-' || to_char(temp_month+1) || '-' || to_char(temp_day+1) || ' ' || to_char(temp_hour) || ':' || to_char(temp_minute) || ':' || to_char(temp_number)), 'YYYY-MM-DD HH24:MI:SS');"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " ELSE"
                        // ide o hranicny koncovy casovy udaj
                        + " :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " := 253402297200;"
                        + " END IF;"
                        // system tsql2lib
                        + " ELSIF :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " != :OLD." + tsql2lib.Settings.TransactionTimeStartColumnName + " OR :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " != :OLD." + tsql2lib.Settings.TransactionTimeEndColumnName + " THEN "
                        // nejde o hranicny zaciatocny casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " >= -62167222800 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny zaciatocny casovy udaj
                        + " ELSE "
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := -9223372036854775808;"
                        + " END IF;"
                        // nejde o hranicny koncovy casovy udaj
                        + " IF :NEW." + tsql2lib.Settings.ValidTimeEndColumnName + " < 253402297200 THEN "
                        + " temp := timestamp '1970-01-01 00:00:00' + numtodsinterval(:NEW." + tsql2lib.Settings.ValidTimeEndColumnName + ", 'second');"
                        + " temp := temp + numtodsinterval(" + tz + ", 'HOUR');"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        // ide o hranicny koncovy casovy udaj
                        + " ELSE"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.ValidTimeEndColumnName + " := 9223372036854775807;"
                        + " END IF;"
                         + " ELSE"
                        + " temp := SYS_EXTRACT_UTC(SYSTIMESTAMP);"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeStartColumnName + " := (extract(year from temp)-1)*32140800+(extract(month from temp)-1)*2678400+(extract(day from temp)-1)*86400+extract(hour from temp)*3600+extract(minute from temp)*60+extract(second from temp);"
                        + " :NEW." + cz.vutbr.fit.jdbc.temp.timedb.Settings.TransactionTimeEndColumnName + " := 9223372036854775807;"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeStartColumnName + " := (cast(temp as date) - to_date('19700101','YYYYMMDD')) * 86400;"
                        + " :NEW." + tsql2lib.Settings.TransactionTimeEndColumnName + " := 253402297200;"
                        + " END IF;";
            }
            str += " END CASE;"
                    + " END;";
            
            // nejde o snapshot tabulku
            if (_info.validTimeSupport != TimeType.NONE || _info.transactionTimeSupport != TimeType.NONE) {
                stmt.execute(str);
            }
        } else { // ak by chcel niekto pridat podporu pre iny DB system

        }
        stmt.close();
    }

    /***
     * vymaze trigger v DB
     * @param _con pripojenie na DB
     * @param _info informacie o tabulke
     * @throws SQLException 
     */
    public static void deleteTableTriggers(Connection _con, CreateTableInfo _info) throws SQLException {
        Statement stmt;
        stmt = _con.createStatement();
        String str = "DROP TRIGGER trigger_" + _info.tableName;
        try {
            stmt.execute(str);
        } catch (SQLException e) {
        }
        stmt.close();
    }
}
