package net.rubr1c.cscraft.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubr1c.cscraft.CSCraft;
import net.rubr1c.cscraft.item.custom.GlowingItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CSCraft.MODID);

    public static final DeferredItem<Item> EMPTY_CABLE = ITEMS.register("empty_cable",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILICON_WAFER = ITEMS.register("silicon_wafer",
            () -> new GlowingItem(new Item.Properties()));

    public static final DeferredItem<Item> CONTACT_PINS = ITEMS.register("contact_pins",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SMALL_PCB = ITEMS.register("small_pcb",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
