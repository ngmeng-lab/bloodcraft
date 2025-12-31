package com.suzuran_ss.bloodcraft_ss.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.menu.IronCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IronCauldronScreen
        extends AbstractContainerScreen<IronCauldronMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(bloodcraft.MODID, "textures/gui/ui_1.png");

    public IronCauldronScreen(IronCauldronMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
