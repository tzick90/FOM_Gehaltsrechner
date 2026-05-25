package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import core.Abgabenrechner;
import core.CsvReader;
import core.CsvReader.BundeslandInfo;
import util.FuzzyMatcher;

import static core.CsvReader.leseBundeslaenderMitJahr;

public class GUI {

    static JMenuItem item1, item2, item3;
    static JFrame frame;

    public static void main(String[] args) {
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


            // adding Radio-Button to choose between monthly or anually salary calculation
            JRadioButton MonthlyCalcButton = new JRadioButton("Monat");
            MonthlyCalcButton.setMnemonic(KeyEvent.VK_M);
            MonthlyCalcButton.setActionCommand("Monat");

            JRadioButton YearlyCalcButton = new JRadioButton("Jahr");
            YearlyCalcButton.setMnemonic(KeyEvent.VK_J);
            YearlyCalcButton.setActionCommand("Jahr");


            // Grouping the RadioButtons
            ButtonGroup FiscalYearButtonsGroup = new ButtonGroup();
            FiscalYearButtonsGroup.add(FiscalYearPreviousButton);
            FiscalYearButtonsGroup.add(FiscalYearCurrentButton);

            ButtonGroup SalaryValueButton = new ButtonGroup();
            SalaryValueButton.add(MonthlyCalcButton);
            SalaryValueButton.add(YearlyCalcButton);



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
            Map<String, Abgabenrechner.KrankenkassenInfo> krankenkassen;
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

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            leftPanel.add(MonthlyCalcButton, gbc);
            gbc.gridy++;
            leftPanel.add(YearlyCalcButton, gbc);

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

            JTextArea outputArea = new JTextArea();
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);
            rightPanel.add(scrollPane, BorderLayout.CENTER);

            // ===== Add panels to frame =====
            frame.add(leftPanel, BorderLayout.LINE_START);
            frame.add(rightPanel, BorderLayout.CENTER);

            // ===== Event Handling =====
            submitButton.addActionListener(e -> {
                String salary = GrossSalaryField.getText().trim();
                String nKids = NumberOfChildrenField.getText().trim();
                String chosenAge = AgeInputField.getText().trim();
                String chosenState = bundeslandDropdown.getSelectedItem().toString().trim();
                String chosenTaxClass = SteuerKlasseDropdown.getSelectedItem().toString().trim();
                String churchMembership = KirchenMitgliedDropdown.getSelectedItem().toString().trim();
                String chosenHealthInsurance = KrankenkassenDropdown.getSelectedItem().toString().trim();
                String fiscalYear = FiscalYearPreviousButton.isSelected() ? "Letztes Fiskaljahr" :
                        FiscalYearCurrentButton.isSelected() ? "Aktuelles Fiskaljahr" : "Nicht ausgewählt";
                String grossSalaryMode = MonthlyCalcButton.isSelected() ? "monatlich" :
                        YearlyCalcButton.isSelected() ? "jährlich" : "Nicht ausgewählt";

                if (salary.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Bitte alle Felder ausfüllen.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                outputArea.append("Gehalt: " + salary + "\n"
                        + "Jahr: " + fiscalYear + "\n"
                        + "Anzahl Kinder: " + nKids + "\n"
                        + "Alter: " + chosenAge + "\n"
                        + "Bundesland: " + chosenState + "\n"
                        + "Steuerklasse: " + chosenTaxClass + "\n"
                        + "Ist Kirchenmitglied: " + churchMembership + "\n"
                        + "Gewählte Krankenkasse: " + chosenHealthInsurance + "\n"
                        + "Bruttolohn-Angabe: " + grossSalaryMode + "\n");
                GrossSalaryField.setText("");
            });

            frame.setVisible(true);
        });
    }
}





            // Register ActionListener to Buttons
            //FiscalYearPreviousButton.addActionListener(new ActionHandler());
            //FiscalYearCurrentButton.addActionListener(new ActionHandler());

            //FiscalYearPreviousButton.setBounds(20, 20, 180, 30);
            //FiscalYearCurrentButton.setBounds(20, 50, 180, 30);

            //JLabel label = new JLabel("✅ Swing funktioniert!", SwingConstants.CENTER);
            //label.setFont(new Font("Arial", Font.BOLD, 24));

            //frame.add(label);
           // frame.add(FiscalYearPreviousButton);
            //frame.add(FiscalYearCurrentButton);


            /*
            ----------------------------------------------------------------------------------
            Adding Add Toggle-Button to activate comparisson-mode
            ---------------------------------------------------------------------------------
             */
    /*
            JToggleButton ComparissonModeToggleButton = new JRadioButton("Jahresvergleich",false);

            // Make background white and keep it visible
            ComparissonModeToggleButton.setBackground(Color.WHITE);
            ComparissonModeToggleButton.setOpaque(true);
            ComparissonModeToggleButton.setContentAreaFilled(true);
            ComparissonModeToggleButton.setFocusPainted(false);
            ComparissonModeToggleButton.setBorderPainted(true);

            // Change background manually when toggled
            ComparissonModeToggleButton.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ComparissonModeToggleButton.setBackground(Color.lightGray); // selected color
                } else {
                    ComparissonModeToggleButton.setBackground(Color.WHITE); // unselected color
                }
            });

            ComparissonModeToggleButton.setBounds(200, 20, 180, 50);

            frame.add(ComparissonModeToggleButton);




            JTextField BruttoLohnFeld = new JTextField();
            BruttoLohnFeld.setBounds(200, 200, 100, 25);
            frame.add(BruttoLohnFeld);



            frame.setVisible(true);
        });
    }
}
*/

