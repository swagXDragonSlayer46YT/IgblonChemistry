package igblonchemistry.client.renderer;

import igblonchemistry.IgblonChemistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Textures {

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite CHEMICAL;

    @SideOnly(Side.CLIENT)
    public static void register() {
        Minecraft mc = Minecraft.getMinecraft();
        TextureMap textureMap = mc.getTextureMapBlocks();

        CHEMICAL = textureMap.registerSprite(new ResourceLocation(IgblonChemistry.MODID, "blocks/fluids/ttt"));
    }
}
