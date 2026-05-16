import javax.swing.*;
import java.awt.*;

public class GUI {
    public static void main( String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gehaltsrechner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("✅ Swing funktioniert!", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));

            frame.add(label);
            frame.setVisible(true);
        });
    }
}
