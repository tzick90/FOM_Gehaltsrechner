package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.Color;

public class GUI {

    static JMenuItem item1, item2, item3;
    static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Gehaltsrechner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);
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

            // Register ActionListener to Buttons
            FiscalYearPreviousButton.addActionListener(new ActionHandler());
            FiscalYearCurrentButton.addActionListener(new ActionHandler());

            FiscalYearPreviousButton.setBounds(20, 20, 180, 30);
            FiscalYearCurrentButton.setBounds(20, 50, 180, 30);

            JLabel label = new JLabel("✅ Swing funktioniert!", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));

            frame.add(label);
            frame.add(FiscalYearPreviousButton);
            frame.add(FiscalYearCurrentButton);


            /*
            ----------------------------------------------------------------------------------
            Adding Add Toggle-Button to activate comparisson-mode
            ---------------------------------------------------------------------------------
             */

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
