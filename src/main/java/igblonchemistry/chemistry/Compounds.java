package igblonchemistry.chemistry;

public class Compounds {

    //Table of heat of vaporizations: https://en.wikipedia.org/wiki/Enthalpy_of_vaporization

    public static Compound Salt;
    public static Compound Water;

    public static void register() {
        Water = new Compound("water")
                .setHeatOfVaporization(40650);

        //TODO: USE LAMBDA FUNCTIONS LATER ON
        Salt = new Compound("salt")
                .addSolubilityInfo(new SolubilityInfo(Water) {
                    public double calculateSolubility(double temperature) {
                        return 356.5 + 0.0035 * Math.pow(temperature - 273, 2);
                    }
                });
    }
}