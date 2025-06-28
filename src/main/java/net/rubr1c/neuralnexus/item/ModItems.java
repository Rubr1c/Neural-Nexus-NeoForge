package net.rubr1c.neuralnexus.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.item.custom.GlowingItem;

import java.util.function.Supplier;

import static net.rubr1c.neuralnexus.item.ModCreativeModeTabs.CSCRAFT_TAB_ITEMS;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeuralNexus.MODID);

    public static final DeferredItem<Item> EMPTY_CABLE = registerItem("empty_cable",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COPPER_WIRE = registerItem("copper_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILICON_WAFER = registerItem("silicon_wafer",
            () -> new GlowingItem(new Item.Properties()));

    public static final DeferredItem<Item> CONTACT_PINS = registerItem("contact_pins",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SMALL_PCB = registerItem("small_pcb",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SOLDER_DUST = registerItem("solder_dust",
            () -> new Item(new Item.Properties()));


    public static DeferredItem<Item> registerItem(String name, Supplier<Item> item) {
        DeferredItem<Item> deferred = ITEMS.register(name, item);
        CSCRAFT_TAB_ITEMS.add(deferred::get);
        return deferred;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
