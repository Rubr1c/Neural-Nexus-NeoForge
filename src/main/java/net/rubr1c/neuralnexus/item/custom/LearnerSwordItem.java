package net.rubr1c.neuralnexus.item.custom;

import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.rubr1c.neuralnexus.codec.LearnerModelData;
import net.rubr1c.neuralnexus.component.ModDataComponents;
import net.rubr1c.neuralnexus.item.ModItems;
import net.rubr1c.neuralnexus.screen.custom.LearnerSwordMenu;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LearnerSwordItem extends SwordItem {
    public static final String NBT_KEY = "LearnerSwordInv";

    public LearnerSwordItem() {
        super(Tiers.NETHERITE,
                new Properties().attributes(
                        SwordItem.createAttributes(Tiers.NETHERITE, 3, -2.4f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer) {
            if (player.isShiftKeyDown()) {


                var active = stack.get(ModDataComponents.MODEL);
                var comp = stack.get(ModDataComponents.NBT);
                if (comp == null)
                    return InteractionResultHolder.pass(stack);
                LearnerModelData next = getNextModelData(player, comp, active);
                if (next == null)
                    return InteractionResultHolder.fail(stack);
                stack.set(ModDataComponents.MODEL, next);
                player.displayClientMessage(Component.literal(next.entity().getPath()), true);
            } else {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> new LearnerSwordMenu(containerId, playerInventory,
                                new FriendlyByteBuf(Unpooled.buffer())),
                        Component.literal("Learner Sword")));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }



    @Override
    public void appendHoverText(ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        var curr = stack.get(ModDataComponents.MODEL);
        if (curr != null) {
            tooltipComponents.add(Component.literal(curr.entity().getPath()));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void saveContents(Player player, ItemStackHandler handler) {
        if (!player.level().isClientSide()) {
            ItemStack held = player.getMainHandItem();
            CompoundTag comp = held.get(ModDataComponents.NBT);

            HolderLookup.Provider provider = player.level().registryAccess();

            if (comp != null) {
                comp.put(LearnerSwordItem.NBT_KEY, handler.serializeNBT(provider));
                held.set(ModDataComponents.NBT, comp);
            }
        }
    }

    public static void loadContents(Player player, ItemStackHandler handler) {
        ItemStack held = player.getMainHandItem();
        var comp = held.get(ModDataComponents.NBT);
        HolderLookup.Provider provider = player.level().registryAccess();
        if (comp != null) {
            CompoundTag swordInv = comp.getCompound(LearnerSwordItem.NBT_KEY);
            handler.deserializeNBT(provider, swordInv);
        } else {
            held.set(ModDataComponents.NBT, new CompoundTag());
        }
    }

    public static void handleLearn(Player player,
                                   ItemStack stack,
                                   LivingEntity dead) {
        CompoundTag comp = stack.get(ModDataComponents.NBT);
        if (comp == null) return;

        HolderLookup.Provider provider = player.level().registryAccess();
        ItemStackHandler handler = new ItemStackHandler(9);

        CompoundTag swordInv = comp.getCompound(LearnerSwordItem.NBT_KEY);
        handler.deserializeNBT(provider, swordInv);


        for (int i = 0; i < 9; i++) {
            ItemStack curr = handler.getStackInSlot(i);
            if (!(curr.getItem() instanceof LearnerModelItem)) continue;   // skip empties

            ResourceLocation entity = curr.get(ModDataComponents.LIVING_ENTITY);
            if (entity == null) continue;

            if (dead.getType().equals(BuiltInRegistries.ENTITY_TYPE.get(entity))) {
                int acc = curr.getOrDefault(ModDataComponents.PERCENT, 0);
                ItemStack updated = curr.copy();
                updated.set(ModDataComponents.PERCENT, Math.min(acc + 1, 100));
                handler.setStackInSlot(i, updated);

                comp.put(LearnerSwordItem.NBT_KEY, handler.serializeNBT(provider));
                stack.set(ModDataComponents.NBT, comp);
                break;
            }

        }
    }

    public static void toggleMode(Player player, ItemStack stack) {
        Boolean state = stack.get(ModDataComponents.BOOL);

        if (state == null) {
            stack.set(ModDataComponents.BOOL, true);
            player.displayClientMessage(Component.literal("Simulating"), true);

        } else {
            stack.set(ModDataComponents.BOOL, !state);
            player.displayClientMessage(Component.literal(state ? "Simulating" : "Learning"), true);
        }
    }

    public static void handleDrops(Level level,
                                   ItemStack stack,
                                   LivingEntity killed,
                                   Collection<ItemEntity> killedDrop) {
        LearnerModelData data = stack.get(ModDataComponents.MODEL);
        if (data == null) return;

        Random random = new Random();

        if (random.nextInt(100) > data.accuracy()) {
            return;
        }

        EntityType<?> target = BuiltInRegistries.ENTITY_TYPE.get(data.entity());
        ResourceKey<LootTable> lootTable = target.getDefaultLootTable();

        MinecraftServer server = level.getServer();
        if (server == null) return;

        LootTable drops = server.reloadableRegistries().getLootTable(lootTable);

        killedDrop.clear();

        LootParams.Builder builder = new LootParams.Builder((ServerLevel)level)
                .withParameter(LootContextParams.THIS_ENTITY, killed)
                .withParameter(LootContextParams.ORIGIN, killed.position());
        LootParams params = builder.create(LootContextParamSets.EMPTY);

        List<ItemStack> rolls = drops.getRandomItems(params);
        for (ItemStack drop : rolls) {
            killedDrop.add(new ItemEntity(
                    level,
                    killed.getX(),
                    killed.getY(),
                    killed.getZ(),
                    drop
            ));
        }
    }

    private LearnerModelData getNextModelData(Player player,
            CompoundTag comp,
            LearnerModelData prev) {

        HolderLookup.Provider provider = player.level().registryAccess();
        ItemStackHandler handler = new ItemStackHandler(9);

        CompoundTag swordInv = comp.getCompound(NBT_KEY);
        handler.deserializeNBT(provider, swordInv);
        int slotIndex;

        if (prev == null) {
            slotIndex = getNextItem(handler, 8);
        } else {
            slotIndex = getNextItem(handler, prev.slot());
            if (slotIndex == prev.slot())
                return null; // no other model found
        }

        ItemStack item = handler.getStackInSlot(slotIndex);
        return extractModelData(item, slotIndex);
    }

    private int getNextItem(ItemStackHandler handler, int slot) {
        for (int offset = 1; offset <= 8; offset++) {
            int i = (slot + offset) % 9;
            if (handler.getStackInSlot(i).getItem() instanceof LearnerModelItem) {
                return i;
            }
        }
        return slot;
    }

    private LearnerModelData extractModelData(ItemStack stack, int slot) {
        if (stack.is(ModItems.LEARNER_MODEL)) {
            Integer acc = stack.get(ModDataComponents.PERCENT);
            ResourceLocation entity = stack.get(ModDataComponents.LIVING_ENTITY);
            if (entity == null)
                return null;

            return new LearnerModelData(
                    acc == null ? 0 : acc,
                    entity,
                    slot);
        } else {
            return null;
        }
    }
}
