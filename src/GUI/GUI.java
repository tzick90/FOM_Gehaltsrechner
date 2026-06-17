package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

import core.Sozialabgabenrechner;
import core.CsvReader;
import core.CsvReader.BundeslandInfo;
import core.FuzzyMatcher;
import util.Abgabenrechner;
import util.Ergebnis;
import FOMProjektrechner.FOMGehaltsrechner;

import static GUI.ProgramSettings.*;

public class GUI extends Component {

    static int[] jahr = { Year.now().getValue() };

    static String formatEUR(double wert) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(wert);
    }

    static void ladeBundeslaenderDropdown(JComboBox<String> dropdown, int jahr) {
        if (ProgramSettings.istProjektmodus()) return; // im Projektmodus nicht nötig

        String pfad = ProgramSettings.getBundeslandUndKirchensteuerPfad();
        if (pfad.isBlank()) return;

        try {
            Map<String, CsvReader.BundeslandInfo> bundeslaender =
                    CsvReader.leseBundeslaenderMitJahr(pfad, jahr);

            dropdown.removeAllItems();
            for (String bl : bundeslaender.keySet()) {
                dropdown.addItem(bl);
            }
            if (dropdown.getItemCount() > 0) {
                dropdown.setSelectedIndex(0);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Fehler beim Laden der Bundesländer für Jahr " + jahr + ":\n" + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void ladeKrankenkassenDropdown(JComboBox<String> dropdown, int jahr) {
        if (ProgramSettings.istProjektmodus()) return;

        String pfad = ProgramSettings.getKrankenkassenPfad();
        if (pfad.isBlank()) return;

        try {
            Map<String, Sozialabgabenrechner.KrankenkassenInfo> kassen =
                    CsvReader.leseKrankenkassen(pfad, jahr);

            dropdown.removeAllItems();
            for (String kk : kassen.keySet()) {
                dropdown.addItem(kk);
            }
            if (dropdown.getItemCount() > 0) {
                dropdown.setSelectedIndex(0);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Fehler beim Laden der Krankenkassen für Jahr " + jahr + ":\n" + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }


    static JMenuItem resetEingabe, einstellungen, beenden;
    static JFrame frame;

    public void main(String[] args) {
        SwingUtilities.invokeLater(GUI::buildGUI);   // HIER_12a
    }

    public static void buildGUI() {                  // HIER_12b NEU

        // Bestehendes Fenster schließen, falls vorhanden (z.B. bei Moduswechsel)
        if (frame != null) {
            frame.dispose();
        }

        boolean projektmodus = ProgramSettings.istProjektmodus();

        frame = new JFrame("Gehaltsrechner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1450, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.requestFocus();

        JMenuBar BarMenu = new JMenuBar();
        JMenu Menu = new JMenu("Datei");

        resetEingabe = new JMenuItem("Reset Eingabe");
        resetEingabe.addActionListener(new ActionHandler());

        einstellungen = new JMenuItem("Einstellungen");
        einstellungen.addActionListener(new ActionHandler());

        beenden = new JMenuItem("Beenden");
        beenden.addActionListener(new ActionHandler());


        Menu.add(resetEingabe);
        Menu.add(einstellungen);
        Menu.add(beenden);
        BarMenu.add(Menu);

        BarMenu.add(Box.createHorizontalGlue());
        JLabel modusLabel = new JLabel("Modus: " + ProgramSettings.getModus() + " ");
        BarMenu.add(modusLabel);

        frame.setJMenuBar(BarMenu);

            /*
            LEFT PANEL for INPUT
             */

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Eingabe"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

            /*
            ----------------------------------------------------------------------------------
            Adding the Radio-Buttons to select the fiscal Year
            ---------------------------------------------------------------------------------
             */

        // adding Radio-Button for fiscal year
        JRadioButton FiscalYearPreviousButton = new JRadioButton("Letztes Fiskaljahr"); // TODO: hier kommt noch eine Klasse um dynamisch das lette und das aktuelle FY zu berechnen und hier als String-Wert wiederzugeben
        FiscalYearPreviousButton.setMnemonic(KeyEvent.VK_L);
        FiscalYearPreviousButton.setActionCommand("Letztes Fiskaljahr");

        JRadioButton FiscalYearCurrentButton = new JRadioButton("Aktuelles Fiskaljahr");
        FiscalYearCurrentButton.setMnemonic(KeyEvent.VK_A);
        FiscalYearCurrentButton.setActionCommand("Aktuelles Fiskaljahr");


        // Grouping the RadioButtons
        ButtonGroup FiscalYearButtonsGroup = new ButtonGroup();
        FiscalYearButtonsGroup.add(FiscalYearPreviousButton);
        FiscalYearButtonsGroup.add(FiscalYearCurrentButton);




            /*
            Adding Input-Fields
             */

        JLabel salaryLabel = new JLabel("mon. Gehalt (Brutto)");
        JTextField GrossSalaryField = new JTextField();

        JButton submitButton = new JButton("Berechnen");


        // Field for Age input
        JLabel ageLabel = new JLabel("Alter:");
        JTextField AgeInputField = new JTextField();



        // HIER_4 NEU: blAbkuerzungen jetzt VOR bundeslaender
        Map<String, String> blAbkuerzungen = Map.ofEntries(
                Map.entry("bw", "Baden-Württemberg"),
                Map.entry("by", "Bayern"),
                Map.entry("be", "Berlin"),
                Map.entry("bb", "Brandenburg"),
                Map.entry("hb", "Bremen"),
                Map.entry("hh", "Hamburg"),
                Map.entry("he", "Hessen"),
                Map.entry("mv", "Mecklenburg-Vorpommern"),
                Map.entry("ni", "Niedersachsen"),
                Map.entry("nrw", "Nordrhein-Westfalen"),
                Map.entry("rp", "Rheinland-Pfalz"),
                Map.entry("sl", "Saarland"),
                Map.entry("sn", "Sachsen"),
                Map.entry("st", "Sachsen-Anhalt"),
                Map.entry("sh", "Schleswig-Holstein"),
                Map.entry("th", "Thüringen")
        );

        // Add FuzzyMatch for German-States
        Map<String, BundeslandInfo> bundeslaender;
        if (projektmodus) {
            bundeslaender = new LinkedHashMap<>();
            for (String bl : blAbkuerzungen.values()) {
                bundeslaender.put(bl, new BundeslandInfo(0, ""));
            }
        } else {
            String pfad = ProgramSettings.getBundeslandUndKirchensteuerPfad();   // HIER_19
            if (pfad.isBlank()) {
                bundeslaender = new LinkedHashMap<>(); // Pfad fehlt – FALL 1 fängt das beim Submit ab
            } else {
                try {
                    bundeslaender = CsvReader.leseBundeslaenderMitJahr(pfad, jahr[0]);
                } catch (IOException e) {
                    bundeslaender = new LinkedHashMap<>(); // Datei ungültig – FALL 2 fängt das beim Submit ab
                }
            }
        }

        JLabel bundeslandLabel = new JLabel("Bundesland:");
        JTextField bundeslandSuchfeld = new JTextField(15);

        JComboBox<String> bundeslandDropdown =
                new JComboBox<>(bundeslaender.keySet().toArray(new String[0]));



        Map<String, BundeslandInfo> finalBundeslaender = bundeslaender;
        bundeslandSuchfeld.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void aktualisieren() {
                String eingabe = bundeslandSuchfeld.getText().trim();

                List<String> treffer = FuzzyMatcher.findeAlle(
                        eingabe,
                        finalBundeslaender,
                        blAbkuerzungen
                );

                bundeslandDropdown.removeAllItems();

                if (eingabe.isEmpty()) {
                    for (String bl : finalBundeslaender.keySet()) {
                        bundeslandDropdown.addItem(bl);
                    }
                } else {
                    for (String bl : treffer) {
                        bundeslandDropdown.addItem(bl);
                    }
                }

                if (bundeslandDropdown.getItemCount() > 0) {
                    bundeslandDropdown.setSelectedIndex(0);
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }
        });


        // Add FuzzyMatch for health Insurances
        Map<String, Sozialabgabenrechner.KrankenkassenInfo> krankenkassen;
        if (projektmodus) {
            krankenkassen = new LinkedHashMap<>();
        } else {
            String pfad = ProgramSettings.getKrankenkassenPfad();   // HIER_20
            if (pfad.isBlank()) {
                krankenkassen = new LinkedHashMap<>(); // Pfad fehlt – FALL 1 fängt das beim Submit ab
            } else {
                try {
                    krankenkassen = CsvReader.leseKrankenkassen(pfad, jahr[0]);
                } catch (IOException e) {
                    krankenkassen = new LinkedHashMap<>(); // Datei ungültig – FALL 2 fängt das beim Submit ab
                }
            }
        }

        // Häufigste Abkürzungen manuell
        Map<String, String> kkAbkürzungen = new HashMap<>();
        kkAbkürzungen.put("tk", "Techniker Krankenkasse");
        kkAbkürzungen.put("dak", "DAK-Gesundheit");
        kkAbkürzungen.put("barmer", "BARMER");
        kkAbkürzungen.put("aok", "AOK");
        kkAbkürzungen.put("ikk", "IKK");
        kkAbkürzungen.put("bkk", "BKK");
        kkAbkürzungen.put("hkk", "hkk");

        JLabel KrankenkassenLabel = new JLabel("Krankenkasse:");
        JTextField KrankenkassenSuchfeld = new JTextField(15);

        JComboBox<String> KrankenkassenDropdown =
                new JComboBox<>(krankenkassen.keySet().toArray(new String[0]));


        Map<String, Sozialabgabenrechner.KrankenkassenInfo> finalKrankenkassen = krankenkassen;
        KrankenkassenSuchfeld.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void aktualisieren() {
                String eingabe = KrankenkassenSuchfeld.getText().trim();

                List<String> treffer = FuzzyMatcher.findeAlle(
                        eingabe,
                        finalKrankenkassen,
                        kkAbkürzungen
                );

                KrankenkassenDropdown.removeAllItems();

                if (eingabe.isEmpty()) {
                    for (String kk : finalKrankenkassen.keySet()) {
                        KrankenkassenDropdown.addItem(kk);
                    }
                } else {
                    for (String kk : treffer) {
                        KrankenkassenDropdown.addItem(kk);
                    }
                }
                if (KrankenkassenDropdown.getItemCount() > 0) {
                    KrankenkassenDropdown.setSelectedIndex(0);
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aktualisieren();
            }
        });


        FiscalYearPreviousButton.addActionListener(e -> {
            jahr[0] = Year.now().getValue() -1;
            ladeBundeslaenderDropdown(bundeslandDropdown, jahr[0]);
            ladeKrankenkassenDropdown(KrankenkassenDropdown, jahr[0]);
        });

        FiscalYearCurrentButton.addActionListener(e -> {
            jahr[0] = Year.now().getValue();
            ladeBundeslaenderDropdown(bundeslandDropdown, jahr[0]);
            ladeKrankenkassenDropdown(KrankenkassenDropdown, jahr[0]);
        });

        // Add field for input number of children
        JLabel numberOfChildrenLabel = new JLabel("Anzahl Kinder:");
        JTextField NumberOfChildrenField = new JTextField();


            /*
            STEUERKLASSE DROPDOWN
             */

        String[] steuerKlasse = projektmodus
                ? FOMGehaltsrechner.GUELTIGE_STEUERKLASSEN
                : new String[]{"I", "II", "III", "IV", "V", "VI"};
        JLabel steuerKlasseLabel = new JLabel("Steuerklasse:");
        JComboBox<String> SteuerKlasseDropdown = new JComboBox<>(steuerKlasse);


            /*
            Kirchenmitglied DROPDOWN
             */

        String[] kirchenMitgliedOptions = {"Nein", "Ja"};
        JLabel kirchenMitglied = new JLabel("Kirchenmitglied:");
        JComboBox<String> KirchenMitgliedDropdown = new JComboBox<>(kirchenMitgliedOptions);

        // HIER_7 NEU
        JLabel kvZusatzLabel = new JLabel("KV-Zusatzbeitrag (%):");
        JTextField KvZusatzbeitragField = new JTextField("1,3");


        // Add components to left panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(FiscalYearPreviousButton, gbc);
        gbc.gridy++;
        leftPanel.add(FiscalYearCurrentButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // ---- optische Trennlinie -----
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        // ---- optische Trennlinie ENDE -----


        gbc.gridwidth = 1;
        // Placement of Salaryfield
        gbc.gridy++;
        gbc.gridx = 0;
        leftPanel.add(salaryLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(GrossSalaryField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // ---- optische Trennlinie -----
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        // ---- optische Trennlinie ENDE -----

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        leftPanel.add(steuerKlasseLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(SteuerKlasseDropdown, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        leftPanel.add(kirchenMitglied, gbc);
        gbc.gridx = 1;
        leftPanel.add(KirchenMitgliedDropdown, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        leftPanel.add(bundeslandLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(bundeslandSuchfeld, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        leftPanel.add(bundeslandDropdown, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // ---- optische Trennlinie -----
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        // ---- optische Trennlinie ENDE -----

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        leftPanel.add(KrankenkassenLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(KrankenkassenSuchfeld, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        leftPanel.add(KrankenkassenDropdown, gbc);

        // HIER_8 NEU
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        leftPanel.add(kvZusatzLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(KvZusatzbeitragField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        leftPanel.add(ageLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(AgeInputField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        leftPanel.add(numberOfChildrenLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(NumberOfChildrenField, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        leftPanel.add(submitButton, gbc);


        // ===== RIGHT PANEL (Output) =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Ausgabe"));

        JTextPane outputArea = new JTextPane();
        outputArea.setContentType("text/html");
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== Add panels to frame =====
        frame.add(leftPanel, BorderLayout.LINE_START);
        frame.add(rightPanel, BorderLayout.CENTER);

        // ===== Event Handling =====
        submitButton.addActionListener(e -> {
            double salary;
            int nKids;
            int chosenAge;
            try {
                salary    = Double.parseDouble(GrossSalaryField.getText().trim().replace(",", "."));
                nKids     = Integer.parseInt(NumberOfChildrenField.getText().trim());
                chosenAge = Integer.parseInt(AgeInputField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Bitte gültige Zahlen für Gehalt, Alter und Anzahl Kinder eingeben!",
                        "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object selectedState = bundeslandDropdown.getSelectedItem();
            if (selectedState == null) {
                JOptionPane.showMessageDialog(frame,
                        "Bitte ein gültiges Bundesland auswählen!",
                        "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String chosenState = selectedState.toString().trim();

            String chosenTaxClass = SteuerKlasseDropdown.getSelectedItem().toString().trim();
            String churchMembership = KirchenMitgliedDropdown.getSelectedItem().toString().trim();

            // todo erklären!
            if (nKids < 0 || salary < 0 || chosenAge < 0) {
                JOptionPane.showMessageDialog(frame,
                        "Diese Eingabe darf nicht negativ sein!",
                        "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ergebnis ergebnis;
            int fiscalYear = 0;
            String chosenHealthInsurance;

            if (projektmodus) {
                // ===== PROJEKTMODUS =====
                double kvZusatzbeitrag;
                try {
                    kvZusatzbeitrag = Double.parseDouble(KvZusatzbeitragField.getText().trim().replace(",", "."));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Bitte einen gültigen KV-Zusatzbeitrag eingeben (z.B. 1,3)!",
                            "Fehler", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (kvZusatzbeitrag < 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Der KV-Zusatzbeitrag darf nicht negativ sein!",
                            "Fehler", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ergebnis = FOMGehaltsrechner.berechneGehalt(
                        salary, chosenTaxClass, churchMembership, chosenState,
                        nKids, chosenAge, kvZusatzbeitrag
                );

                chosenHealthInsurance = "frei eingegebenem Satz";

            } else {
                // ===== Professional Mode =====


                Object selectedKK = KrankenkassenDropdown.getSelectedItem();
                if (selectedKK == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Bitte eine gültige Krankenkasse auswählen!",
                            "Fehler", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                chosenHealthInsurance = selectedKK.toString().trim();
                fiscalYear = FiscalYearPreviousButton.isSelected() ? Year.now().getValue() - 1 :
                        FiscalYearCurrentButton.isSelected() ? Year.now().getValue() : 0;

                List<String> fehlendePfade = ProgramSettings.getFehlendePfade();
                if (!fehlendePfade.isEmpty()) {
                    String nachricht = "Folgende CSV-Dateipfade fehlen noch:\n\n• "
                            + String.join("\n• ", fehlendePfade)
                            + "\n\nBitte in den Einstellungen festlegen.";
                    JOptionPane.showMessageDialog(frame, nachricht,
                            "Fehlende Quelldaten!", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<String> dateiFehler = ProgramSettings.pruefeAlleCsvDateien();
                if (!dateiFehler.isEmpty()) {
                    String nachricht = "Folgende Dateien sind fehlerhaft oder falsch zugeordnet:\n\n• "
                            + String.join("\n• ", dateiFehler);
                    JOptionPane.showMessageDialog(frame, nachricht,
                            "Ungültige CSV-Dateien", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (fiscalYear == 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Bitte ein Fiskaljahr auswählen!",
                            "Fehler", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ergebnis = Abgabenrechner.berechneGehalt(
                        salary,
                        chosenTaxClass,
                        churchMembership,
                        chosenState,
                        nKids,
                        fiscalYear,
                        chosenHealthInsurance,
                        chosenAge,
                        // CSV-Pfade aus Programm-Setting
                        getSteuerSaetzePfad(),
                        getEinkommenssteuerGrenzenPfad(),
                        getBundeslandUndKirchensteuerPfad(),
                        getSteuerpauschalenPfad(),
                        getKrankenkassenPfad(),
                        getSozialversicherungssaetzePfad()
                );
            }

            if (ergebnis == null) {
                JOptionPane.showMessageDialog(frame,
                        "Berechnung fehlgeschlagen. Bitte Eingaben prüfen.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kopfzeile = projektmodus
                    ? "<tr><th colspan='3' align='left'>Projektmodus (vereinfachte Berechnung)</th></tr>"
                    : "<tr><th colspan='3' align='left'>Ausgewähltes Fiskaljahr: " + fiscalYear + "</th></tr>";

            outputArea.setText(
                    "<html><body style='font-family: monospace; font-size: 12px; padding: 10px'>" +
                            "<table width='100%'>" +
                            "<colgroup>" +
                            "<col style='width:55%'>" +
                            "<col style='width:22%'>" +
                            "<col style='width:23%'>" +
                            "</colgroup>" +
                            kopfzeile +
                            "<tr><td colspan='3' style='font-style: italic; font-size: 10px; color: slategrey;'>Angaben in EUR</td></tr>" +
                            "<tr><th align='left'>Position</th><th align='right'>Monat</th><th align='right'>Jahr</th></tr>" +
                            "<tr><td colspan='3'><hr/></td></tr>" +

                            // Brutto
                            "<tr><td><b>Bruttogehalt</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getBrutto()) + "</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getBrutto() * 12) + "</b></td></tr>" +

                            "<tr><td colspan='3'><br/><b>── Steuern ──────────────</b></td></tr>" +

                            "<tr><td>Lohnsteuer</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getLohnsteuerMonat()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getLohnsteuerJahr()) + "</td></tr>" +

                            "<tr><td>Kirchensteuer</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKirchensteuerMonat()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKirchensteuerJahr()) + "</td></tr>" +

                            "<tr><td>Soli</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getSoliMonat()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getSoliJahr()) + "</td></tr>" +

                            "<tr><td colspan='3'><br/><b>── Sozialabgaben (AN-Anteil)  ────────</b></td></tr>" +

                            "<tr><td>Krankenversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKvBeitrag()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKvBeitrag() * 12) + "</td></tr>" +

                            "<tr><td><i>&nbsp;&nbsp; ..davon Basisbeitrag (7,30%)</i></td>" +
                            "<td align='right' style='color: slategrey;'><i>" + formatEUR(ergebnis.getKvBasisBetrag()) + "</i></td>" +
                            "<td align='right' style='color: slategrey;'><i>" + formatEUR(ergebnis.getKvBasisBetrag() * 12) + "</i></td></tr>" +

                            "<tr><td style='color: slategrey;'><i>&nbsp;&nbsp; ..davon Zusatzbeitrag durch: " + chosenHealthInsurance +
                            " (" + String.format("%.2f", ergebnis.getKvZusatz()) + "%)</i></td>" +
                            "<td align='right' style='color: slategrey;'><i>" + formatEUR(ergebnis.getKvZusatzBetrag()) + "</i></td>" +
                            "<td align='right' style='color: slategrey;'><i>" + formatEUR(ergebnis.getKvZusatzBetrag() * 12) + "</i></td></tr>" +

                            "<tr><td>Rentenversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getRvBeitrag()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getRvBeitrag() * 12) + "</td></tr>" +

                            "<tr><td>Arbeitslosenvers.</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getAvBeitrag()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getAvBeitrag() * 12) + "</td></tr>" +

                            "<tr><td>Pflegeversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getPvBeitrag()) + "</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getPvBeitrag() * 12) + "</td></tr>" +

                            "<tr><td colspan='3'><hr/></td></tr>" +

                            // Netto
                            "<tr><td><b>Nettogehalt</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getNettoMonat()) + "</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getNettoMonat() * 12) + "</b></td></tr>" +

                            "</table></body></html>"
            );


        });


        if (projektmodus) {
            FiscalYearPreviousButton.setVisible(false);
            FiscalYearCurrentButton.setVisible(false);
            KrankenkassenLabel.setVisible(false);
            KrankenkassenSuchfeld.setVisible(false);
            KrankenkassenDropdown.setVisible(false);
        } else {
            kvZusatzLabel.setVisible(false);
            KvZusatzbeitragField.setVisible(false);
        }

        frame.setVisible(true);
    }
}






