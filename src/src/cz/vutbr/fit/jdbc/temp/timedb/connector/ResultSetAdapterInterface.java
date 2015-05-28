package cz.vutbr.fit.jdbc.temp.timedb.connector;

import java.util.Vector;

public interface ResultSetAdapterInterface {

    /**
     * Goes to the first row in a result.
     * @return the first row of the result
     */
    public ResultRowAdapterInterface firstRow();

    /**
     * Goest to the next row in a result.
     * @param row an actual row of the result, which goes to the next row
     * @return the next row of the result
     */
    public ResultRowAdapterInterface nextRow(ResultRowAdapterInterface row);

    /**
     * String representation of an actual row as a table.
     * @return string representation of the actual row as a table
     */
    public String createOutput();

    /**
     * String representation of an actual row.
     * @return string representation of the actual row
     */
    public String createString();
    
    /**
     * String representation of all column names.
     * @return string representation of all column names
     */
    public Vector<String> getHeaders();
}
