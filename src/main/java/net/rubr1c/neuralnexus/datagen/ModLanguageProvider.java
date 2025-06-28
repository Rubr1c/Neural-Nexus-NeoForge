package net.rubr1c.neuralnexus.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.block.ModBlocks;
import net.rubr1c.neuralnexus.item.ModCreativeModeTabs;
import net.rubr1c.neuralnexus.item.ModItems;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, NeuralNexus.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var item: ModItems.ITEMS.getEntries()) {
            if (item.get() instanceof BlockItem) continue;
            addItem(item, getNameFromHolder(item));
        }

        for (var block: ModBlocks.BLOCKS.getEntries()) {
            addBlock(block, getNameFromHolder(block));
        }

        for (var cmt: ModCreativeModeTabs.CREATIVE_MODE_TABS.getEntries()) {
            addCreativeTab(cmt, getNameFromHolder(cmt));
        }
    }

    protected void addCreativeTab(DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> tab, String name) {
        add("creativetab." + NeuralNexus.MODID + "." + tab.getId().getPath(), name);
    }

    private String getNameFromHolder(DeferredHolder<?, ?> holder) {
        String key = holder.getId().getPath();
        if (key == null || key.isEmpty()) {
            return key;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = key.split("_");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
            if (i < parts.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }
}
