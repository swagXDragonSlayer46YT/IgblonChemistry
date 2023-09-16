package igblonchemistry.common.blocks;

import igblonchemistry.IgblonChemistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class ChemicalReactor extends Block {

    public static final ResourceLocation CHEMICAL_REACTOR = new ResourceLocation(IgblonChemistry.MODID, "chemical_reactor");
    public ChemicalReactor() {
        super(Material.IRON);
        setRegistryName(CHEMICAL_REACTOR);
        setTranslationKey(IgblonChemistry.MODID + ".chemical_reactor");
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(IgblonChemistry.creativeTab);
        Machines.BLOCKS.add(this);
    }
}
