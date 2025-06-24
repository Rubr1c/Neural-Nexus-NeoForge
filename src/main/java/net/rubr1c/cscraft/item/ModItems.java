package net.rubr1c.cscraft.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubr1c.cscraft.CSCraft;
import net.rubr1c.cscraft.item.custom.GlowingItem;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.function.Supplier;

import static net.rubr1c.cscraft.item.ModCreativeModeTabs.CSCRAFT_TAB_ITEMS;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CSCraft.MODID);

    public static final DeferredItem<Item> EMPTY_CABLE = createItem("empty_cable",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COPPER_WIRE = createItem("copper_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILICON_WAFER = createItem("silicon_wafer",
            () -> new GlowingItem(new Item.Properties()));

    public static final DeferredItem<Item> CONTACT_PINS = createItem("contact_pins",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SMALL_PCB = createItem("small_pcb",
            () -> new Item(new Item.Properties()));


    public static DeferredItem<Item> createItem(String name, Supplier<Item> item) {
        DeferredItem<Item> deferred = ITEMS.register(name, item);
        CSCRAFT_TAB_ITEMS.add(deferred::get);
        return deferred;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
