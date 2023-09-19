package igblonchemistry.chemistry;

public class Chemicals {

    //Table of heat of vaporizations: https://en.wikipedia.org/wiki/Enthalpy_of_vaporization

    public static Chemical Hydrogen;
    public static Chemical Helium;
    public static Chemical Lithium;
    public static Chemical Beryllium;
    public static Chemical Boron;
    public static Chemical Carbon;
    public static Chemical Nitrogen;
    public static Chemical Oxygen;
    public static Chemical Fluorine;
    public static Chemical Neon;
    public static Chemical Sodium;
    public static Chemical Magnesium;
    public static Chemical Aluminium;
    public static Chemical Silicon;
    public static Chemical Phosphorus;
    public static Chemical Sulfur;
    public static Chemical Chlorine;
    public static Chemical Argon;
    public static Chemical Potassium;
    public static Chemical Calcium;
    public static Chemical Scandium;
    public static Chemical Titanium;
    public static Chemical Vanadium;
    public static Chemical Chromium;
    public static Chemical Manganese;
    public static Chemical Iron;
    public static Chemical Cobalt;
    public static Chemical Nickel;
    public static Chemical Copper;
    public static Chemical Zinc;
    public static Chemical Gallium;
    public static Chemical Germanium;
    public static Chemical Arsenic;
    public static Chemical Selenium;
    public static Chemical Bromine;
    public static Chemical Krypton;
    public static Chemical Rubidium;
    public static Chemical Strontium;
    public static Chemical Yttrium;
    public static Chemical Zirconium;
    public static Chemical Niobium;
    public static Chemical Molybdenum;
    public static Chemical Technetium;
    public static Chemical Ruthenium;
    public static Chemical Rhodium;
    public static Chemical Palladium;
    public static Chemical Silver;
    public static Chemical Cadmium;
    public static Chemical Indium;
    public static Chemical Tin;
    public static Chemical Antimony;
    public static Chemical Tellurium;
    public static Chemical Iodine;
    public static Chemical Xenon;
    public static Chemical Caesium;
    public static Chemical Barium;
    public static Chemical Lanthanum;
    public static Chemical Cerium;
    public static Chemical Praseodymium;
    public static Chemical Neodymium;
    public static Chemical Promethium;
    public static Chemical Samarium;
    public static Chemical Europium;
    public static Chemical Gadolinium;
    public static Chemical Terbium;
    public static Chemical Dysprosium;
    public static Chemical Holmium;
    public static Chemical Erbium;
    public static Chemical Thulium;
    public static Chemical Ytterbium;
    public static Chemical Lutetium;
    public static Chemical Hafnium;
    public static Chemical Tantalum;
    public static Chemical Tungsten;
    public static Chemical Rhenium;
    public static Chemical Osmium;
    public static Chemical Iridium;
    public static Chemical Platinum;
    public static Chemical Gold;
    public static Chemical Mercury;
    public static Chemical Thallium;
    public static Chemical Lead;
    public static Chemical Bismuth;
    public static Chemical Polonium;
    public static Chemical Astatine;
    public static Chemical Radon;
    public static Chemical Francium;
    public static Chemical Radium;
    public static Chemical Actinium;
    public static Chemical Thorium;
    public static Chemical Protactinium;
    public static Chemical Uranium;
    public static Chemical Neptunium;
    public static Chemical Plutonium;
    public static Chemical Americium;
    public static Chemical Curium;
    public static Chemical Berkelium;
    public static Chemical Californium;
    public static Chemical Einsteinium;
    public static Chemical Fermium;
    public static Chemical Mendelevium;
    public static Chemical Nobelium;
    public static Chemical Lawrencium;
    public static Chemical Rutherfordium;
    public static Chemical Dubnium;
    public static Chemical Seaborgium;
    public static Chemical Bohrium;
    public static Chemical Hassium;
    public static Chemical Meitnerium;
    public static Chemical Darmstadtium;
    public static Chemical Roentgenium;
    public static Chemical Copernicium;
    public static Chemical Nihonium;
    public static Chemical Flerovium;
    public static Chemical Moscovium;
    public static Chemical Livermorium;
    public static Chemical Tennessine;
    public static Chemical Oganesson;

    public static Chemical Salt;
    public static Chemical Water;
    public static Chemical SulfuricAcid;
    public static Chemical SodiumHydroxide;
    public static Chemical SodiumSulfate;

    public static void register() {
        registerElements();

        Water = new Chemical("Water", Hydrogen, 2, Oxygen, 1)
                .setHeatOfVaporization(40650)
                .setBoilingPoint(373)
                .setMeltingPoint(273)
                .setColor(0x2d6ce0)
                .setDensity(1000)
                .setMolarMass(18.01)
                .setHeatCapacity(75.38)
                .setAcidData(14, 1, 1);

        Salt = new Chemical("Salt", Sodium, 1, Chlorine, 1)
                .addSolubilityInfo(Water, temperature -> 356.5 + 0.0035 * Math.pow(temperature - 273, 2))
                .setMeltingPoint(1074)
                .setDensity(2160)
                .setMolarMass(58.44)
                .setHeatCapacity(51.42)
                .setColor(0xffffff);

        SulfuricAcid = new Chemical("Sulfuric Acid", Hydrogen, 2, Sulfur, 1, Oxygen, 4)
                .addSolubilityInfo(Water, temperature -> 356.5 + 0.0035 * Math.pow(temperature - 273, 2))
                .setMeltingPoint(283)
                .setDensity(1840)
                .setMolarMass(98.08)
                .setColor(0xe0721d)
                .setHeatCapacity(136)
                .setAcidData(-3, 2, 0);

        SodiumHydroxide = new Chemical("Sodium Hydroxide", Sodium, 1, Oxygen, 1, Hydrogen, 1)
                .addSolubilityInfo(Water, temperature -> 970 + 0.2 * Math.pow(temperature - 273, 2))
                .setMeltingPoint(591)
                .setDensity(2130)
                .setMolarMass(40)
                .setColor(0x1f185c)
                .setHeatCapacity(64.43)
                .setAcidData(-3, 0, 1);

        SodiumSulfate = new Chemical("Sodium Sulfate", Sodium, 2, Sulfur, 1, Oxygen, 4)
                .addSolubilityInfo(Water, temperature -> temperature < 308 ? 49 + 0.4 * Math.pow(temperature - 273, 2) : temperature > 308 ? 539 - 2 * (temperature - 308) : 539)
                .setMeltingPoint(1157)
                .setDensity(2660)
                .setMolarMass(142)
                .setHeatCapacity(128.2)
                .setColor(0xe3e89e);
    }

    public static void registerElements() {
        Hydrogen = new Chemical("Hydrogen")
                .setMeltingPoint(14)
                .setBoilingPoint(20.28)
                .setDensity(0.08375)
                .setMolarMass(1);

        Oxygen = new Chemical("Oxygen")
                .setMeltingPoint(54)
                .setBoilingPoint(90)
                .setDensity(1.42)
                .setMolarMass(16);

        Nitrogen = new Chemical("Nitrogen")
                .setMeltingPoint(63)
                .setBoilingPoint(77)
                .setDensity(0.806)
                .setMolarMass(14);

        Argon = new Chemical("Argon")
                .setMeltingPoint(84)
                .setBoilingPoint(87)
                .setDensity(0.535)
                .setMolarMass(40);
    }
}