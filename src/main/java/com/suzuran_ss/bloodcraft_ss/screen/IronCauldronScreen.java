package com.suzuran_ss.bloodcraft_ss.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.menu.IronCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class IronCauldronScreen extends AbstractContainerScreen<IronCauldronMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(bloodcraft.MODID, "textures/gui/ui_1.png");

    // 自定义UI位置，根据你的ui_1.png调整
    private static final int TITLE_LABEL_X = 8;
    private static final int TITLE_LABEL_Y = 6;
    private static final int PLAYER_INVENTORY_LABEL_X = 8;
    private static final int PLAYER_INVENTORY_LABEL_Y = 74;

    public IronCauldronScreen(IronCauldronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // 设置GUI尺寸
        this.imageWidth = 176;  // 标准容器宽度
        this.imageHeight = 166; // 标准容器高度
    }

    @Override
    protected void init() {
        super.init();
        // 移除配方书按钮（如果有的话）
        this.titleLabelX = TITLE_LABEL_X;
        this.titleLabelY = TITLE_LABEL_Y;
        this.inventoryLabelX = PLAYER_INVENTORY_LABEL_X;
        this.inventoryLabelY = PLAYER_INVENTORY_LABEL_Y;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // 渲染UI背景
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 渲染整个UI纹理
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 如果需要渲染进度条或其他动态元素，可以在这里添加
        // 例如：renderProgressArrow(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 渲染标题
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        // 渲染玩家库存标签
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private int getProgress() {
        // 这里可以计算合成进度，返回0-24之间的值
        return 0;
    }

    // 覆盖以禁用配方书相关的点击处理
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        // 标准实现，检查点击是否在GUI外部
        return mouseX < (double)guiLeft
                || mouseY < (double)guiTop
                || mouseX >= (double)(guiLeft + this.imageWidth)
                || mouseY >= (double)(guiTop + this.imageHeight);
    }

    // 确保不会处理配方书相关的点击
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }
}