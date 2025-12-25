package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import model.Inventaris;
import net.miginfocom.swing.MigLayout;

public class InventarisDialog extends JDialog {

    private final JTextField namaBarangField = new JTextField(25);
    private final JTextField kategoriField = new JTextField(25);
    private final JTextField kondisiField = new JTextField(25);
    private final JTextField jumlahField = new JTextField(10);

    private final JButton saveButton = new JButton("Simpan");
    private final JButton cancelButton = new JButton("Batal");

    private Inventaris inventaris;

    public InventarisDialog(JFrame owner) {
        super(owner, "Tambah Barang Inventaris", true);
        this.inventaris = new Inventaris();
        setupComponents();
    }

    public InventarisDialog(JFrame owner, Inventaris inventarisToEdit) {
        super(owner, "Edit Barang Inventaris", true);
        this.inventaris = inventarisToEdit;
        setupComponents();

        namaBarangField.setText(inventarisToEdit.getNamaBarang());
        kategoriField.setText(inventarisToEdit.getKategori());
        kondisiField.setText(inventarisToEdit.getKondisi());
        jumlahField.setText(String.valueOf(inventarisToEdit.getJumlah()));
    }

    private void setupComponents() {
        setLayout(new MigLayout("fill, insets 30", "[right]20[grow]"));

        add(new JLabel("Nama Barang"), "");
        add(namaBarangField, "growx, wrap");

        add(new JLabel("Kategori"), "");
        add(kategoriField, "growx, wrap");

        add(new JLabel("Kondisi"), "");
        add(kondisiField, "growx, wrap");

        add(new JLabel("Jumlah"), "");
        add(jumlahField, "growx, wrap");

        saveButton.setBackground(UIManager.getColor("Button.default.background"));
        saveButton.setForeground(UIManager.getColor("Button.default.foreground"));
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));

        JPanel buttonPanel = new JPanel(new MigLayout("", "[]10[]"));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, "span, right");

        pack();
        setMinimumSize(new Dimension(500, 420));
        setLocationRelativeTo(getOwner());
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public Inventaris getInventaris() {
        inventaris.setNamaBarang(namaBarangField.getText().trim());
        inventaris.setKategori(kategoriField.getText().trim());
        inventaris.setKondisi(kondisiField.getText().trim());

        try {
            inventaris.setJumlah(Integer.parseInt(jumlahField.getText().trim()));
        } catch (NumberFormatException e) {
            inventaris.setJumlah(0);
        }

        return inventaris;
    }
}
