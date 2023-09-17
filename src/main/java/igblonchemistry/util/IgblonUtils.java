package igblonchemistry.util;

public class IgblonUtils {

    //0 digits to round to nearest whole number
    public static int roundToDigit(double number, int digits) {
        return (int) (Math.round(number * Math.pow(10, digits)) / Math.pow(10, digits));
    }
}
