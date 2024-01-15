package igblonchemistry.chemistry;

import igblonchemistry.common.ChemistryConstants;
import igblonchemistry.common.blocks.TileChemicalReactor;
import scala.Console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

            pressure = (totalMols * ChemistryConstants.GAS_CONSTANT * temperature) / totalVolume;
        }
    }

    //Measured in Pascals
    public double getPressure() {
        return pressure;
    }

    public double getPressureOfIndividualChemical(Chemical chemical) {
        return (components.get(chemical) * ChemistryConstants.GAS_CONSTANT * temperature) / totalVolume;
    }

    public void doGaseousSeparations(Mixture mixtureBelow, double separationSpeed) {
        Random r = new Random();

        for (Map.Entry<Chemical, Double> entry : components.entrySet()) {
            //Simulate absorption

            HashMap<Chemical, SolubilityInfo> solubilityInfos = entry.getKey().getSolubilityInfos();

            if (!solubilityInfos.isEmpty()) {
                double solubilityTotal = 0;

                for (Map.Entry<Chemical, SolubilityInfo> entry2 : solubilityInfos.entrySet()) {
                    for (Map.Entry<Chemical, Double> entry3 : mixtureBelow.getComponents().entrySet()) {
                        if (entry3.getKey().compareTo(entry2.getKey()) == 0 && entry.getKey().compareTo(entry2.getKey()) != 0) {
                            solubilityTotal += entry2.getValue().calculateSolubility(temperature) * getVolumeOfIndividualChemical(entry3.getKey()) / entry.getKey().getMolarMass();
                        }
                    }
                }

                if (solubilityTotal > 0) {
                    mixtureBelow.moveChemical(this, entry.getKey(), Math.min(entry.getValue(), separationSpeed + r.nextDouble()));
                }
            }

            if (entry.getKey().calculateVaporPressure(temperature) < pressure) {
                mixtureBelow.moveChemical(this, entry.getKey(), Math.min(entry.getValue(), separationSpeed + r.nextDouble()));
            }
        }

        updateVariables();
    }
}
