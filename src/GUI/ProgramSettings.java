package GUI;

import javax.swing.*;
import java.awt.*;


public class ProgramSettings extends JDialog {
    public ProgramSettings(Frame owner) {
        super(owner, "Einstellungen",true);
        setSize(700,500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    private static JTextField steuerSaetze;
    private static JTextField einkommenssteuerGrenzen;
    private static JTextField bundeslandUndKirchensteuer;
    private static JTextField steuerPauschalen;
    private static JTextField krankenkassen;
    private static JTextField sozialversicherungsSaetze;

    public ProgramSettings(JFrame parent) {
        super(parent, "Einstellungen", true);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Steuern-CSVs", createSteuerPanel());
        tabs.addTab("Sozialversicherung-CSVs", createSozialPanel());

        add(tabs);
        pack();
    }

    // create first Panel for tax-related CSV-files:
    private JPanel createSteuerPanel() {
        JPanel panel = new JPanel(new GridLayout(2,3));

        // erstes Steuer-Feld
        steuerSaetze = new JTextField();
        JButton browseSteuerSaetze = new JButton("...");

        browseSteuerSaetze.addActionListener(e -> chooseFile(steuerSaetze));

        panel.add(new JLabel("Steuer-Sätze:"));
        panel.add(steuerSaetze);
        panel.add(browseSteuerSaetze);

        // zweites Steuer-Feld
        einkommenssteuerGrenzen = new JTextField();
        JButton browseEinkommenssteuerGrenzen = new JButton("...");

        browseEinkommenssteuerGrenzen.addActionListener(e -> chooseFile(einkommenssteuerGrenzen));

        panel.add(new JLabel("Einkommenssteuer-Grenzen:"));
        panel.add(einkommenssteuerGrenzen);
        panel.add(browseEinkommenssteuerGrenzen);

        // drittes Steuer-Feld
        bundeslandUndKirchensteuer = new JTextField();
        JButton browseBundeslandUndKirchensteuer = new JButton("...");
        browseBundeslandUndKirchensteuer.addActionListener(e -> chooseFile(bundeslandUndKirchensteuer));

        panel.add(new JLabel("Bundesländer:"));
        panel.add((bundeslandUndKirchensteuer));
        panel.add(browseBundeslandUndKirchensteuer);


        // viertes Steuer-Feld
        steuerPauschalen = new JTextField();
        JButton browseSteuerPauschalen = new JButton("...");
        browseSteuerPauschalen.addActionListener(e -> chooseFile(steuerPauschalen));

        panel.add(new JLabel("Steuer-Pauschalen:"));
        panel.add((steuerPauschalen));
        panel.add(browseSteuerPauschalen);

        return panel;

    }

    // Create second Panel for social-security related CSV-Files:
    private JPanel createSozialPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));


        // erstes social security field:
        krankenkassen = new JTextField();
        JButton browseKrankenkassen = new JButton("...");

        browseKrankenkassen.addActionListener(e -> chooseFile(krankenkassen));

        panel.add(new JLabel("Krankenkassen:"));
        panel.add(krankenkassen);
        panel.add(browseKrankenkassen);


        // second social security field:
        sozialversicherungsSaetze = new JTextField();
        JButton browseSozialversicherungsSaetze = new JButton("...");

        browseSozialversicherungsSaetze.addActionListener(e -> chooseFile(sozialversicherungsSaetze));

        panel.add(new JLabel("Sozialversicherungssätze:"));
        panel.add(sozialversicherungsSaetze);
        panel.add(browseSozialversicherungsSaetze);

        return panel;
    }


    private void chooseFile(JTextField targetField) {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Getter deklarieren
    public static String getSteuerSaetzePfad()                 { return steuerSaetze.getText();}
    public static String getEinkommenssteuerGrenzenPfad()      { return einkommenssteuerGrenzen.getText();}
    public static String getBundeslandUndKirchensteuerPfad()   { return bundeslandUndKirchensteuer.getText();}
    public static String getSteuerpauschalenPfad()             { return steuerPauschalen.getText();}
    public static String getKrankenkassenPfad()                { return krankenkassen.getText();}
    public static String getSozialversicherungssaetzePfad()    { return sozialversicherungsSaetze.getText();}

}
