package net.rubr1c.neuralnexus.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
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
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.codec.LearnerModelData;
import net.rubr1c.neuralnexus.component.ModDataComponents;
import net.rubr1c.neuralnexus.item.ModItems;
import net.rubr1c.neuralnexus.item.custom.LearnerSwordItem;

import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = NeuralNexus.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        DamageSource src = event.getSource();

        if (src.getEntity() instanceof WitherBoss wither
                && dead instanceof Pig && dead.isBaby()) {

            ItemStack drop = new ItemStack(ModItems.MCSM_THEME_MUSIC_DISC.get());
            dead.spawnAtLocation(drop);
        }

    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide()) return;
        if (level.getServer() == null) return;

        DamageSource src = event.getSource();
        if (!(src.getEntity() instanceof Player player)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof LearnerSwordItem)) return;

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
