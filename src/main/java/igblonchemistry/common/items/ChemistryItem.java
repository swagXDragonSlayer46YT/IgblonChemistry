package igblonchemistry.common.items;

import igblonchemistry.util.HasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import igblonchemistry.IgblonChemistry;

public class ChemistryItem extends Item implements HasModel {

    public ChemistryItem(String name) {
        setCreativeTab(IgblonChemistry.creativeTab);
        setRegistryName(name);
        setTranslationKey(name);

        Items.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        IgblonChemistry.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
