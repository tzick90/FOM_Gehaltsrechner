package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import core.CsvValidator;


public class ProgramSettings extends JDialog {


    private static final String SETTINGS_FILE = "settings.properties";

    private static JTextField steuerSaetze;
    private static JTextField einkommenssteuerGrenzen;
    private static JTextField bundeslandUndKirchensteuer;
    private static JTextField steuerPauschalen;
    private static JTextField krankenkassen;
    private static JTextField sozialversicherungsSaetze;

    private static JRadioButton professionalModeRadio;
    private static JRadioButton fomModeRadio;


    public ProgramSettings(JFrame parent) {
        super(parent, "Einstellungen", true);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Steuern-CSVs", createSteuerPanel());
        tabs.addTab("Sozialversicherung-CSVs", createSozialPanel());
        tabs.addTab("Modus", createModusPanel());


        dateiPfadeLaden();

        //New Save-Button für Dateipfade & Einstellungen (Modus):
        JButton speichernButton = new JButton("Pfade speichern & Einstellungen anwenden");
        speichernButton.addActionListener(e -> dateiPfadeSpeichern());


        add(tabs, BorderLayout.CENTER);
        add(speichernButton, BorderLayout.SOUTH);

        pack();
    }

    // create first Panel for tax-related CSV-files:
    private JPanel createSteuerPanel() {
        JPanel panel = new JPanel(new GridLayout(2,3));

        // erstes Steuer-Feld
        steuerSaetze = new JTextField(20);
        JButton browseSteuerSaetze = new JButton("...");

        browseSteuerSaetze.addActionListener(e -> chooseFile(steuerSaetze));

        panel.add(new JLabel("Steuer-Sätze:"));
        panel.add(steuerSaetze);
        panel.add(browseSteuerSaetze);

        // zweites Steuer-Feld
        einkommenssteuerGrenzen = new JTextField(20);
        JButton browseEinkommenssteuerGrenzen = new JButton("...");

        browseEinkommenssteuerGrenzen.addActionListener(e -> chooseFile(einkommenssteuerGrenzen));

        panel.add(new JLabel("Einkommenssteuer-Grenzen:"));
        panel.add(einkommenssteuerGrenzen);
        panel.add(browseEinkommenssteuerGrenzen);

        // drittes Steuer-Feld
        bundeslandUndKirchensteuer = new JTextField(20);
        JButton browseBundeslandUndKirchensteuer = new JButton("...");
        browseBundeslandUndKirchensteuer.addActionListener(e -> chooseFile(bundeslandUndKirchensteuer));

        panel.add(new JLabel("Bundesländer:"));
        panel.add((bundeslandUndKirchensteuer));
        panel.add(browseBundeslandUndKirchensteuer);


        // viertes Steuer-Feld
        steuerPauschalen = new JTextField(20);
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
        krankenkassen = new JTextField(20);
        JButton browseKrankenkassen = new JButton("...");

        browseKrankenkassen.addActionListener(e -> chooseFile(krankenkassen));

        panel.add(new JLabel("Krankenkassen:"));
        panel.add(krankenkassen);
        panel.add(browseKrankenkassen);


        // second social security field:
        sozialversicherungsSaetze = new JTextField(20);
        JButton browseSozialversicherungsSaetze = new JButton("...");

        browseSozialversicherungsSaetze.addActionListener(e -> chooseFile(sozialversicherungsSaetze));

        panel.add(new JLabel("Sozialversicherungssätze:"));
        panel.add(sozialversicherungsSaetze);
        panel.add(browseSozialversicherungsSaetze);

        return panel;
    }

    private JPanel createModusPanel() {
        JPanel panel = new JPanel(new GridLayout(2,1,5,5));

        professionalModeRadio = new JRadioButton("Für eine komplette Abgabenberechnung");
        fomModeRadio = new JRadioButton("Projektmodus (vereinfachte Berechnung)");

        ButtonGroup modusGruppe = new ButtonGroup();
        modusGruppe.add(professionalModeRadio);
        modusGruppe.add(fomModeRadio);

        professionalModeRadio.setSelected(true);

        panel.add(professionalModeRadio);
        panel.add(fomModeRadio);

        return panel;

    }

    private void chooseFile(JTextField targetField) {
        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void dateiPfadeSpeichern() {
        Properties properties = new Properties();
        properties.setProperty("steuerSaetzePfad",              steuerSaetze.getText());
        properties.setProperty("einkommenssteuerGrenzenPfad",   einkommenssteuerGrenzen.getText());
        properties.setProperty("bundeslandUndKirchensteuerPfad", bundeslandUndKirchensteuer.getText());
        properties.setProperty("steuerPauschalenPfad",          steuerPauschalen.getText());
        properties.setProperty("krankenkassenPfad",             krankenkassen.getText());
        properties.setProperty("sozialversicherungsSaetzePfad", sozialversicherungsSaetze.getText());


        properties.setProperty("modus", fomModeRadio.isSelected() ? "FOM Projektmodus":"Professional-Modus");

        try (FileWriter fw = new FileWriter(SETTINGS_FILE)) {
            properties.store(fw, "Abgabenrechner Einstellungen");
            JOptionPane.showMessageDialog(this,
                    "Einstellungen wurden gespeichert.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);

            // Hier wird die GUI des Rechners neu geladen, in Abhängigkeit vom gewähltem Modus des Rechners.
            // Auf diese Weise kann der Rechner beide Berechnungsmethoden auch über die GUI abdecken.
            SwingUtilities.invokeLater(GUI::buildGUI);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Speichern:\n" + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dateiPfadeLaden() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return;
        }

        Properties properties = new Properties();
        try (FileReader fr = new FileReader(file)) {
            properties.load(fr);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden der Einstellungen:\n" + e.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Bekannte Einschränkung: Der gespeicherte Modus wird beim Programmstart
        // nicht automatisch wiederhergestellt (Default ist immer Vollmodus).
        // Innerhalb einer laufenden Session funktioniert die Moduswahl korrekt.

        String modus = properties.getProperty("FOM Projektmodus", "Professional-Modus");
        if (modus.equals("Projektmodus")) {
            fomModeRadio.setSelected(true);
        } else {
            professionalModeRadio.setSelected(true);
        }

        steuerSaetze.setText(              properties.getProperty("steuerSaetzePfad", ""));
        einkommenssteuerGrenzen.setText(   properties.getProperty("einkommenssteuerGrenzenPfad", ""));
        bundeslandUndKirchensteuer.setText(properties.getProperty("bundeslandUndKirchensteuerPfad", ""));
        steuerPauschalen.setText(          properties.getProperty("steuerPauschalenPfad", ""));
        krankenkassen.setText(             properties.getProperty("krankenkassenPfad", ""));
        sozialversicherungsSaetze.setText( properties.getProperty("sozialversicherungsSaetzePfad", ""));

    }

    public static List<String> getFehlendePfade() {
        List<String> fehlend = new ArrayList<>();

        if (steuerSaetze == null || steuerSaetze.getText().isBlank())
            fehlend.add("Steuer-Sätze");
        if (einkommenssteuerGrenzen == null || einkommenssteuerGrenzen.getText().isBlank())
            fehlend.add("Einkommenssteuer-Grenzen");
        if (bundeslandUndKirchensteuer == null || bundeslandUndKirchensteuer.getText().isBlank())
            fehlend.add("Bundesländer/Kirchensteuer");
        if (steuerPauschalen == null || steuerPauschalen.getText().isBlank())
            fehlend.add("Steuer-Pauschalen");
        if (krankenkassen == null || krankenkassen.getText().isBlank())
            fehlend.add("Krankenkassen");
        if (sozialversicherungsSaetze == null || sozialversicherungsSaetze.getText().isBlank())
            fehlend.add("Sozialversicherungssätze");

        return fehlend;
    }

    public static List<String> pruefeAlleCsvDateien() {
        List<String> fehler = new ArrayList<>();
        String f;

        f = CsvValidator.pruefeMitJahrCsv(getSteuerSaetzePfad());
        if (f != null) fehler.add("Steuer-Sätze: " + f);

        f = CsvValidator.pruefeMitJahrCsv(getEinkommenssteuerGrenzenPfad());
        if (f != null) fehler.add("Einkommenssteuer-Grenzen: " + f);

        f = CsvValidator.pruefeBundeslaenderCsv(getBundeslandUndKirchensteuerPfad());
        if (f != null) fehler.add("Bundesländer: " + f);

        f = CsvValidator.pruefeMitJahrCsv(getSteuerpauschalenPfad());
        if (f != null) fehler.add("Steuer-Pauschalen: " + f);

        f = CsvValidator.pruefeKrankenkassenCsv(getKrankenkassenPfad());
        if (f != null) fehler.add("Krankenkassen: " + f);

        f = CsvValidator.pruefeMitJahrCsv(getSozialversicherungssaetzePfad());
        if (f != null) fehler.add("Sozialversicherungssätze: " + f);

        return fehler;
    }



    // Getter deklarieren
    public static String getSteuerSaetzePfad()                 { return steuerSaetze != null ? steuerSaetze.getText(): "";}
    public static String getEinkommenssteuerGrenzenPfad()      { return einkommenssteuerGrenzen != null ? einkommenssteuerGrenzen.getText(): "";}
    public static String getBundeslandUndKirchensteuerPfad()   { return bundeslandUndKirchensteuer != null ? bundeslandUndKirchensteuer.getText(): "";}
    public static String getSteuerpauschalenPfad()             { return steuerPauschalen != null ? steuerPauschalen.getText(): "";}
    public static String getKrankenkassenPfad()                { return krankenkassen != null ? krankenkassen.getText(): "";}
    public static String getSozialversicherungssaetzePfad()    { return sozialversicherungsSaetze != null ? sozialversicherungsSaetze.getText(): "";}

    public static String getModus() {
        if (fomModeRadio != null) {
            return fomModeRadio.isSelected() ? "Projektmodus" : "Vollmodus";
        }

        File file = new File(SETTINGS_FILE);
        if (!file.exists()) return "Vollmodus";

        Properties properties = new Properties();
        try (FileReader fr = new FileReader(file)) {
            properties.load(fr);
        } catch (IOException e) {
            return "Vollmodus";
        }
        return properties.getProperty("modus", "Vollmodus");
    }

    public static boolean istProjektmodus() {
        return "Projektmodus".equals(getModus());
    }

}
