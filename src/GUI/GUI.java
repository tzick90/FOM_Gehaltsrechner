package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Year;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

import core.Sozialabgabenrechner;
import core.CsvReader;
import core.CsvReader.BundeslandInfo;
import core.FuzzyMatcher;
import util.Abgabenrechner;
import util.Ergebnis;

import static GUI.ProgramSettings.*;

public class GUI extends Component {

    static String formatEUR(double wert) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(wert);
    }

    static JMenuItem item1, item2, item3;
    static JFrame frame;

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Gehaltsrechner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout(10, 10));
            frame.requestFocus();

            JMenuBar BarMenu = new JMenuBar();
            JMenu Menu = new JMenu("Datei");

            item1 = new JMenuItem("Reset Eingabe");
            item1.addActionListener(new ActionHandler());

            item2 = new JMenuItem("Einstellungen");
            item2.addActionListener(new ActionHandler());

            item3 = new JMenuItem("Beenden");
            item3.addActionListener(new ActionHandler());



            Menu.add(item1);
            Menu.add(item2);
            Menu.add(item3);
            BarMenu.add(Menu);
            frame.setJMenuBar(BarMenu);

            /*
            LEFT PANEL for INPUT
             */

            JPanel leftPanel = new JPanel(new GridBagLayout());
            leftPanel.setBorder(BorderFactory.createTitledBorder("Eingabe"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
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

            JLabel salaryLabel = new JLabel("Gehalt (Brutto)");
            JTextField GrossSalaryField = new JTextField();

            JButton submitButton = new JButton("Berechnen");


            // Field for Age input
            JLabel ageLabel = new JLabel("Alter:");
            JTextField AgeInputField = new JTextField();

            int jahr = 2026; // TODO: Change with

            // Add FuzzyMatch for German-States
            Map<String, BundeslandInfo> bundeslaender;
            try {
                bundeslaender = CsvReader.leseBundeslaenderMitJahr(
                        "config/Bundesland_und_Kirchensteuer.csv", jahr); // TODO: change with path from settings
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



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

            JLabel bundeslandLabel = new JLabel("Bundesland:");
            JTextField bundeslandSuchfeld = new JTextField(15);

            JComboBox<String> bundeslandDropdown =
                    new JComboBox<>(bundeslaender.keySet().toArray(new String[0]));

            bundeslandSuchfeld.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void aktualisieren() {
                    String eingabe = bundeslandSuchfeld.getText().trim();

                    List<String> treffer = FuzzyMatcher.findeAlle(
                            eingabe,
                            bundeslaender,
                            blAbkuerzungen
                    );

                    bundeslandDropdown.removeAllItems();

                    if (eingabe.isEmpty()) {
                        for (String bl : bundeslaender.keySet()) {
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
            try {
                krankenkassen = CsvReader.leseKrankenkassen("config/krankenkassen.csv");
            } catch (IOException e) {
                throw new RuntimeException(e);
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


            KrankenkassenSuchfeld.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void aktualisieren() {
                    String eingabe = KrankenkassenSuchfeld.getText().trim();

                    List<String> treffer = FuzzyMatcher.findeAlle(
                            eingabe,
                            krankenkassen,
                            kkAbkürzungen
                    );

                    KrankenkassenDropdown.removeAllItems();

                    if (eingabe.isEmpty()) {
                        for (String kk : krankenkassen.keySet()) {
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

            // Add field for input number of children
            JLabel numberOfChildrenLabel = new JLabel("Anzahl Kinder:");
            JTextField NumberOfChildrenField = new JTextField();


            /*
            STEUERKLASSE DROPDOWN
             */

            String[] steuerKlasse = {"I", "II", "III", "IV","V","VI"};
            JLabel steuerKlasseLabel = new JLabel("Steuerklasse:");
            JComboBox<String> SteuerKlasseDropdown = new JComboBox<>(steuerKlasse);


            /*
            Kirchenmitglied DROPDOWN
             */

            String[] kirchenMitgliedOptions = {"Nein", "Ja"};
            JLabel kirchenMitglied = new JLabel("Kirchenmitglied:");
            JComboBox<String> KirchenMitgliedDropdown = new JComboBox<>(kirchenMitgliedOptions);



            // Add components to left panel
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            leftPanel.add(FiscalYearPreviousButton, gbc);
            gbc.gridy++;
            leftPanel.add(FiscalYearCurrentButton, gbc);

            gbc.gridwidth = 1; gbc.gridy++;

            // ---- optische Trennlinie -----
            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
            // ---- optische Trennlinie ENDE -----



            // Placement of Salaryfield
            gbc.gridy++; gbc.gridx = 0;
            leftPanel.add(salaryLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(GrossSalaryField, gbc);

            gbc.gridwidth = 1; gbc.gridy++;

            // ---- optische Trennlinie -----
            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
            // ---- optische Trennlinie ENDE -----


            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
            leftPanel.add(steuerKlasseLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(SteuerKlasseDropdown, gbc);

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
            leftPanel.add(kirchenMitglied, gbc);
            gbc.gridx = 1;
            leftPanel.add(KirchenMitgliedDropdown, gbc);

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
            leftPanel.add(bundeslandLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(bundeslandSuchfeld, gbc);

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            leftPanel.add(bundeslandDropdown, gbc);

            gbc.gridwidth = 1; gbc.gridy++;

            // ---- optische Trennlinie -----
            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
            // ---- optische Trennlinie ENDE -----


            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
            leftPanel.add(KrankenkassenLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(KrankenkassenSuchfeld, gbc);

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            leftPanel.add(KrankenkassenDropdown, gbc);

            gbc.gridy++; gbc.gridx = 0;
            leftPanel.add(ageLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(AgeInputField, gbc);

            gbc.gridy++; gbc.gridx = 0;
            leftPanel.add(numberOfChildrenLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(NumberOfChildrenField, gbc);



            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
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
                double salary = Double.parseDouble(GrossSalaryField.getText().trim());
                int nKids = Integer.parseInt(NumberOfChildrenField.getText().trim());
                int chosenAge = Integer.parseInt(AgeInputField.getText().trim());
                String chosenState = bundeslandDropdown.getSelectedItem().toString().trim();
                String chosenTaxClass = SteuerKlasseDropdown.getSelectedItem().toString().trim();
                String churchMembership = KirchenMitgliedDropdown.getSelectedItem().toString().trim();
                String chosenHealthInsurance = KrankenkassenDropdown.getSelectedItem().toString().trim();
                int fiscalYear = FiscalYearPreviousButton.isSelected() ? Year.now().getValue()-1 :
                        FiscalYearCurrentButton.isSelected() ? Year.now().getValue() : 0;

                if (fiscalYear == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Bitte ein Fiskaljahr auswählen!",
                            "Fehler",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (nKids < 0 || salary < 0 || chosenAge <0) {
                    JOptionPane.showMessageDialog(this,
                            "Diese Eingabe darf nicht negativ sein!",
                            "Fehler",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }


                Ergebnis ergebnis = Abgabenrechner.berechneGehalt(
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

                outputArea.setText(
                        "<html><body style='font-family: monospace; font-size: 12px; padding: 10px'>" +
                            "<table width='100%'>" +
                            "<tr><th align='left'>Ausgewähltes Fiskaljahr: "+fiscalYear + "</th></tr>"+
                            "<tr><th align='left'>Position</th><th align='right'>Monat</th><th align='right'>Jahr</th></tr>" +
                            "<tr><td colspan='3'><hr/></td></tr>" +
                            // Brutto
                            "<tr><td><b>Bruttogehalt</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getBrutto()) + "</b></td>" + "<td><b>€</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getBrutto() * 12) + "</b></td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td colspan='3'><br/><b>── Steuern ──────────────</b></td></tr>" +

                            "<tr><td>Lohnsteuer</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getLohnsteuerMonat()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getLohnsteuerJahr()) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td>Kirchensteuer</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKirchensteuerMonat()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKirchensteuerJahr()) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td>Soli</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getSoliMonat()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getSoliJahr()) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td colspan='3'><br/><b>── Sozialabgaben (AN-Anteil)  ────────</b></td></tr>" +

                            "<tr><td>Krankenversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKvBeitrag()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getKvBeitrag() * 12) + "</td></tr>" + "<td><b>€</b></td>" +

                                "<tr><td><i>&nbsp;&nbsp; ..davon Basisbeitrag (7,30%)</i></td>" +
                                "<td align='right'><i>" + formatEUR(ergebnis.getKvBasisBetrag()) + "</i></td>" + "<td><b>€</b></td>" +
                                "<td align='right'><i>" + formatEUR(ergebnis.getKvBasisBetrag() * 12) + "</i></td></tr>" + "<td><b>€</b></td>" +

                                "<tr><td><i>&nbsp;&nbsp; ..davon Zusatzbeitrag durch: " + chosenHealthInsurance +
                                " (" + String.format("%.2f €",ergebnis.getKvZusatz()) + "%)</i></td>" +
                                "<td align='right'><i>" + formatEUR(ergebnis.getKvZusatzBetrag()) + "</i></td>" + "<td><b>€</b></td>" +
                                "<td align='right'><i>" + formatEUR(ergebnis.getKvZusatzBetrag() * 12) + "</i></td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td>Rentenversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getRvBeitrag()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getRvBeitrag() * 12) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td>Arbeitslosenvers.</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getAvBeitrag()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getAvBeitrag() * 12) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td>Pflegeversicherung</td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getPvBeitrag()) + "</td>" + "<td><b>€</b></td>" +
                            "<td align='right'>" + formatEUR(ergebnis.getPvBeitrag() * 12) + "</td></tr>" + "<td><b>€</b></td>" +

                            "<tr><td colspan='3'><hr/></td></tr>" +

                            // Netto
                            "<tr><td><b>Nettogehalt</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getNettoMonat()) + "</b></td>" + "<td><b>€</b></td>" +
                            "<td align='right'><b>" + formatEUR(ergebnis.getNettoMonat() * 12) + "</b></td></tr>" + "<td><b>€</b></td>" +

                            "</table></body></html>"
                );



            });






            frame.setVisible(true);
        });
    }
}






