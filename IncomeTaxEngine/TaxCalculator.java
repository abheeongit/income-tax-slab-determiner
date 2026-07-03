public class TaxCalculator {

    // Computes taxable income after deductions.
    public double calculateTaxableIncome(double grossIncome, double standardDeduction, double otherDeductions) {
        double taxableIncome = grossIncome - standardDeduction - otherDeductions;
        return Math.max(0.0, taxableIncome);
    }

    // Calculates all tax values for the selected regime.
    public TaxRecord calculateForRegime(double grossIncome, double standardDeduction, double otherDeductions,
                                       double rebateThreshold, String regime) {
        TaxRecord record = new TaxRecord();
        double taxableIncome = calculateTaxableIncome(grossIncome, standardDeduction, otherDeductions);
        double grossTax = calculateMarginalTax(taxableIncome, getSlabs(regime));
        double rebate = calculateRebate(taxableIncome, grossTax, rebateThreshold, regime);
        double taxAfterRebate = round(grossTax - rebate);
        double cess = calculateCess(taxAfterRebate);
        double netTax = round(taxAfterRebate + cess);

        record.setGrossIncome(grossIncome);
        record.setStandardDeduction(standardDeduction);
        record.setOtherDeductions(otherDeductions);
        record.setRebateThreshold(rebateThreshold);
        record.setTaxableIncome(taxableIncome);
        record.setGrossTax(grossTax);
        record.setRebate(rebate);
        record.setTaxAfterRebate(taxAfterRebate);
        record.setCess(cess);
        record.setNetTax(netTax);
        return record;
    }

    // Computes the old-regime final tax including rebate and cess.
    public double calculateOldTax(double taxableIncome) {
        double tax = calculateMarginalTax(taxableIncome, TaxSlabs.OLD_REGIME_SLABS);
        tax = applyRebate(taxableIncome, tax, "Old Regime");
        return tax + calculateCess(tax);
    }

    // Computes the new-regime final tax including rebate and cess.
    public double calculateNewTax(double taxableIncome) {
        double tax = calculateMarginalTax(taxableIncome, TaxSlabs.NEW_REGIME_SLABS);
        tax = applyRebate(taxableIncome, tax, "New Regime");
        return tax + calculateCess(tax);
    }

    // Applies marginal taxation across the supplied slab table.
    public double calculateMarginalTax(double taxableIncome, double[][] slabs) {
        double totalTax = 0.0;

        for (double[] slab : slabs) {
            double lowerLimit = slab[0];
            double upperLimit = slab[1];
            double rate = slab[2];

            if (taxableIncome <= lowerLimit) {
                continue;
            }

            double incomeInSlab = Math.min(taxableIncome, upperLimit) - lowerLimit;
            if (incomeInSlab > 0) {
                totalTax += incomeInSlab * rate;
            }
        }

        return round(totalTax);
    }

    // Applies the rebate before cess is added.
    public double applyRebate(double taxableIncome, double tax, String regime) {
        if (taxableIncome <= getRebateThreshold(regime)) {
            tax = Math.max(0.0, tax - getRebateAmount(regime));
        }
        return round(tax);
    }

    // Calculates rebate for the chosen regime using the user-provided threshold.
    public double calculateRebate(double taxableIncome, double grossTax, double rebateThreshold, String regime) {
        if (taxableIncome <= rebateThreshold) {
            return round(Math.min(grossTax, getRebateAmount(regime)));
        }
        return 0.0;
    }

    // Calculates health and education cess on the tax amount.
    public double calculateCess(double tax) {
        return round(tax * TaxSlabs.CESS_RATE);
    }

    // Recommends the cheaper regime.
    public String recommendRegime(double oldFinalTax, double newFinalTax) {
        if (Math.abs(oldFinalTax - newFinalTax) < 0.01) {
            return "Either Regime";
        }
        return oldFinalTax < newFinalTax ? "Old Regime" : "New Regime";
    }

    // Returns the slab table for the selected regime.
    public double[][] getSlabs(String regime) {
        if ("Old Regime".equals(regime)) {
            return TaxSlabs.OLD_REGIME_SLABS;
        }
        return TaxSlabs.NEW_REGIME_SLABS;
    }

    // Returns the rebate threshold for the selected regime.
    private double getRebateThreshold(String regime) {
        switch (regime) {
            case "Old Regime":
                return TaxSlabs.OLD_REBATE_THRESHOLD;
            case "New Regime":
            default:
                return TaxSlabs.NEW_REBATE_THRESHOLD;
        }
    }

    // Returns the rebate amount for the selected regime.
    private double getRebateAmount(String regime) {
        switch (regime) {
            case "Old Regime":
                return TaxSlabs.OLD_REBATE_AMOUNT;
            case "New Regime":
            default:
                return TaxSlabs.NEW_REBATE_AMOUNT;
        }
    }

    // Rounds values to two decimal places.
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}