import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final TaxCalculator taxCalculator;
    private final FileManager fileManager;
    private TaxRecord lastCalculation;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.taxCalculator = new TaxCalculator();
        this.fileManager = new FileManager();
    }

    // Starts the menu loop and keeps the application running until exit.
    public void start() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    calculateTax();
                    break;
                case 2:
                    compareBothRegimes();
                    break;
                case 3:
                    fileManager.readSavedRecords();
                    Utils.pause(scanner);
                    break;
                case 4:
                    saveLastCalculation();
                    break;
                case 5:
                    System.out.println("Thank you for using Income Tax Computation Engine.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Displays the main terminal menu.
    private void displayMenu() {
        Utils.printLine();
        System.out.println("        INCOME TAX CALCULATOR");
        Utils.printLine();
        System.out.println("1. Calculate Tax");
        System.out.println("2. Compare Old vs New Regime");
        System.out.println("3. View Saved Calculations");
        System.out.println("4. Save Last Calculation");
        System.out.println("5. Exit");
        System.out.print("Enter Choice: ");
    }

    // Reads a valid menu choice from the user.
    private int readMenuChoice() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 5) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.print("Enter a valid choice (1-5): ");
        }
    }

    // Reads and validates the regime choice from the user.
    private String readRegimeChoice() {
        while (true) {
            System.out.println("Choose Regime");
            System.out.println("1. Old Regime");
            System.out.println("2. New Regime");
            System.out.print("Enter Choice: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return "Old Regime";
                case "2":
                    return "New Regime";
                default:
                    System.out.println("Invalid regime choice. Please try again.");
            }
        }
    }

    // Reads a non-empty text value from the scanner.
    private String readNonEmptyText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    // Reads a non-negative amount and keeps asking until valid.
    private double readNonNegativeAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (Utils.validatePositiveNumber(value)) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a valid non-negative number.");
        }
    }

    // Calculates tax for the selected regime and stores the latest result.
    private void calculateTax() {
        TaxRecord input = readTaxInputs();
        String selectedRegime = readRegimeChoice();
        TaxRecord result = calculateBothRegimes(input, selectedRegime);

        displaySingleReport(result);
        lastCalculation = result;
        Utils.pause(scanner);
    }

    // Calculates both regimes and shows a side-by-side style comparison.
    private void compareBothRegimes() {
        TaxRecord input = readTaxInputs();
        TaxRecord result = calculateBothRegimes(input, "Compare Both");

        displayComparisonReport(result);
        lastCalculation = result;
        Utils.pause(scanner);
    }

    // Saves the latest calculation to file.
    private void saveLastCalculation() {
        if (lastCalculation == null) {
            System.out.println("No calculation available. Please calculate tax first.");
        } else {
            fileManager.saveCalculation(lastCalculation);
            System.out.println("Last calculation saved successfully.");
        }
        Utils.pause(scanner);
    }

    // Reads the calculator inputs.
    private TaxRecord readTaxInputs() {
        TaxRecord record = new TaxRecord();
        record.setGrossIncome(readNonNegativeAmount("Gross Annual Income: "));
        record.setOtherDeductions(readNonNegativeAmount("Other Deductions: "));
        record.setStandardDeduction(readNonNegativeAmount("Standard Deduction: "));
        record.setRebateThreshold(readNonNegativeAmount("Rebate Threshold: "));
        return record;
    }

    // Calculates both regimes and populates the final result record.
    private TaxRecord calculateBothRegimes(TaxRecord input, String selectedRegime) {
        TaxRecord oldRegime = taxCalculator.calculateForRegime(
                input.getGrossIncome(),
                input.getStandardDeduction(),
                input.getOtherDeductions(),
                input.getRebateThreshold(),
                "Old Regime");

        TaxRecord newRegime = taxCalculator.calculateForRegime(
                input.getGrossIncome(),
                input.getStandardDeduction(),
                input.getOtherDeductions(),
                input.getRebateThreshold(),
                "New Regime");

        TaxRecord result = new TaxRecord();
        result.setGrossIncome(input.getGrossIncome());
        result.setOtherDeductions(input.getOtherDeductions());
        result.setStandardDeduction(input.getStandardDeduction());
        result.setRebateThreshold(input.getRebateThreshold());
        result.setTaxableIncome(oldRegime.getTaxableIncome());
        result.setGrossTax("Old Regime".equals(selectedRegime) ? oldRegime.getGrossTax() : newRegime.getGrossTax());
        result.setRebate("Old Regime".equals(selectedRegime) ? oldRegime.getRebate() : newRegime.getRebate());
        result.setTaxAfterRebate("Old Regime".equals(selectedRegime) ? oldRegime.getTaxAfterRebate() : newRegime.getTaxAfterRebate());
        result.setCess("Old Regime".equals(selectedRegime) ? oldRegime.getCess() : newRegime.getCess());
        result.setNetTax("Old Regime".equals(selectedRegime) ? oldRegime.getNetTax() : newRegime.getNetTax());
        result.setOldTax(oldRegime.getNetTax());
        result.setNewTax(newRegime.getNetTax());
        result.setRecommendedRegime(taxCalculator.recommendRegime(oldRegime.getNetTax(), newRegime.getNetTax()));
        result.setSelectedRegime(selectedRegime);
        result.setTimestamp(java.time.LocalDateTime.now().toString());
        return result;
    }

    // Displays a single-regime income tax report.
    private void displaySingleReport(TaxRecord record) {
        System.out.println();
        Utils.printLine();
        System.out.println("            INCOME TAX REPORT");
        Utils.printLine();
        System.out.println("Gross Income        : " + Utils.formatCurrency(record.getGrossIncome()));
        System.out.println("Other Deductions    : " + Utils.formatCurrency(record.getOtherDeductions()));
        System.out.println("Standard Deduction  : " + Utils.formatCurrency(record.getStandardDeduction()));
        System.out.println("Taxable Income      : " + Utils.formatCurrency(record.getTaxableIncome()));
        System.out.println("Gross Tax           : " + Utils.formatCurrency(record.getGrossTax()));
        System.out.println("Rebate              : " + Utils.formatCurrency(record.getRebate()));
        System.out.println("Tax After Rebate    : " + Utils.formatCurrency(record.getTaxAfterRebate()));
        System.out.println("Health & Edu Cess   : " + Utils.formatCurrency(record.getCess()));
        System.out.println("Net Tax             : " + Utils.formatCurrency(record.getNetTax()));
        Utils.printLine();
    }

    // Displays both regimes and the recommendation.
    private void displayComparisonReport(TaxRecord record) {
        TaxRecord oldRegime = taxCalculator.calculateForRegime(
            record.getGrossIncome(),
            record.getStandardDeduction(),
            record.getOtherDeductions(),
            record.getRebateThreshold(),
            "Old Regime");
        TaxRecord newRegime = taxCalculator.calculateForRegime(
            record.getGrossIncome(),
            record.getStandardDeduction(),
            record.getOtherDeductions(),
            record.getRebateThreshold(),
            "New Regime");

        System.out.println();
        Utils.printLine();
        System.out.println("            OLD REGIME");
        Utils.printLine();
        System.out.println("Taxable Income      : " + Utils.formatCurrency(record.getTaxableIncome()));
        System.out.println("Gross Tax           : " + Utils.formatCurrency(oldRegime.getGrossTax()));
        System.out.println("Rebate              : " + Utils.formatCurrency(oldRegime.getRebate()));
        System.out.println("Tax After Rebate    : " + Utils.formatCurrency(oldRegime.getTaxAfterRebate()));
        System.out.println("Cess                : " + Utils.formatCurrency(oldRegime.getCess()));
        System.out.println("Net Tax             : " + Utils.formatCurrency(record.getOldTax()));
        System.out.println();
        Utils.printLine();
        System.out.println("            NEW REGIME");
        Utils.printLine();
        System.out.println("Taxable Income      : " + Utils.formatCurrency(record.getTaxableIncome()));
        System.out.println("Gross Tax           : " + Utils.formatCurrency(newRegime.getGrossTax()));
        System.out.println("Rebate              : " + Utils.formatCurrency(newRegime.getRebate()));
        System.out.println("Tax After Rebate    : " + Utils.formatCurrency(newRegime.getTaxAfterRebate()));
        System.out.println("Cess                : " + Utils.formatCurrency(newRegime.getCess()));
        System.out.println("Net Tax             : " + Utils.formatCurrency(record.getNewTax()));
        System.out.println();
        Utils.printLine();
        System.out.println("RECOMMENDATION");
        Utils.printLine();
        System.out.println("Recommended Regime : " + record.getRecommendedRegime());
        System.out.println("Tax Savings        : " + Utils.formatCurrency(Math.abs(record.getOldTax() - record.getNewTax())));
        System.out.println("Reason             : Lower net tax is recommended.");
        Utils.printLine();
    }
}