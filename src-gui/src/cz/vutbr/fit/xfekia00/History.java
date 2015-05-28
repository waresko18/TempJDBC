package cz.vutbr.fit.xfekia00;

import javax.swing.DefaultListModel;

/**
 * praca s historiou
 * @author Filip Fekiac
 */
public class History extends DefaultListModel<HistoryItem> {
    public static int MAXIMUM_SIZE = 10;
    private static History instance;
    
    private History() {}
    
    public static History init() {
        if (instance == null) {
            instance = new History();
        }
        
        return instance;
    }
    
    /***
     * velkost historickych udajov sa naustale kontroluje aby sa neprekrocila
     * @param item 
     */
    public void insertHistoryItem(HistoryItem item) {
        if (size() >= MAXIMUM_SIZE && MAXIMUM_SIZE > 0) {
            remove(0);
        }
        if (MAXIMUM_SIZE > 0) {
            addElement(item);
        }
    }
}
