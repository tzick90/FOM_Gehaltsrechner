package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.List;

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

            int jahr = 2026;

            // Add FuzzyMatch for German-States
            Map<String, BundeslandInfo> bundeslaender;
            try {
                bundeslaender = CsvReader.leseBundeslaenderMitJahr(
                        "config/Bundesland_und_Kirchensteuer.csv", jahr);
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




            // Add components to left panel
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            leftPanel.add(FiscalYearPreviousButton, gbc);
            gbc.gridy++;
            leftPanel.add(FiscalYearCurrentButton, gbc);

            gbc.gridwidth = 1; gbc.gridy++;

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
            leftPanel.add(bundeslandLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(bundeslandSuchfeld, gbc);

            gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
            leftPanel.add(bundeslandDropdown, gbc);

            gbc.gridy++; gbc.gridx = 0;
            leftPanel.add(salaryLabel, gbc);
            gbc.gridx = 1;
            leftPanel.add(GrossSalaryField, gbc);

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
                String fiscalYear = FiscalYearPreviousButton.isSelected() ? "Letztes Fiskaljahr" :
                        FiscalYearCurrentButton.isSelected() ? "Aktuelles Fiskaljahr" : "Nicht ausgewählt";

                if (salary.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Bitte alle Felder ausfüllen.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                outputArea.append("Gehalt: " + salary + ", Jahr: " + fiscalYear + "\n");
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

