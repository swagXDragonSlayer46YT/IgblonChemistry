package igblonchemistry.chemistry;

public class SolubilityInfo {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    private Compound solvent;

    public SolubilityInfo(Compound solvent) {
        this.solvent = solvent;
    }

    //Measured in grams per 1 liter.
    //Meant to be overridden for each new solubility info.
    //Temperature is measured in Celsius.
    //Pressure is assumed to be 1 atmosphere.
    public double calculateSolubility(double temperature) {
        return 0;
    }
}
