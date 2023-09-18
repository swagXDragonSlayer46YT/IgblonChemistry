package igblonchemistry.chemistry;

import igblonchemistry.common.blocks.TileChemicalReactor;

public class GaseousMixture extends Mixture {

    private double pressure;

    public GaseousMixture(TileChemicalReactor chemicalReactor, Chemical chemical, double amount) {
        super(chemicalReactor, chemical, amount);
    }
}
