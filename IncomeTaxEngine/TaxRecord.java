public class TaxRecord {

    private double grossIncome;
    private double otherDeductions;
    private double standardDeduction;
    private double rebateThreshold;
    private double taxableIncome;
    private double oldTax;
    private double newTax;
    private String recommendedRegime;
    private double grossTax;
    private double rebate;
    private double taxAfterRebate;
    private double cess;
    private double netTax;
    private String selectedRegime;
    private String timestamp;

    public TaxRecord() {
    }

    public double getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(double grossIncome) {
        this.grossIncome = grossIncome;
    }

    public double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public double getStandardDeduction() {
        return standardDeduction;
    }

    public void setStandardDeduction(double standardDeduction) {
        this.standardDeduction = standardDeduction;
    }

    public double getRebateThreshold() {
        return rebateThreshold;
    }

    public void setRebateThreshold(double rebateThreshold) {
        this.rebateThreshold = rebateThreshold;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public double getOldTax() {
        return oldTax;
    }

    public void setOldTax(double oldTax) {
        this.oldTax = oldTax;
    }

    public double getNewTax() {
        return newTax;
    }

    public void setNewTax(double newTax) {
        this.newTax = newTax;
    }

    public String getRecommendedRegime() {
        return recommendedRegime;
    }

    public void setRecommendedRegime(String recommendedRegime) {
        this.recommendedRegime = recommendedRegime;
    }

    public double getGrossTax() {
        return grossTax;
    }

    public void setGrossTax(double grossTax) {
        this.grossTax = grossTax;
    }

    public double getRebate() {
        return rebate;
    }

    public void setRebate(double rebate) {
        this.rebate = rebate;
    }

    public double getTaxAfterRebate() {
        return taxAfterRebate;
    }

    public void setTaxAfterRebate(double taxAfterRebate) {
        this.taxAfterRebate = taxAfterRebate;
    }

    public double getCess() {
        return cess;
    }

    public void setCess(double cess) {
        this.cess = cess;
    }

    public double getNetTax() {
        return netTax;
    }

    public void setNetTax(double netTax) {
        this.netTax = netTax;
    }

    public String getSelectedRegime() {
        return selectedRegime;
    }

    public void setSelectedRegime(String selectedRegime) {
        this.selectedRegime = selectedRegime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}