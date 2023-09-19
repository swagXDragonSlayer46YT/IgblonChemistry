package igblonchemistry.common;

import igblonchemistry.chemistry.Chemical;
import igblonchemistry.chemistry.Chemicals;

import java.util.HashMap;

public class ChemistryConstants {
    public static double GAS_CONSTANT = 8.31446261815324;

    //In Pascals
    public static double ATMOSPHERIC_PRESSURE = 101325;

    //Composition of Earth's atmosphere, percentage
    public static HashMap<Chemical, Double> EARTH_ATMOSPHERE_COMPOSITION = new HashMap<Chemical, Double>();

    public static void register() {
        EARTH_ATMOSPHERE_COMPOSITION.put(Chemicals.Nitrogen, 0.78);
        EARTH_ATMOSPHERE_COMPOSITION.put(Chemicals.Oxygen, 0.22);
    }
}
