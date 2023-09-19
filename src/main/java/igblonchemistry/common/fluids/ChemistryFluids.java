package igblonchemistry.common.fluids;

import igblonchemistry.IgblonChemistry;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;

public class ChemistryFluids {

    public static ArrayList<ResourceLocation> locationsToRegister = new ArrayList<ResourceLocation>();

    public static final ChemistryFluid CHEMICAL = new ChemistryFluid("chemical_fluid", new ResourceLocation(IgblonChemistry.MODID, "blocks/fluids/fluid"));
    public static final ChemistryFluid DUST = new ChemistryFluid("chemical_dust", new ResourceLocation(IgblonChemistry.MODID, "blocks/fluids/dust"));

    public static void init() {
        FluidRegistry.registerFluid(CHEMICAL);
        FluidRegistry.registerFluid(DUST);
    }

    public static void registerSprites(TextureMap textureMap) {
        for (ResourceLocation location : locationsToRegister) {
            textureMap.registerSprite(location);
        }
    }
}
