package cz.vutbr.fit.xfekia00;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * vysledok ziskany z DB pre jeden dopyt
 * @author Filip Fekiac
 */
public class Result extends JTable {
    private String title;
    private final HistoryItem history;
    private final int position;
    
    public Result(HistoryItem _history, int _position) {
        history = _history;
        position = _position;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String _title) {
        this.title = _title;
        history.addCaptionItem(position, title);
    }
    
    /***
     * prevod vysledku do reprezentacie historie
     * @param resultSet
     * @return
     * @throws SQLException 
     */
    public boolean setResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<String[]> data = new ArrayList<>();
        ResultSetMetaData md;
        int size;
        String[] row;
        
        md = resultSet.getMetaData();
        size = md.getColumnCount();
        if (size > 0) {
            String[] header = new String[size];
            // hlavicka
            for (int i=1; i <= size; i++) {
                header[i-1] = md.getColumnName(i);
            }

            // jednotlive riadky
            while (resultSet.next() && !resultSet.isAfterLast()) {
                row = new String[size];
                for (int i=1; i <= size; i++) {
                    row[i-1] = resultSet.getString(i);
                }
                data.add(row);
            }

            String[][] res = new String[data.size()][size];
            data.toArray(res);
            // ulozenie
            history.addResultItem(position, res);
            history.addHeaderItem(position, header);

            DefaultTableModel myDataModel = new DefaultTableModel(res, header);
            setModel(myDataModel);
            return true;
        } else {
            return false;
        }
    }
}
