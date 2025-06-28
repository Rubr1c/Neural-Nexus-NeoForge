package net.rubr1c.neuralnexus.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubr1c.neuralnexus.NeuralNexus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NeuralNexus.MODID);

    public static final List<Supplier<? extends ItemLike>> CSCRAFT_TAB_ITEMS = new ArrayList<>();

    public static final Supplier<CreativeModeTab> CSCRAFT_TAB = CREATIVE_MODE_TABS.register("neural_nexus",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SMALL_PCB.get()))
                    .title(Component.translatable("creativetab." + NeuralNexus.MODID + ".neural_nexus"))
                    .displayItems((params, output) -> {
                        for (Supplier<? extends ItemLike> sup : CSCRAFT_TAB_ITEMS) {
                            output.accept(sup.get());
                        }
                    })
                    .build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
