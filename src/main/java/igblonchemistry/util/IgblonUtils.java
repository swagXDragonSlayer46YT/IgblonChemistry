package igblonchemistry.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class IgblonUtils {

    //0 digits to round to nearest whole number
    public static double roundToDigit(double number, int digits) {
        return (Math.round(number * Math.pow(10, digits)) / Math.pow(10, digits));
    }

    public static String formatNumber(double number, int digits) {
        int wholeNumber = (int) roundToDigit(number, 0);
        double decimals = number - wholeNumber;

        String numberString = Integer.toString(wholeNumber);
        String newString = "";
        for (int i = 0; i < numberString.length(); i++) {
            if (i % 3 == 0 && i > 0) {
                newString = "," + newString;
            }
            newString = numberString.substring(numberString.length() - i - 1, numberString.length() - i) + newString;
        }
        return newString + Double.toString(roundToDigit(decimals, digits)).substring(2);
    }
}
