package igblonchemistry.proxy;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.client.renderer.Textures;
import igblonchemistry.common.blocks.Blocks;
import igblonchemistry.common.fluids.ChemistryFluids;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        Textures.register();
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Blocks.initModels();
    }

    @SubscribeEvent
    public static void textureLoad(TextureStitchEvent.Pre event) {
        IgblonChemistry.logger.warn("test");
        ChemistryFluids.registerSprites(event.getMap());
    }
}
