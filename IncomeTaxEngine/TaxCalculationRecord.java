public class TaxCalculationRecord {

    private final String timestamp;
    private final double grossIncome;
    private final double otherDeductions;
    private final double standardDeduction;
    private final double taxableIncome;
    private final double oldTax;
    private final double newTax;
    private final String recommendedRegime;
    private final double finalTax;

    public TaxCalculationRecord(String timestamp, double grossIncome, double otherDeductions,
                                double standardDeduction, double taxableIncome, double oldTax,
                                double newTax, String recommendedRegime, double finalTax) {
        this.timestamp = timestamp;
        this.grossIncome = grossIncome;
        this.otherDeductions = otherDeductions;
        this.standardDeduction = standardDeduction;
        this.taxableIncome = taxableIncome;
        this.oldTax = oldTax;
        this.newTax = newTax;
        this.recommendedRegime = recommendedRegime;
        this.finalTax = finalTax;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getGrossIncome() {
        return grossIncome;
    }

    public double getOtherDeductions() {
        return otherDeductions;
    }

    public double getStandardDeduction() {
        return standardDeduction;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public double getOldTax() {
        return oldTax;
    }

    public double getNewTax() {
        return newTax;
    }

    public String getRecommendedRegime() {
        return recommendedRegime;
    }

    public double getFinalTax() {
        return finalTax;
    }
}
