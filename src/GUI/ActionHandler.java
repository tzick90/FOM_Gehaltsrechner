package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ActionHandler implements ActionListener {

    private ProgramSettings settingsDialog;

    @Override
    public void actionPerformed(ActionEvent a) {
        if (a.getSource() == GUI.item1) {
            System.out.println("Reset Eingabe..");
        } else if (a.getSource() == GUI.item2) {
            if (settingsDialog == null) {
                settingsDialog = new ProgramSettings(GUI.frame);
            }
            settingsDialog.setVisible(true);
        } else if (a.getSource() == GUI.item3) {
            System.exit(0);
        }
    }
}
