package igblonchemistry.chemistry;

public class Compounds {

    //Table of heat of vaporizations: https://en.wikipedia.org/wiki/Enthalpy_of_vaporization

    public static Compound Salt;
    public static Compound Water;

    public static void register() {
        Water = new Compound("Water")
                .setHeatOfVaporization(40650)
                .setBoilingPoint(373)
                .setColor(0x2d6ce0)
                .setDensity(1000)
                .setMolarMass(18.01)
                .setPH(7);

        Salt = new Compound("Salt")
                .addSolubilityInfo(Water, temperature -> 356.5 + 0.0035 * Math.pow(temperature - 273, 2))
                .setDensity(2160)
                .setMolarMass(58.44)
                .setColor(0xffffff)
                .setPH(7);
    }
}