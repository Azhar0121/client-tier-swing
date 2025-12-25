package worker.inventaris;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.InventarisApiClient;
import model.Inventaris;
import view.InventarisFrame;

public class SaveInventarisWorker extends SwingWorker<Void, Void> {

    private final InventarisFrame frame;
    private final InventarisApiClient inventarisApiClient;
    private final Inventaris inventaris;

    public SaveInventarisWorker(
            InventarisFrame frame,
            InventarisApiClient inventarisApiClient,
            Inventaris inventaris
    ) {
        this.frame = frame;
        this.inventarisApiClient = inventarisApiClient;
        this.inventaris = inventaris;

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Menyimpan data inventaris...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        inventarisApiClient.create(inventaris);
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);

        try {
            get(); // menangkap exception jika ada
            frame.getProgressBar().setString("Inventaris berhasil disimpan");

            JOptionPane.showMessageDialog(
                    frame,
                    "Data inventaris berhasil ditambahkan.",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            frame.getProgressBar().setString("Gagal menyimpan inventaris");

            JOptionPane.showMessageDialog(
                    frame,
                    "Terjadi kesalahan saat menyimpan data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
