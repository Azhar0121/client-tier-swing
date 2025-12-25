package worker.inventaris;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.InventarisApiClient;
import model.Inventaris;
import view.InventarisFrame;

public class LoadInventarisWorker extends SwingWorker<List<Inventaris>, Void> {

    private final InventarisFrame frame;
    private final InventarisApiClient inventarisApiClient;

    public LoadInventarisWorker(
            InventarisFrame frame,
            InventarisApiClient inventarisApiClient
    ) {
        this.frame = frame;
        this.inventarisApiClient = inventarisApiClient;

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Memuat data inventaris...");
    }

    @Override
    protected List<Inventaris> doInBackground() throws Exception {
        return inventarisApiClient.findAll();
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);

        try {
            List<Inventaris> result = get();

            frame.getInventarisTableModel().setInventarisList(result);
            frame.getTotalRecordsLabel().setText(result.size() + " Data");

            frame.getProgressBar().setString(result.size() + " data berhasil dimuat");
        } catch (Exception e) {
            frame.getProgressBar().setString("Gagal memuat data");

            JOptionPane.showMessageDialog(
                    frame,
                    "Terjadi kesalahan saat memuat data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
