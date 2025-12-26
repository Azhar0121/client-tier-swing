import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import controller.InventarisController;
import view.InventarisFrame;

public class InventarisApp {
    public static void main(String[] args) {
        try {
            // Set Look-and-Feel
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());

            // Tweak global UI defaults for a more modern appearance
            UIManager.put("Component.arc", 14);
            UIManager.put("Button.arc", 14);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ProgressBar.height", 18);
            UIManager.put("Table.rowHeight", 30);
            UIManager.put("Table.alternateRowBackground", new Color(250, 250, 252));
            UIManager.put("ScrollBar.showButtons", Boolean.FALSE);
        } catch (Exception ex) {
            System.err.println("Gagal mengatur tema FlatLaf: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            InventarisFrame frame = new InventarisFrame();
            new InventarisController(frame);
            frame.setVisible(true);
        });
    }
}
