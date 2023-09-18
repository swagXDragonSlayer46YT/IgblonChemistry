package igblonchemistry.chemistry;

import java.util.ArrayList;

public class ChemicalReactions {

    //Register immiscibility between a chemical and other chemicals
    public static void registerImmiscibility(Chemical chemical, Chemical... chemicals) {
        chemical.registerImmiscibility(chemicals);

        for (Chemical c : chemicals) {
            c.registerImmiscibility(chemical);
        }
    }

    public static void register() {
        //TODO: REGISTER IMMISCIBILITY BETWEEN WATER AND OIL AS AN EXAMPLE
    }
}
