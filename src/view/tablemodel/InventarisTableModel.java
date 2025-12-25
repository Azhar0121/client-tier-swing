package view.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Inventaris;

public class InventarisTableModel extends AbstractTableModel {

    private List<Inventaris> inventarisList = new ArrayList<>();

    private final String[] columnNames = {
        "ID",
        "Nama Barang",
        "Kategori",
        "Kondisi",
        "Jumlah"
    };

    public void setInventarisList(List<Inventaris> inventarisList) {
        this.inventarisList = inventarisList;
        fireTableDataChanged();
    }

    public Inventaris getInventarisAt(int rowIndex) {
        return inventarisList.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return inventarisList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Inventaris inventaris = inventarisList.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> inventaris.getId();
            case 1 -> inventaris.getNamaBarang();
            case 2 -> inventaris.getKategori();
            case 3 -> inventaris.getKondisi();
            case 4 -> inventaris.getJumlah();
            default -> null;
        };
    }
}
