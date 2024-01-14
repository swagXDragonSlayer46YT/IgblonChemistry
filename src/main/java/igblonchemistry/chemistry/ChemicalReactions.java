package igblonchemistry.chemistry;

import java.util.ArrayList;

public class ChemicalReactions {

    public static ArrayList<ChemicalReaction> chemicalReactions = new ArrayList<ChemicalReaction>();

    //Register miscibility between a chemical and other chemicals
    public static void registerMiscibility(Chemical chemical, Chemical... chemicals) {
        chemical.registerMiscibility(chemicals);

        for (Chemical c : chemicals) {
            c.registerMiscibility(chemical);
        }
    }

    public static void register() {
        registerMiscibility(Chemicals.Water, Chemicals.FormicAcid, Chemicals.SulfuricAcid);

        new ChemicalReaction()
                .addReactant(Chemicals.SodiumHydroxide, 2)
                .addReactant(Chemicals.SulfuricAcid, 1)
                .addProduct(Chemicals.Water, 2)
                .addProduct(Chemicals.SodiumSulfate, 1)
                .setChemicalReactionType(ChemicalReactionTypes.INSTANT)
                .setEnthalpyChange(-68600);

        new ChemicalReaction()
                .addReactant(Chemicals.Hydrogen, 1)
                .addReactant(Chemicals.Chlorine, 1)
                .addProduct(Chemicals.HydrogenChloride, 1)
                .setChemicalReactionType(ChemicalReactionTypes.INSTANT)
                .setEnthalpyChange(-92300);
    }
}
