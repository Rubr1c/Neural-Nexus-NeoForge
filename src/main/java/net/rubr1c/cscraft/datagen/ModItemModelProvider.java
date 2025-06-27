package net.rubr1c.cscraft.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.rubr1c.cscraft.CSCraft;
import net.rubr1c.cscraft.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CSCraft.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.COPPER_WIRE.get());
        basicItem(ModItems.CONTACT_PINS.get());
        basicItem(ModItems.EMPTY_CABLE.get());
        basicItem(ModItems.SILICON_WAFER.get());
        basicItem(ModItems.SMALL_PCB.get());
        basicItem(ModItems.SOLDER_DUST.get());
    }
}
