package igblonchemistry;

import igblonchemistry.common.CommonProxy;
import igblonchemistry.common.blocks.Machines;
import igblonchemistry.common.items.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = IgblonChemistry.MODID,
        name = IgblonChemistry.MODNAME,
        acceptedMinecraftVersions = "[1.12.2,1.13)",
        version = IgblonChemistry.MODVERSION)

public class IgblonChemistry {

    public static final String MODID = "igblonchemistry";
    public static final String MODNAME = "Igblon Chemistry";
    public static final String MODVERSION = "0.0.1";

    @SidedProxy(clientSide = "igblonchemistry.client.ClientProxy", serverSide = "igblonchemistry.common.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs creativeTab = new CreativeTabs("Chemistry") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Machines.chemicalReactor);
        }
    };

    @Mod.Instance
    public static IgblonChemistry instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {

    }
}
