public class Utils {

    // Prints a separator line for cleaner console output.
    public static void printLine() {
        System.out.println("============================================================");
    }

    // Pauses the screen until the user presses Enter.
    public static void pause(java.util.Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    // Clears the screen by printing blank lines.
    public static void clearScreen() {
        for (int index = 0; index < 40; index++) {
            System.out.println();
        }
    }

    // Formats numbers as currency for display.
    public static String formatCurrency(double value) {
        return String.format("Rs. %,.2f", value);
    }

    // Validates non-negative numeric input.
    public static boolean validatePositiveNumber(double value) {
        return value >= 0;
    }
}