package igblonchemistry.common.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class ChemistryFluids {

    public static final ChemistryFluid chemical = new ChemistryFluid("chemical", new ResourceLocation("blocks/fluids/chemical"), 0xFFFFFF);

    public static void init() {
        //FluidRegistry.registerFluid(chemical);
    }
}
