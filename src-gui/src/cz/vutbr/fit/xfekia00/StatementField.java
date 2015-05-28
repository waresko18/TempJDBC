package cz.vutbr.fit.xfekia00;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

/**
 * textova oblast pre zadavanie dopytov
 * @author Filip Fekiac
 */
public class StatementField extends JTextArea {

    private ArrayList<String> parseInput(String sql) {
        ArrayList<String> result;
        
        //parsovanie scriptu na jednotlive dopyty
        result = new ArrayList<>();
        String re = "\\s*" // vymaze prazdne znaky na zaciatku
                + "    (?:(?<mojsql>"   // SQL prikaz, ktory nas zaujima
                + "      (?:" // vsetky alternativy ktore mozu nastat
                + "        (?:\'[^\'\\\\]*(?:\\\\.[^\'\\\\]*)*\')" // retazec v jednoduchych uvodzovkach
                + "      | (?:\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")" // retazec v zlozenych uvodzovkach
                + "      | (?:\\/*[^*]*\\*+([^*\\/][^*]*\\*+)*\\/)" // viacriadkovy komentar
                + "      | (?:\\#.*)"  // jednoriadkovy komentar
                + "      | (?:--.*)" // jednoriadkovy komentar
                + "      | (?:[^\"\';\\#])" // hocijaky znak z tych co nas nezaujimaju
                + "      )+)" // tieto alternativy mozu nastat niekolko krat ale aspon raz
                + "      (?:;|$)" // koniec SQL prikazu ; alebo koncom retazca
                + "    )"; // koniec SQL priakazu ktory nas zaujima
        
        Pattern pattern = Pattern.compile(re, Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
        Matcher matches = pattern.matcher(sql);
        while(matches.find()) {
            String temp = matches.group("mojsql").trim();
            if (temp.isEmpty()) {
                continue;
            }
            result.add(temp);
        }
        
        return result;
    }
    
    /***
     * berie sa vsetko od zaciatku
     * @return 
     */
    public ArrayList<String> parseInputFromStart() {
        return parseInput(getText());
    }
    
    /***
     * berie sa do uvahy len pod kurzorom
     * @return 
     */
    public ArrayList<String> parseInputFromCarret() {
        ArrayList<String> resultWithErr, resultAll, result;
        String DELIMITER = "Đ€Łß¤";
        
        result = new ArrayList<>();
        String sql = getText();
        Integer position = getCaretPosition();
        
        sql = sql.substring(0, position) + DELIMITER + sql.substring(position);
        resultWithErr = parseInput(sql);
        resultAll = parseInput(getText());
        
        int index = 0;
        boolean save = false;
        for (String re : resultWithErr) {
            if (re.contains(DELIMITER)) {
                save = true;
            }
            if (save && index < resultAll.size()) {
                result.add(resultAll.get(index));
                break;
            }
            index++;
        }
        
        return result;
    }
    
}
