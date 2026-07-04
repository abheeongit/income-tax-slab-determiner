import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RecordsFrame extends JFrame {

    private final FileManager fileManager;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Timestamp", "Gross Income", "Taxable Income", "Old Tax", "New Tax",
                    "Recommended Regime", "Final Tax" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public RecordsFrame(FileManager fileManager) {
        this.fileManager = fileManager;

        setTitle("Saved Tax Records");
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.setBackground(new Color(248, 246, 239));
        setContentPane(content);

        JLabel header = new JLabel("Saved Calculations");
        header.setFont(new Font("Serif", Font.BOLD, 24));
        content.add(header, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> loadRecords());

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.add(refreshButton);
        content.add(footer, BorderLayout.SOUTH);

        loadRecords();
    }

    private void loadRecords() {
        tableModel.setRowCount(0);

        List<TaxCalculationRecord> records = fileManager.readRecords();
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved records found yet.", "Saved Results",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (TaxCalculationRecord record : records) {
            tableModel.addRow(new Object[] {
                    record.getTimestamp(),
                    formatCurrency(record.getGrossIncome()),
                    formatCurrency(record.getTaxableIncome()),
                    formatCurrency(record.getOldTax()),
                    formatCurrency(record.getNewTax()),
                    record.getRecommendedRegime(),
                    formatCurrency(record.getFinalTax())
            });
        }
    }

    private String formatCurrency(double value) {
        return String.format("Rs. %,.2f", value);
    }
}
