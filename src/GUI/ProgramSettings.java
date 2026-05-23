package GUI;

import javax.swing.*;
import java.awt.*;

public class ProgramSettings extends JDialog {
    public ProgramSettings(Frame owner) {
        super(owner, "Einstellungen",true);
        setSize(400,300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    private JTextField steuer1Field;
    private JTextField steuer2Field;
    private JTextField sozialField;

    public ProgramSettings(JFrame parent) {
        super(parent, "Einstellungen", true);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Steuern", createSteuerPanel());
        tabs.addTab("Sozialversicherung", createSozialPanel());

        add(tabs);
        pack();
    }

    private JPanel createSteuerPanel() {
        JPanel panel = new JPanel(new GridLayout(2,3));

        steuer1Field = new JTextField();
        JButton browse1 = new JButton("...");

        browse1.addActionListener(e -> chooseFile(steuer1Field));

        panel.add(new JLabel("Steuer CSV 1:"));
        panel.add(steuer1Field);
        panel.add(browse1);

        // zweites Feld
        steuer2Field = new JTextField();
        JButton browse2 = new JButton("...");

        browse2.addActionListener(e -> chooseFile(steuer2Field));

        panel.add(new JLabel("Steuer CSV 2:"));
        panel.add(steuer2Field);
        panel.add(browse2);

        return panel;

    }


    private JPanel createSozialPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        sozialField = new JTextField();
        JButton browse = new JButton("...");

        browse.addActionListener(e -> chooseFile(sozialField));

        panel.add(new JLabel("Sozial CSV:"));
        panel.add(sozialField);
        panel.add(browse);

        return panel;
    }


    private void chooseFile(JTextField targetField) {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
