package igblonchemistry.chemistry;

import java.util.ArrayList;
import java.util.HashMap;

public class Compound implements Comparable<Compound> {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    private String name;

    private HashMap<Compound, SolubilityInfo> solubilityInfos = new HashMap<Compound, SolubilityInfo>();

    public Compound(String name) {
        this.name = name;
    }

    public Compound addSolubilityInfo(SolubilityInfo solubilityInfo) {
        this.solubilityInfos.put(solubilityInfo.getSolvent(), solubilityInfo);
        return this;
    }

    public double getSolubility(Compound solvent, double temperature) {
        SolubilityInfo solubilityInfo = solubilityInfos.get(solvent);

        if (solubilityInfo != null) {
            return solubilityInfo.calculateSolubility(temperature);
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Compound compound) {
        return getName().compareTo(compound.getName());
    }

    public String getName() {
        return this.name;
    }
}