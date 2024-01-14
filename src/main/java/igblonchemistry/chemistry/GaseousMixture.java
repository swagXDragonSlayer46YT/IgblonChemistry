package igblonchemistry.chemistry;

import igblonchemistry.common.ChemistryConstants;
import igblonchemistry.common.blocks.TileChemicalReactor;

import java.util.ArrayList;
import java.util.Map;

public class GaseousMixture extends Mixture {

    //measured in Pascals
    private double pressure;

    private boolean isVacuum;

    public GaseousMixture(TileChemicalReactor chemicalReactor, Chemical chemical, double amount, double temperature) {
        super(chemicalReactor, chemical, amount, temperature);
    }

    //Rather than removing itself when empty, it will simply be a vacuum
    @Override
    public boolean isEmpty() {
        return false;
    }

    public void checkIfVacuum() {
        if (components.size() == 0) {
            isVacuum = true;
        } else {
            isVacuum = false;
        }
    }

    public boolean getIsVacuum() {
        return isVacuum;
    }

    //Gases will expand to occupy leftover space
    @Override
    public void updateVariables() {
        super.updateVariables();

        if (chemicalReactor != null) {
            totalVolume = chemicalReactor.getReactorVolume() - chemicalReactor.getOccupiedVolume();
        }
    }

    public double getPercentageOfIndividualChemical(Chemical chemical) {
        return (components.get(chemical) * chemical.getMolarMass() / chemical.getDensity()) / totalVolume;
    }

    public double[] getIndividualPressures() {
        double[] pressures = new double[components.size()];
        int i = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            pressures[i] = (entry.getValue() * ChemistryConstants.GAS_CONSTANT * getTemperature()) / getTotalVolume();
            i++;
        }

        return pressures;
    }

    //Measured in Pascals
    public double getPressure() {
        return pressure;
    }

    public void calculateTotalPressures() {
        double[] pressures = getIndividualPressures();
        double tPressure = 0;

        for (double a : pressures) {
            tPressure += a;
        }

        pressure = tPressure;
    }
}
