package net.rubr1c.neuralnexus.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.item.ModItems;

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
}
