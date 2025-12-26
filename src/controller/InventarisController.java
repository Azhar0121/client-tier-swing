package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import api.InventarisApiClient;
import api.WebSocketClientHandler;
import model.Inventaris;
import view.InventarisDialog;
import view.InventarisFrame;
import worker.inventaris.DeleteInventarisWorker;
import worker.inventaris.LoadInventarisWorker;
import worker.inventaris.SaveInventarisWorker;
import worker.inventaris.UpdateInventarisWorker;

public class InventarisController {

    private final InventarisFrame frame;
    private final InventarisApiClient inventarisApiClient = new InventarisApiClient();

    private List<Inventaris> allInventaris = new ArrayList<>();
    private List<Inventaris> displayedInventaris = new ArrayList<>();

    private WebSocketClientHandler wsClient;

    public InventarisController(InventarisFrame frame) {
        this.frame = frame;
        setupEventListeners();
        setupWebSocket();
        loadAllInventaris();
    }

    private void setupWebSocket() {
        try {
            URI uri = new URI("ws://localhost:3000");
            wsClient = new WebSocketClientHandler(uri, new Consumer<String>() {
                @Override
                public void accept(String message) {
                    System.out.println("Realtime update received: " + message);
                    handleWebSocketMessage(message);
                }
            });
            wsClient.connect();
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Gagal terhubung ke server realtime:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWebSocketMessage(String message) {
        // Setiap notifikasi realtime akan memicu reload data
        SwingUtilities.invokeLater(this::loadAllInventaris);
    }

    private void setupEventListeners() {

        frame.getAddButton().addActionListener(e -> openInventarisDialog(null));
        frame.getRefreshButton().addActionListener(e -> loadAllInventaris());
        frame.getDeleteButton().addActionListener(e -> deleteSelectedInventaris());

        frame.getInventarisTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = frame.getInventarisTable().getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = frame.getInventarisTable().convertRowIndexToModel(selectedRow);
                        openInventarisDialog(frame.getInventarisTableModel().getInventarisAt(modelRow));
                    }
                }
            }
        });

        frame.getSearchField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            private void applySearchFilter() {
                String keyword = frame.getSearchField().getText();
                if (keyword == null)
                    keyword = "";
                keyword = keyword.toLowerCase().trim();

                displayedInventaris = new ArrayList<>();

                for (Inventaris inv : allInventaris) {
                    String nama = inv.getNamaBarang() != null ? inv.getNamaBarang().toLowerCase() : "";
                    String kategori = inv.getKategori() != null ? inv.getKategori().toLowerCase() : "";
                    String kondisi = inv.getKondisi() != null ? inv.getKondisi().toLowerCase() : "";

                    if (nama.contains(keyword) || kategori.contains(keyword) || kondisi.contains(keyword)) {
                        displayedInventaris.add(inv);
                    }
                }

                frame.getInventarisTableModel().setInventarisList(displayedInventaris);
                updateTotalRecordsLabel();
            }
        });
    }

    private void openInventarisDialog(Inventaris inventarisToEdit) {

        InventarisDialog dialog = (inventarisToEdit == null)
                ? new InventarisDialog(frame)
                : new InventarisDialog(frame, inventarisToEdit);

        dialog.getSaveButton().addActionListener(e -> {
            Inventaris inventaris = dialog.getInventaris();
            SwingWorker<Void, Void> worker;

            if (inventarisToEdit == null) {
                worker = new SaveInventarisWorker(frame, inventarisApiClient, inventaris);
            } else {
                worker = new UpdateInventarisWorker(frame, inventarisApiClient, inventaris);
            }

            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    dialog.dispose();
                    loadAllInventaris();
                }
            });

            worker.execute();
        });

        dialog.setVisible(true);
    }

    private void deleteSelectedInventaris() {
        int selectedRow = frame.getInventarisTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(frame, "Pilih data yang ingin dihapus.");
            return;
        }

        // convert view index to model index to handle sorting/filtering
        int modelRow = frame.getInventarisTable().convertRowIndexToModel(selectedRow);
        Inventaris inv = frame.getInventarisTableModel().getInventarisAt(modelRow);

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Hapus barang:\n" + inv.getNamaBarang() + " (" + inv.getKategori() + ")?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DeleteInventarisWorker worker = new DeleteInventarisWorker(frame, inventarisApiClient, inv);

            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    loadAllInventaris();
                }
            });

            worker.execute();
        }
    }

    private void loadAllInventaris() {

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Memuat data...");

        LoadInventarisWorker worker = new LoadInventarisWorker(frame, inventarisApiClient);

        worker.addPropertyChangeListener(evt -> {
            if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                try {
                    allInventaris = worker.get();
                    displayedInventaris = new ArrayList<>(allInventaris);

                    frame.getInventarisTableModel()
                            .setInventarisList(displayedInventaris);

                    updateTotalRecordsLabel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Gagal memuat data.");
                } finally {
                    frame.getProgressBar().setIndeterminate(false);
                    frame.getProgressBar().setString("Ready");
                }
            }
        });

        worker.execute();
    }

    private void updateTotalRecordsLabel() {
        frame.getTotalRecordsLabel()
                .setText(displayedInventaris.size() + " Data");
    }
}
