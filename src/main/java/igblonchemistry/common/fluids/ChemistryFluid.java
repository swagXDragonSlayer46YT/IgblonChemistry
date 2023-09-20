package igblonchemistry.common.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;

public class ChemistryFluid extends Fluid {

    public ChemistryFluid(String fluidName, ResourceLocation still) {
        super(fluidName, still, still);
    }
}
