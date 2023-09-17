package igblonchemistry.chemistry;

@FunctionalInterface
interface SolubilityInfo {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    //Measured in grams per 1 liter.
    //Meant to be overridden for each new solubility info.
    //Temperature is measured in Kelvin.
    //Pressure is assumed to be 1 atmosphere.
    double calculateSolubility(double temperature);
}
