package cz.vutbr.fit.xfekia00;

/**
 * rozhranie pre udalosti generovane pri praci s DB
 * @author Filip Fekiac
 */
public interface DatabaseActionListener {
    
    /***
     * vykonanie pripojenia na DB
     * @param isConnected 
     */
    public void connectionPerformed(boolean isConnected);
    
    /***
     * ukoncenie skriptu
     * @param results 
     */
    public void runPerformed(Results results);
    
    /***
     * priebeh behu skriptu v %
     * @param progress 
     */
    public void runProgress(double progress);
}
