package cz.vutbr.fit.jdbc.temp.difference;

import java.util.HashMap;

/**
 * struktura pre ukladanie informacii o tabulke
 * @author Filip Fekiac
 */
public class CreateTableInfo {
    public TimeType validTimeSupport = TimeType.NONE;
    public TimeType transactionTimeSupport = TimeType.NONE;
    public DateTimeScale validTimeScale = DateTimeScale.SECOND;
    public String tableName = "";
    public long vacuumCutOff = 0L;
    public boolean vacuumCutOffRelative = false;
    public HashMap<String, Long> _surrogates = new HashMap();
}
