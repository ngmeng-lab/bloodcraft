package com.suzuran_ss.bloodcraft_ss.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.menu.IronCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IronCauldronScreen extends AbstractContainerScreen<IronCauldronMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(bloodcraft.MODID, "textures/gui/ui_1.png");

    public IronCauldronScreen(IronCauldronMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // 设置GUI尺寸
        this.imageWidth = 176;  // 标准容器宽度
        this.imageHeight = 166; // 标准容器高度
    }

    @Override
    protected void init() {
        super.init();
        // 移除配方书按钮
        // 不调用任何配方书初始化代码
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

        // 这里可以根据合成进度渲染其他元素
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
}