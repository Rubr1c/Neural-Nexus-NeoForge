package net.rubr1c.neuralnexus.item.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.rubr1c.neuralnexus.component.ModDataComponents;

import java.util.List;

public class LearnerModelItem extends Item {


    public LearnerModelItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack,
                                                  Player player,
                                                  LivingEntity interactionTarget,
                                                  InteractionHand usedHand) {

        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(interactionTarget.getType());
        ItemStack newStack = stack.copy();
        newStack.set(ModDataComponents.LIVING_ENTITY,
                ResourceLocation.parse(id.toString()));
        newStack.set(ModDataComponents.PERCENT, 0);
        player.setItemInHand(usedHand, newStack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {

        var entity = stack.get(ModDataComponents.LIVING_ENTITY);
        var accuracy = stack.get(ModDataComponents.PERCENT);
        if (entity != null) {
            tooltipComponents.add(Component.literal("Type: " + entity.getPath()));
            tooltipComponents.add(Component.literal("Accuracy: " + accuracy));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}
