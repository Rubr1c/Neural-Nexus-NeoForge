package net.rubr1c.neuralnexus.screen.custom;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.rubr1c.neuralnexus.NeuralNexus;
import net.rubr1c.neuralnexus.screen.custom.LearnerSwordMenu;

public class LearnerSwordScreen extends AbstractContainerScreen<LearnerSwordMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeuralNexus.MODID, "textures/gui/learner_sword/learner_sword_gui.png");

    public LearnerSwordScreen(LearnerSwordMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 175;
        this.imageHeight = 128;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width  - imageWidth)  / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(
                TEXTURE,
                x, y,
                0, 0,
                imageWidth,
                imageHeight
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);

    }
}

