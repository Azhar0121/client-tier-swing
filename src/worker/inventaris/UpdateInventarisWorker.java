package worker.inventaris;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.InventarisApiClient;
import model.Inventaris;
import view.InventarisFrame;

public class UpdateInventarisWorker extends SwingWorker<Void, Void> {

    private final InventarisFrame frame;
    private final InventarisApiClient inventarisApiClient;
    private final Inventaris inventaris;

    public UpdateInventarisWorker(
            InventarisFrame frame,
            InventarisApiClient inventarisApiClient,
            Inventaris inventaris
    ) {
        this.frame = frame;
        this.inventarisApiClient = inventarisApiClient;
        this.inventaris = inventaris;

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Memperbarui data inventaris...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        inventarisApiClient.update(inventaris);
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);

        try {
            get();
            frame.getProgressBar().setString("Inventaris berhasil diperbarui");

            JOptionPane.showMessageDialog(
                    frame,
                    "Data inventaris berhasil diperbarui.",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            frame.getProgressBar().setString("Gagal memperbarui inventaris");

            JOptionPane.showMessageDialog(
                    frame,
                    "Terjadi kesalahan saat memperbarui data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
