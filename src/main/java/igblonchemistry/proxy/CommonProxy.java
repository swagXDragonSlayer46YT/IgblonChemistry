package igblonchemistry.proxy;

import igblonchemistry.IgblonChemistry;
import igblonchemistry.client.renderer.Textures;
import igblonchemistry.common.GuiHandler;
import igblonchemistry.common.blocks.ChemicalReactor;
import igblonchemistry.common.blocks.Blocks;
import igblonchemistry.common.blocks.TileChemicalReactor;
import igblonchemistry.common.fluids.ChemistryFluids;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ChemistryFluids.init();
    }

    public void init(FMLInitializationEvent event) {
        Textures.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(IgblonChemistry.instance, new GuiHandler());
    }

    public void postInit(FMLInitializationEvent event) {

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new ChemicalReactor());
        GameRegistry.registerTileEntity(TileChemicalReactor.class, IgblonChemistry.MODID + "_chemical_reactor");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(Blocks.chemicalReactor).setRegistryName(Blocks.chemicalReactor.CHEMICAL_REACTOR));
    }

    public void registerItemRenderer(Item item, int meta, String id) {

    };
}
