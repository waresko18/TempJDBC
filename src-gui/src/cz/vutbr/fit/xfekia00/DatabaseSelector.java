package cz.vutbr.fit.xfekia00;

import cz.vutbr.fit.jdbc.temp.ConnectionType;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * vyber z podporovanych databazovych systemov nacitany priamo z JDBC ovladaca
 * @author Filip Fekiac
 */
public class DatabaseSelector extends JComboBox<String> {
    public static final String DEFAULT_CONNECTION = "DIRECT";
    
    public DatabaseSelector() {
        // priadnie priameho pripojenia na DB
        ConnectionType[] connections = ConnectionType.values();
        String[] myModel = new String[connections.length + 1];
        myModel[0] = DEFAULT_CONNECTION;
        for (int i = 1; i < myModel.length; i++) {
            myModel[i] = connections[i - 1].name();
        }

        setModel(new DefaultComboBoxModel<>(myModel));
        addActionListener(DatabaseConnection.init(this));
    }
}
