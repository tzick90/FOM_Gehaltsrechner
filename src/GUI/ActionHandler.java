package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ActionHandler implements ActionListener {

    private ProgramSettings settingsDialog;

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource() == GUI.resetEingabe) {
            System.out.println("Reset Eingabe..");
        } else if (a.getSource() == GUI.einstellungen) {
            if (settingsDialog == null) {
                settingsDialog = new ProgramSettings(GUI.frame);
            }
            settingsDialog.setVisible(true);
        } else if (a.getSource() == GUI.beenden) {
            System.exit(0);
        }
    }
}
