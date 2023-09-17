package igblonchemistry.chemistry;

public class Compounds {

    //Table of heat of vaporizations: https://en.wikipedia.org/wiki/Enthalpy_of_vaporization

    public static Compound Salt;
    public static Compound Water;

    public static void register() {
        Water = new Compound("water")
                .setHeatOfVaporization(40650)
                .setColor(0x2d6ce0)
                .setDensity(1000)
                .setMolarMass(18.01);

        Salt = new Compound("salt")
                .addSolubilityInfo(Water, temperature -> 356.5 + 0.0035 * Math.pow(temperature - 273, 2))
                .setDensity(2160)
                .setMolarMass(58.44)
                .setColor(0xffffff);
    }
}