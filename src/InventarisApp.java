import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import controller.InventarisController;
import view.InventarisFrame;

public class InventarisApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
        } catch (Exception ex) {
            System.err.println("Gagal mengatur tema FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            InventarisFrame frame = new InventarisFrame();
            new InventarisController(frame);
            frame.setVisible(true);
        });
    }
}
