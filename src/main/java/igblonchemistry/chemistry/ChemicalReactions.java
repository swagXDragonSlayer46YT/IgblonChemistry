package igblonchemistry.chemistry;

import java.util.ArrayList;

public class ChemicalReactions {

    public static ArrayList<ChemicalReaction> chemicalReactions = new ArrayList<ChemicalReaction>();

    //Register immiscibility between a chemical and other chemicals
    public static void registerImmiscibility(Chemical chemical, Chemical... chemicals) {
        chemical.registerImmiscibility(chemicals);

        for (Chemical c : chemicals) {
            c.registerImmiscibility(chemical);
        }
    }

    public static void register() {
        //TODO: REGISTER IMMISCIBILITY BETWEEN WATER AND OIL AS AN EXAMPLE

        new ChemicalReaction()
                .addReactant(Chemicals.SodiumHydroxide, 2)
                .addReactant(Chemicals.SulfuricAcid, 1)
                .addProduct(Chemicals.Water, 2)
                .addProduct(Chemicals.SodiumSulfate, 1)
                .setEnthalpyChange(-68600);
    }
}
