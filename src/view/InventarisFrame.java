package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import view.tablemodel.InventarisTableModel;
import javax.swing.RowFilter;
import java.util.regex.Pattern;

public class InventarisFrame extends JFrame {

    private final JTextField searchField = new JTextField(30);
    private final JButton addButton = new JButton("Tambah Barang");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton deleteButton = new JButton("Hapus");
    private final JLabel totalRecordsLabel = new JLabel("0 Data");

    private final JTable inventarisTable = new JTable();
    private final InventarisTableModel inventarisTableModel = new InventarisTableModel();
    private final JProgressBar progressBar = new JProgressBar();
    private TableRowSorter<InventarisTableModel> tableSorter;

    public InventarisFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sistem Inventaris Barang Laboratorium");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout(
                "fill, insets 20",
                "[grow]",
                "[]10[]10[grow]10[]10[]"));

        inventarisTable.setModel(inventarisTableModel);
        progressBar.setStringPainted(true);
        configureTable();

        // Header
        JLabel title = new JLabel("Daftar Inventaris Barang Laboratorium");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));
        add(title, "wrap, span 2");

        add(createSearchPanel(), "growx, w 70%");
        add(createButtonPanel(), "wrap, right, w 30%");
        add(new JScrollPane(inventarisTable), "grow, wrap, span 2");
        add(progressBar, "growx, h 20!, wrap, span 2");
        add(totalRecordsLabel, "right, span 2");

        pack();
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gap 8"));
        JLabel searchLabel = new JLabel("🔍 Cari Barang:");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.PLAIN, 14f));
        panel.add(searchLabel);

        searchField.setToolTipText("Ketik kata kunci untuk mencari (Nama, Kategori, Kondisi)...");
        searchField.setFont(searchField.getFont().deriveFont(14f));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        panel.add(searchField, "growx");

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void applyFilter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    tableSorter.setRowFilter(null);
                } else {
                    try {
                        tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                    } catch (Exception ex) {
                        tableSorter.setRowFilter(null);
                    }
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0, gap 8, right"));

        // Apply flat/rounded style via client properties (FlatLaf will pick these up)
        addButton.putClientProperty("JButton.buttonType", "roundRect");
        refreshButton.putClientProperty("JButton.buttonType", "roundRect");
        deleteButton.putClientProperty("JButton.buttonType", "roundRect");

        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD));
        refreshButton.setFont(refreshButton.getFont().deriveFont(Font.PLAIN));
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.PLAIN));

        // emphasize delete
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setOpaque(true);

        panel.add(refreshButton, "h 32!");
        panel.add(deleteButton, "h 32!");
        panel.add(addButton, "h 32!");

        return panel;
    }

    private void configureTable() {
        inventarisTable.setAutoCreateRowSorter(true);
        tableSorter = new TableRowSorter<>(inventarisTableModel);
        inventarisTable.setRowSorter(tableSorter);
        inventarisTable.setFillsViewportHeight(true);
        inventarisTable.setRowHeight(26);
        inventarisTable.getTableHeader().setReorderingAllowed(false);
        inventarisTable.setShowGrid(false);

        // set preferred column widths
        TableColumnModel colModel = inventarisTable.getColumnModel();
        int[] widths = { 60, 450, 180, 160, 100 };
        for (int i = 0; i < widths.length && i < colModel.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            col.setPreferredWidth(widths[i]);
        }

        // Renderers for alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        // use center renderer for 'Jumlah' column instead of right-aligned
        DefaultTableCellRenderer jumlahCenterRenderer = new DefaultTableCellRenderer();
        jumlahCenterRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

        if (colModel.getColumnCount() > 0)
            colModel.getColumn(0).setCellRenderer(centerRenderer);
        if (colModel.getColumnCount() > 4)
            colModel.getColumn(4).setCellRenderer(jumlahCenterRenderer);

        // alternate row colors (striping)
        DefaultTableCellRenderer stripeRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = UIManager.getColor("Table.background");
            private final Color ODD = UIManager.getColor("Table.alternateRowBackground") != null
                    ? UIManager.getColor("Table.alternateRowBackground")
                    : new Color(245, 245, 245);

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                if (!isSelected)
                    c.setBackground((row % 2 == 0) ? EVEN : ODD);
                return c;
            }
        };

        inventarisTable.setDefaultRenderer(Object.class, stripeRenderer);
        inventarisTable.getTableHeader().setFont(inventarisTable.getTableHeader().getFont().deriveFont(Font.BOLD));
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JTable getInventarisTable() {
        return inventarisTable;
    }

    public InventarisTableModel getInventarisTableModel() {
        return inventarisTableModel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getTotalRecordsLabel() {
        return totalRecordsLabel;
    }
}
