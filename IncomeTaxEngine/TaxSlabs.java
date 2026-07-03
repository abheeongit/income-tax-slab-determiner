public class TaxSlabs {

    public static final double[][] OLD_REGIME_SLABS = {
            { 0, 250000, 0.00 },
            { 250000, 500000, 0.05 },
            { 500000, 1000000, 0.20 },
            { 1000000, Double.MAX_VALUE, 0.30 }
    };

    public static final double[][] NEW_REGIME_SLABS = {
            { 0, 300000, 0.00 },
            { 300000, 600000, 0.05 },
            { 600000, 900000, 0.10 },
            { 900000, 1200000, 0.15 },
            { 1200000, 1500000, 0.20 },
            { 1500000, Double.MAX_VALUE, 0.30 }
    };

    public static final double OLD_REBATE_THRESHOLD = 500000;
    public static final double NEW_REBATE_THRESHOLD = 700000;
    public static final double OLD_REBATE_AMOUNT = 12500;
    public static final double NEW_REBATE_AMOUNT = 25000;
    public static final double CESS_RATE = 0.04;

    private TaxSlabs() {
    }
}