package net.rubr1c.neuralnexus.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.codec.LearnerModelData;
import net.rubr1c.neuralnexus.component.ModDataComponents;
import net.rubr1c.neuralnexus.item.ModItems;
import net.rubr1c.neuralnexus.item.custom.LearnerModelItem;
import net.rubr1c.neuralnexus.item.custom.LearnerSwordItem;
import net.rubr1c.neuralnexus.screen.custom.LearnerSwordMenu;

import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = NeuralNexus.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onWitherKillBabyPig(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        DamageSource src = event.getSource();

        if (src.getEntity() instanceof WitherBoss wither
                && dead instanceof Pig && dead.isBaby()) {

            ItemStack drop = new ItemStack(ModItems.MCSM_THEME_MUSIC_DISC.get());
            dead.spawnAtLocation(drop);
        }
    }

    @SubscribeEvent
    public static void onKillWithLearnerSword(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        DamageSource src = event.getSource();

        if (src.getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof LearnerSwordItem) {
                Boolean isLearning = stack.get(ModDataComponents.BOOL);
                if (isLearning != null && isLearning) {
                    var comp = stack.get(ModDataComponents.NBT);
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
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickWithLearnerSword(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof LearnerSwordItem) {
            Boolean state = stack.get(ModDataComponents.BOOL);

            Player player = event.getEntity();
            if (state == null) {
                stack.set(ModDataComponents.BOOL, true);
                player.displayClientMessage(Component.literal("Simulating"), true);

            } else {
                stack.set(ModDataComponents.BOOL, !state);
                player.displayClientMessage(Component.literal(state ? "Simulating" : "Learning"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onDropsKilledByLearnerSword(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide()) return;
        if (level.getServer() == null) return;

        DamageSource src = event.getSource();
        if (!(src.getEntity() instanceof Player player)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof LearnerSwordItem)) return;

        Boolean isLearning = stack.get(ModDataComponents.BOOL);
        if (isLearning != null && isLearning) return;

        LearnerModelData data = stack.get(ModDataComponents.MODEL);
        if (data == null) return;

        Random random = new Random();

        if (random.nextInt(100) > data.accuracy()) {
            return;
        }

        var target = BuiltInRegistries.ENTITY_TYPE.get(data.entity());
        var lootTable = target.getDefaultLootTable();
        LootTable drops = level.getServer().reloadableRegistries().getLootTable(lootTable);

        LivingEntity entity = event.getEntity();
        event.getDrops().clear();

        LootParams.Builder builder = new LootParams.Builder((ServerLevel)level)
                .withParameter(LootContextParams.THIS_ENTITY, event.getEntity())
                .withParameter(LootContextParams.ORIGIN, event.getEntity().position());
        LootParams params = builder.create(LootContextParamSets.EMPTY);

        List<ItemStack> rolls = drops.getRandomItems(params);
        for (ItemStack drop : rolls) {
            event.getDrops().add(new ItemEntity(
                    level,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    drop
            ));
        }

    }


}
