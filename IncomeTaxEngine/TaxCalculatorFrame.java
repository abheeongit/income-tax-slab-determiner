import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TaxCalculatorFrame extends JFrame {

    private final TaxCalculator taxCalculator = new TaxCalculator();
    private final FileManager fileManager = new FileManager();

    private final JTextField grossIncomeField = new JTextField("1200000");
    private final JTextField otherDeductionsField = new JTextField("150000");
    private final JTextField standardDeductionField = new JTextField("50000");
    private final JTextField rebateThresholdField = new JTextField("700000");
    private final JComboBox<String> regimeSelector = new JComboBox<>(new String[] {
            "Compare Both Regimes", "Old Regime", "New Regime"
    });
    private final JTextArea outputArea = new JTextArea();

    private TaxCalculationRecord lastRecord;

    public TaxCalculatorFrame() {
        setTitle("Income Tax Slab Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(16, 16));

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        content.setBackground(new Color(245, 242, 232));
        setContentPane(content);

        content.add(buildHeader(), BorderLayout.NORTH);
        content.add(buildCenterPanel(), BorderLayout.CENTER);
        content.add(buildActions(), BorderLayout.SOUTH);

        outputArea.setText("Enter income details, choose a regime action, and calculate.");
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Income Tax Slab Engine");
        title.setFont(new Font("Serif", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Desktop workflow for tax calculation, comparison, and saved history.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16));
        center.setOpaque(false);
        center.add(buildInputPanel());
        center.add(buildOutputPanel());
        return center;
    }

    private JPanel buildInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 178, 145)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        inputPanel.setBackground(new Color(255, 251, 240));

        inputPanel.add(new JLabel("Gross Annual Income"));
        inputPanel.add(grossIncomeField);
        inputPanel.add(new JLabel("Other Deductions"));
        inputPanel.add(otherDeductionsField);
        inputPanel.add(new JLabel("Standard Deduction"));
        inputPanel.add(standardDeductionField);
        inputPanel.add(new JLabel("Rebate Threshold"));
        inputPanel.add(rebateThresholdField);
        inputPanel.add(new JLabel("Mode"));
        inputPanel.add(regimeSelector);

        return inputPanel;
    }

    private JScrollPane buildOutputPanel() {
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(30, 33, 41));
        outputArea.setForeground(new Color(232, 237, 241));
        outputArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return new JScrollPane(outputArea);
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel();
        actions.setOpaque(false);

        JButton calculateButton = new JButton("Calculate");
        JButton saveButton = new JButton("Save Result");
        JButton recordsButton = new JButton("Open Saved Results");
        JButton clearButton = new JButton("Clear");

        calculateButton.addActionListener(event -> calculate());
        saveButton.addActionListener(event -> saveLastCalculation());
        recordsButton.addActionListener(event -> openRecordsWindow());
        clearButton.addActionListener(event -> clearForm());

        actions.add(calculateButton);
        actions.add(saveButton);
        actions.add(recordsButton);
        actions.add(clearButton);
        return actions;
    }

    private void calculate() {
        try {
            double grossIncome = parseAmount(grossIncomeField, "Gross Annual Income");
            double otherDeductions = parseAmount(otherDeductionsField, "Other Deductions");
            double standardDeduction = parseAmount(standardDeductionField, "Standard Deduction");
            double rebateThreshold = parseAmount(rebateThresholdField, "Rebate Threshold");
            String selectedMode = (String) regimeSelector.getSelectedItem();

            if ("Old Regime".equals(selectedMode)) {
                double[] result = taxCalculator.calculateOldTax(
                        grossIncome, standardDeduction, otherDeductions, rebateThreshold);
                lastRecord = new TaxCalculationRecord(LocalDateTime.now().toString(), grossIncome, otherDeductions,
                        standardDeduction, result[0], result[5], result[5], "Old Regime", result[5]);
                outputArea.setText(buildSingleRegimeReport("Old Regime", grossIncome, otherDeductions,
                        standardDeduction, result));
                return;
            }

            if ("New Regime".equals(selectedMode)) {
                double[] result = taxCalculator.calculateNewTax(
                        grossIncome, standardDeduction, otherDeductions, rebateThreshold);
                lastRecord = new TaxCalculationRecord(LocalDateTime.now().toString(), grossIncome, otherDeductions,
                        standardDeduction, result[0], result[5], result[5], "New Regime", result[5]);
                outputArea.setText(buildSingleRegimeReport("New Regime", grossIncome, otherDeductions,
                        standardDeduction, result));
                return;
            }

            double[] oldResult = taxCalculator.calculateOldTax(
                    grossIncome, standardDeduction, otherDeductions, rebateThreshold);
            double[] newResult = taxCalculator.calculateNewTax(
                    grossIncome, standardDeduction, otherDeductions, rebateThreshold);
            String recommendedRegime = taxCalculator.compareRegimes(oldResult[5], newResult[5]);
            double finalTax = "Old Regime".equals(recommendedRegime) ? oldResult[5]
                    : "New Regime".equals(recommendedRegime) ? newResult[5] : oldResult[5];

            lastRecord = new TaxCalculationRecord(LocalDateTime.now().toString(), grossIncome, otherDeductions,
                    standardDeduction, oldResult[0], oldResult[5], newResult[5], recommendedRegime, finalTax);
            outputArea.setText(buildComparisonReport(grossIncome, otherDeductions, standardDeduction,
                    oldResult, newResult, recommendedRegime));
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveLastCalculation() {
        if (lastRecord == null) {
            JOptionPane.showMessageDialog(this, "Run a calculation before saving.", "Nothing To Save",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        fileManager.saveRecord(lastRecord.getTimestamp(), lastRecord.getGrossIncome(),
                lastRecord.getOtherDeductions(), lastRecord.getStandardDeduction(),
                lastRecord.getTaxableIncome(), lastRecord.getOldTax(), lastRecord.getNewTax(),
                lastRecord.getRecommendedRegime(), lastRecord.getFinalTax());

        JOptionPane.showMessageDialog(this, "Calculation saved to records.csv.", "Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openRecordsWindow() {
        new RecordsFrame(fileManager).setVisible(true);
    }

    private void clearForm() {
        grossIncomeField.setText("");
        otherDeductionsField.setText("");
        standardDeductionField.setText("");
        rebateThresholdField.setText("");
        regimeSelector.setSelectedIndex(0);
        outputArea.setText("Enter income details, choose a regime action, and calculate.");
        lastRecord = null;
    }

    private double parseAmount(JTextField field, String label) {
        String value = field.getText().trim();
        try {
            double amount = Double.parseDouble(value);
            if (amount < 0) {
                throw new IllegalArgumentException(label + " must be a non-negative number.");
            }
            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
    }

    private String buildSingleRegimeReport(String regime, double grossIncome, double otherDeductions,
                                           double standardDeduction, double[] result) {
        return regime + System.lineSeparator()
                + repeat("=") + System.lineSeparator()
                + "Gross Income         : " + formatCurrency(grossIncome) + System.lineSeparator()
                + "Other Deductions     : " + formatCurrency(otherDeductions) + System.lineSeparator()
                + "Standard Deduction   : " + formatCurrency(standardDeduction) + System.lineSeparator()
                + "Taxable Income       : " + formatCurrency(result[0]) + System.lineSeparator()
                + "Gross Tax            : " + formatCurrency(result[1]) + System.lineSeparator()
                + "Rebate               : " + formatCurrency(result[2]) + System.lineSeparator()
                + "Tax After Rebate     : " + formatCurrency(result[3]) + System.lineSeparator()
                + "Health/Edu Cess      : " + formatCurrency(result[4]) + System.lineSeparator()
                + "Net Tax              : " + formatCurrency(result[5]);
    }

    private String buildComparisonReport(double grossIncome, double otherDeductions, double standardDeduction,
                                         double[] oldResult, double[] newResult, String recommendedRegime) {
        double savings = Math.abs(oldResult[5] - newResult[5]);
        return "Old Regime" + System.lineSeparator()
                + repeat("=") + System.lineSeparator()
                + formatResultBlock(grossIncome, otherDeductions, standardDeduction, oldResult)
                + System.lineSeparator() + System.lineSeparator()
                + "New Regime" + System.lineSeparator()
                + repeat("=") + System.lineSeparator()
                + formatResultBlock(grossIncome, otherDeductions, standardDeduction, newResult)
                + System.lineSeparator() + System.lineSeparator()
                + "Recommendation" + System.lineSeparator()
                + repeat("=") + System.lineSeparator()
                + "Recommended Regime  : " + recommendedRegime + System.lineSeparator()
                + "Savings             : " + formatCurrency(savings);
    }

    private String formatResultBlock(double grossIncome, double otherDeductions, double standardDeduction,
                                     double[] result) {
        return "Gross Income         : " + formatCurrency(grossIncome) + System.lineSeparator()
                + "Other Deductions     : " + formatCurrency(otherDeductions) + System.lineSeparator()
                + "Standard Deduction   : " + formatCurrency(standardDeduction) + System.lineSeparator()
                + "Taxable Income       : " + formatCurrency(result[0]) + System.lineSeparator()
                + "Gross Tax            : " + formatCurrency(result[1]) + System.lineSeparator()
                + "Rebate               : " + formatCurrency(result[2]) + System.lineSeparator()
                + "Tax After Rebate     : " + formatCurrency(result[3]) + System.lineSeparator()
                + "Health/Edu Cess      : " + formatCurrency(result[4]) + System.lineSeparator()
                + "Net Tax              : " + formatCurrency(result[5]);
    }

    private String repeat(String marker) {
        return "==============================";
    }

    private String formatCurrency(double value) {
        return String.format("Rs. %,.2f", value);
    }
}
