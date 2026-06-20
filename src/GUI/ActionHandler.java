package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ActionHandler implements ActionListener {

    private ProgramSettings settingsDialog;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == GUI.einstellungen) {
            if (settingsDialog == null) {
                settingsDialog = new ProgramSettings(GUI.frame);
            }
            settingsDialog.setVisible(true);
        } else if (e.getSource() == GUI.beenden) {
            System.exit(0);
        }
    }
}
