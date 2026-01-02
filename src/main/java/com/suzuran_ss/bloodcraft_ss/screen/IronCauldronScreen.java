package com.suzuran_ss.bloodcraft_ss.screen;

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

        // 原版容器尺寸
        this.imageWidth = 176;
        this.imageHeight = 166;

        // 标签位置，和原版一致
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // 自动居中，原版逻辑
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 原版渲染背景
        guiGraphics.blit(
                TEXTURE,
                this.leftPos,
                this.topPos,
                0, 0,
                this.imageWidth,
                this.imageHeight
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 原版渲染文字
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
