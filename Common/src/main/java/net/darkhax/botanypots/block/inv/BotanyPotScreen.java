package net.darkhax.botanypots.block.inv;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.Constants;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BotanyPotScreen extends AbstractContainerScreen<BotanyPotMenu> {

    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final ResourceLocation backgroundTexture;

    public BotanyPotScreen(BotanyPotMenu menu, Inventory playerInv, Component name) {

        super(menu, playerInv, name);
        this.backgroundTexture = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/" + (menu.isHopper() ? "hopper_botany_pot_gui.png" : "botany_pot_gui.png"));
    }

    @Override
    public void init() {

        super.init();

        int recipeOffset = this.leftPos + (this.menu.isHopper() ? 13 : 33);
        this.addRenderableWidget(new ImageButton(recipeOffset, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (btn) -> {}));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.backgroundTexture);
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {

        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, tickDelta);
        this.renderTooltip(poseStack, mouseX, mouseY);

        if (this.hoveredSlot != null) {

            this.font.draw(poseStack, this.hoveredSlot.index + " - " + this.hoveredSlot.getClass().getName(), 20f, 20f, 0xFFFFFFFF);
        }
    }
}
