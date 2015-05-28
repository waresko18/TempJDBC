package cz.vutbr.fit.jdbc.temp.timedb.connector;

public interface ResultRowAdapterInterface {

    /**
     * Get number of columns in row.
     * @return the number of columns in row
     */
    public int getLength();

    /**
     * Get a value of a row in given column.
     * @param col the column, where to get the value
     * @return null if col<0 or col>=length, otherwise the value in the column
     */
    public String getColumnValue(int col);

    /**
     * Get a type of a row in given column.
     * @param col the column, where to get the type
     * @return null if col<0 or col>=length, otherwise the type in the column
     */
    public String getColumnType(int col);

    /**
     * Goest to the next row in a result.
     * @return the next row of the result
     */
    public ResultRowAdapterInterface getNext();

}
