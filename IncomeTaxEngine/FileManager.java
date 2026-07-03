import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    private static final String FILE_NAME = "records.csv";

    // Appends the latest tax calculation to the file.
    public void saveCalculation(TaxRecord record) {
        File file = new File(FILE_NAME);
        boolean writeHeader = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (writeHeader) {
                writer.write("Timestamp,GrossIncome,OtherDeductions,StandardDeduction,TaxableIncome,OldTax,NewTax,RecommendedRegime,FinalTax");
                writer.newLine();
            }

            writer.write(buildCsvLine(record));
            writer.newLine();
        } catch (IOException exception) {
            System.out.println("Unable to save record: " + exception.getMessage());
        }
    }

    // Reads and displays all saved records.
    public void readSavedRecords() {
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

    // Displays only the summary fields requested for saved calculations.
    private boolean displaySummaryLine(String line) {
        String[] columns = line.split(",");
        if (columns.length < 9 || !columns[0].startsWith("\"") || !columns[0].contains("T")) {
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

    // Removes CSV quotes for display only.
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "");
    }

    // Builds one CSV line from the employee data.
    private String buildCsvLine(TaxRecord record) {
        return escape(record.getTimestamp()) + ","
                + format(record.getGrossIncome()) + ","
                + format(record.getOtherDeductions()) + ","
                + format(record.getStandardDeduction()) + ","
                + format(record.getTaxableIncome()) + ","
                + format(record.getOldTax()) + ","
                + format(record.getNewTax()) + ","
                + escape(record.getRecommendedRegime()) + ","
                + format(record.getNetTax());
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