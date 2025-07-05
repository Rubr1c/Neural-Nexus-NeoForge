package net.rubr1c.neuralnexus.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.rubr1c.neuralnexus.codec.LearnerModelData;
import net.rubr1c.neuralnexus.component.ModDataComponents;

import java.util.List;
import java.util.Objects;

public class LearnerSwordItem extends SwordItem {
    public LearnerSwordItem() {
        super(Tiers.NETHERITE,
                new Properties().attributes(
                        SwordItem.createAttributes(Tiers.NETHERITE,3, -2.4f)
                )
        );
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
}
