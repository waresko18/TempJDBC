package cz.vutbr.fit.xfekia00;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

/**
 * Trieda pre pracu so subormi. Vykonava import a export scriptov.
 * @author Filip Fekiac
 */
public class FileParser {

    private static FileParser instance = null;
    private JTextArea textArea = null;
    private DatabaseSelector selectorDB = null;

    private FileParser() {
    }

    /**
     * jedna sa o singleton
     * @param _area oblast kde sa nachadzaju prikazy 
     * @param _selector zoznam databazovych systemov
     * @return 
     */
    public static FileParser init(JTextArea _area, DatabaseSelector _selector) {
        if (instance == null) {
            instance = new FileParser();
        }
        if (_area != null) {
            instance.textArea = _area;
        }

        if (_selector != null) {
            instance.selectorDB = _selector;
        }

        return instance;
    }

    public static FileParser init() {
        return init(null, null);
    }

    /***
     * otvori a nacita obsah zadanaeho subora
     * @param file 
     */
    public void openFile(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            String temp, res = "";
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader r = new BufferedReader(isr);
            if((temp = r.readLine()) != null) {
                // ziskanie typu DB systemu z komentara na prvom riadku
                Pattern pattern = Pattern.compile("\\s*##\\s*System\\s*:\\s*(?<system>[a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE);
                Matcher matches = pattern.matcher(temp);
                if (matches.find()) {
                    temp = matches.group("system").trim();
                    if (!temp.isEmpty()) {
                        selectorDB.setSelectedItem(temp);
                    }
                } else {
                    res = temp + "\n";
                }
            } 
            
            while((temp = r.readLine()) != null) { // nacitanie obsahu
                res += temp + "\n";
            }
            
            textArea.setText(res);
        } catch (IOException ex) {}
    }

    /***
     * ulozanie skriptu do suboru 
     * @param file cesta k suboru
     * @return 
     */
    public boolean saveFile(File file) {
        boolean result = true;
        FileOutputStream is = null;
        try {
            is = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            // ulozenie informacie ohladom systemu do prvaho riadka
            w.write("## System: ");
            w.write(selectorDB.getSelectedItem().toString());
            w.write("\n\n");
            w.write(textArea.getText());
            w.close();
            osw.close();
        } catch (IOException ex) {
            result = false;
        } finally {
            try {
                is.close();
            } catch (NullPointerException | IOException ex) {}
        }
        
        return result;
    }
}
