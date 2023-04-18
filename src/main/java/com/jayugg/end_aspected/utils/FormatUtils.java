package com.jayugg.end_aspected.utils;

public class FormatUtils {
    public static String formatNumber(double num) {
        if (num == (int) num) {
            // If the number is a whole number, just return it as a string
            return String.valueOf((int) num);
        } else {
            // If the number has decimal places, format it to display the first decimal digit (if it's not 0)
            String formatted = String.format("%.1f", num);
            if (formatted.endsWith(".0")) {
                // If the first decimal digit is 0, just return the whole number without the decimal point
                return String.valueOf((int) num);
            } else {
                // If the first decimal digit is not 0, return the formatted string with the decimal point
                return formatted;
            }
        }
    }
}
