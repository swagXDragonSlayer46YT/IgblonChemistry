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

    @Override
    public void update() {

        checkIfVacuum();

        updateVariables();

        containedChemicals = new ArrayList<>(components.keySet());

        calculateTotalVolume();
        calculateTotalPressures();
        runPossibleReactions();

        cleanComponentsList();
    }

    @Override
    public void updateVariables() {
        double totalHeatCapacity = 0;
        double totalMoles = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            totalHeatCapacity += entry.getKey().getHeatCapacity() * entry.getValue();
            totalMoles += entry.getValue();
        }

        setTotalMols(totalMoles);
        setAverageHeatCapacity(totalHeatCapacity / totalMoles);
        setTemperature(293);
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

    //Returns a list of percentages of gases by volume
    @Override
    public double[] getIndividualVolumes() {
        double[] volumes = new double[components.size()];
        int i = 0;

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            volumes[i] = entry.getValue() / getTotalMols();
            i++;
        }

        return volumes;
    }

    //Gases will expand to occupy leftover space
    @Override
    public void calculateTotalVolume() {
        setTotalVolume(chemicalReactor.getReactorVolume() - chemicalReactor.getOccupiedVolume());
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
