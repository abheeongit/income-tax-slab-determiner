public class TaxCalculator {

    private static final double[][] OLD_REGIME_SLABS = {
        { 0, 250000, 0.00 },
        { 250000, 500000, 0.05 },
        { 500000, 1000000, 0.20 },
        { 1000000, Double.MAX_VALUE, 0.30 }
    };

    private static final double[][] NEW_REGIME_SLABS = {
        { 0, 300000, 0.00 },
        { 300000, 600000, 0.05 },
        { 600000, 900000, 0.10 },
        { 900000, 1200000, 0.15 },
        { 1200000, 1500000, 0.20 },
        { 1500000, Double.MAX_VALUE, 0.30 }
    };

    private static final double OLD_REBATE_AMOUNT = 12500;
    private static final double NEW_REBATE_AMOUNT = 25000;
    private static final double CESS_RATE = 0.04;

    // Computes taxable income after deductions.
    public double calculateTaxableIncome(double grossIncome, double standardDeduction, double otherDeductions) {
        double taxableIncome = grossIncome - standardDeduction - otherDeductions;
        return Math.max(0.0, taxableIncome);
    }

    // Calculates tax for the old regime.
    public double[] calculateOldTax(double grossIncome, double standardDeduction, double otherDeductions,
                                   double rebateThreshold) {
        return calculateTaxBreakdown(grossIncome, standardDeduction, otherDeductions, rebateThreshold, "Old Regime");
    }

    // Calculates tax for the new regime.
    public double[] calculateNewTax(double grossIncome, double standardDeduction, double otherDeductions,
                                   double rebateThreshold) {
        return calculateTaxBreakdown(grossIncome, standardDeduction, otherDeductions, rebateThreshold, "New Regime");
    }

    // Calculates the full tax breakdown for one regime.
    // Returns: taxable income, gross tax, rebate, tax after rebate, cess, net tax.
    public double[] calculateTaxBreakdown(double grossIncome, double standardDeduction, double otherDeductions,
                                          double rebateThreshold, String regime) {
        double taxableIncome = calculateTaxableIncome(grossIncome, standardDeduction, otherDeductions);
        double grossTax = calculateMarginalTax(taxableIncome, getSlabs(regime));
        double rebate = applyRebate(taxableIncome, grossTax, rebateThreshold, regime);
        double taxAfterRebate = round(grossTax - rebate);
        double cess = calculateCess(taxAfterRebate);
        double netTax = round(taxAfterRebate + cess);
        return new double[] { taxableIncome, grossTax, rebate, taxAfterRebate, cess, netTax };
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
    public double applyRebate(double taxableIncome, double grossTax, double rebateThreshold, String regime) {
        if (taxableIncome <= rebateThreshold) {
            return round(Math.min(grossTax, getRebateAmount(regime)));
        }
        return 0.0;
    }

    // Calculates health and education cess on the tax amount.
    public double calculateCess(double tax) {
        return round(tax * CESS_RATE);
    }

    // Compares both regimes and returns the cheaper one.
    public String compareRegimes(double oldFinalTax, double newFinalTax) {
        if (Math.abs(oldFinalTax - newFinalTax) < 0.01) {
            return "Either Regime";
        }
        return oldFinalTax < newFinalTax ? "Old Regime" : "New Regime";
    }

    // Returns the slab table for the selected regime.
    public double[][] getSlabs(String regime) {
        if ("Old Regime".equals(regime)) {
            return OLD_REGIME_SLABS;
        }
        return NEW_REGIME_SLABS;
    }

    // Returns the rebate amount for the selected regime.
    private double getRebateAmount(String regime) {
        switch (regime) {
            case "Old Regime":
                return OLD_REBATE_AMOUNT;
            case "New Regime":
            default:
                return NEW_REBATE_AMOUNT;
        }
    }

    // Rounds values to two decimal places.
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}