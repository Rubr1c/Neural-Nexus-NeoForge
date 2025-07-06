package net.rubr1c.neuralnexus.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.component.ModDataComponents;
import net.rubr1c.neuralnexus.item.ModItems;
import net.rubr1c.neuralnexus.item.custom.LearnerSwordItem;

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
                    LearnerSwordItem.handleLearn(player, stack, dead);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickWithLearnerSword(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();

        if (stack.getItem() instanceof LearnerSwordItem) {
            LearnerSwordItem.toggleMode(player, stack);
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

        LearnerSwordItem.handleDrops(level, stack, event.getEntity(), event.getDrops());

    }


}
