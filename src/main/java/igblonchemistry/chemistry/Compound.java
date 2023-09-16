package igblonchemistry.chemistry;

import java.util.ArrayList;

public class Compound {
    //SOLUBILITIES TAKEN FROM: https://en.wikipedia.org/wiki/Solubility_table

    private String name;

    private ArrayList<SolubilityInfo> solubilityInfo = new ArrayList<SolubilityInfo>();

    public Compound(String name) {
        this.name = name;
    }

    public Compound addSolubilityInfo(SolubilityInfo solubilityInfo) {
        this.solubilityInfo.add(solubilityInfo);
        return this;
    }
}
