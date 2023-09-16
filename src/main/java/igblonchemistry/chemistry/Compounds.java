package igblonchemistry.chemistry;

public class Compounds {
    public static Compound Salt;
    public static Compound Water;

    public static void register() {
        Water = new Compound("water");

        Salt = new Compound("salt")
                .addSolubilityInfo(new SolubilityInfo(Water) {
                    public double calculateSolubility(double temperature) {
                        return 356.5 + 0.0035 * temperature * temperature;
                    }
                });
    }
}