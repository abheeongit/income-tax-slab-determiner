import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String FILE_NAME = System.getProperty("user.dir") + File.separator + "records.csv";

    // Appends the latest tax calculation to the file.
    public void saveRecord(String timestamp, double grossIncome, double otherDeductions, double standardDeduction,
                           double taxableIncome, double oldTax, double newTax, String recommendedRegime,
                           double finalTax) {
        File file = new File(FILE_NAME);
        boolean writeHeader = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (writeHeader) {
                writer.write("Timestamp,GrossIncome,OtherDeductions,StandardDeduction,TaxableIncome,OldTax,NewTax,RecommendedRegime,FinalTax");
                writer.newLine();
            }

            appendToCSV(writer, timestamp, grossIncome, otherDeductions, standardDeduction, taxableIncome,
                    oldTax, newTax, recommendedRegime, finalTax);
            writer.newLine();
        } catch (IOException exception) {
            System.out.println("Unable to save record: " + exception.getMessage());
        }
    }

    // Reads and displays all saved records.
    public void viewRecords() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No saved records found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("Timestamp | Gross Income | Taxable Income | Old Tax | New Tax | Recommended Regime | Net Tax");
            String line;
            boolean hasData = false;

            while ((line = reader.readLine()) != null) {
                if (displaySummaryLine(line)) {
                    hasData = true;
                }
            }

            if (!hasData) {
                System.out.println("No saved records found.");
            }
        } catch (IOException exception) {
            System.out.println("Unable to read records: " + exception.getMessage());
        }
    }

    // Reads all saved records for the Swing table view.
    public List<TaxCalculationRecord> readRecords() {
        List<TaxCalculationRecord> records = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TaxCalculationRecord record = parseRecord(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to read records: " + exception.getMessage());
        }

        return records;
    }

    // Displays only the summary fields requested for saved calculations.
    private boolean displaySummaryLine(String line) {
        String[] columns = line.split(",");
        if (columns.length < 9 || "Timestamp".equals(columns[0])) {
            return false;
        }

        System.out.println(clean(columns[0]) + " | "
                + clean(columns[1]) + " | "
                + clean(columns[4]) + " | "
                + clean(columns[5]) + " | "
                + clean(columns[6]) + " | "
                + clean(columns[7]) + " | "
                + clean(columns[8]));
        return true;
    }

    private TaxCalculationRecord parseRecord(String line) {
        String[] columns = line.split(",");
        if (columns.length < 9 || "Timestamp".equals(columns[0])) {
            return null;
        }

        try {
            return new TaxCalculationRecord(
                    clean(columns[0]),
                    Double.parseDouble(clean(columns[1])),
                    Double.parseDouble(clean(columns[2])),
                    Double.parseDouble(clean(columns[3])),
                    Double.parseDouble(clean(columns[4])),
                    Double.parseDouble(clean(columns[5])),
                    Double.parseDouble(clean(columns[6])),
                    clean(columns[7]),
                    Double.parseDouble(clean(columns[8])));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    // Removes CSV quotes for display only.
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "");
    }

    // Appends one CSV line.
    public void appendToCSV(BufferedWriter writer, String timestamp, double grossIncome, double otherDeductions,
                            double standardDeduction, double taxableIncome, double oldTax, double newTax,
                            String recommendedRegime, double finalTax) throws IOException {
        writer.write(escape(timestamp) + ","
                + format(grossIncome) + ","
                + format(otherDeductions) + ","
                + format(standardDeduction) + ","
                + format(taxableIncome) + ","
                + format(oldTax) + ","
                + format(newTax) + ","
                + escape(recommendedRegime) + ","
                + format(finalTax));
    }

    // Wraps text values for simple CSV safety.
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    // Formats numeric values with two decimals.
    private String format(double value) {
        return String.format("%.2f", value);
    }
}
