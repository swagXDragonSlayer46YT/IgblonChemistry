package igblonchemistry.client.renderer;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.common.fluids.ChemistryFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class Textures {

    public static ArrayList<ResourceLocation> locationsToRegister = new ArrayList<ResourceLocation>();

    public static void registerSprites(TextureMap textureMap) {
        locationsToRegister.add(new ResourceLocation(IgblonChemistry.MODID, "blocks/fluids/fluid"));
        locationsToRegister.add(new ResourceLocation(IgblonChemistry.MODID, "blocks/fluids/dust"));

        for (ResourceLocation location : locationsToRegister) {
            textureMap.registerSprite(location);
        }
    }
}
