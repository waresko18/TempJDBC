package cz.vutbr.fit.xfekia00;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * jedno vykonanie skriptu sa uklada ako polozka historie
 * @author Filip Fekiac
 */
public class HistoryItem {
    private final String usedDatabase; // pouzity databazovy system
    private final LinkedList<String[][]> resultList; // vysledky dopytov
    private final LinkedList<String[]> resultHeader; // hlavicky dopytov
    private final LinkedList<String> resultCaption; // jednotlive dopyty
    private final Calendar date;
    
    public HistoryItem(String _usedDB) {
        usedDatabase = _usedDB;
        resultList = new LinkedList<>();
        resultHeader = new LinkedList<>();
        resultCaption = new LinkedList<>();
        date = Calendar.getInstance();
    }
    
    public void addResultItem(int pos, String[][] _item) {
        resultList.add(pos, _item);
    }
    
    public void addHeaderItem(int pos, String[] _item) {
        resultHeader.add(pos, _item);
    }
    
    public void addCaptionItem(int pos, String _item) {
        resultCaption.add(pos, _item);
    }
    
    public String getUsedDatabase() {
        return usedDatabase;
    }

    public LinkedList<String[][]> getResultList() {
        return resultList;
    }

    public LinkedList<String[]> getResultHeader() {
        return resultHeader;
    }

    public LinkedList<String> getResultCaption() {
        return resultCaption;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return format.format(date.getTime()) + " " + usedDatabase;
    }
}
