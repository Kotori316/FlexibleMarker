package com.kotori316.marker.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.kotori316.marker.Marker;

public class GuiMarker extends GuiContainer {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");

    public GuiMarker(EntityPlayer player) {
        super(new ContainerMarker(player));
    }

    @Override
    public void initGui() {
        super.initGui();
        String[] mp = {"-", "+"};
        int index = 0;
        for (int i = 0; i < 4; i++) {
            buttonList.add(new GuiButton(index, guiLeft + 17 + 35 * i, guiTop + 20, 10, 20, mp[index++ % 2]));
            buttonList.add(new GuiButton(index, guiLeft + 42 + 35 * i, guiTop + 20, 10, 20, mp[index++ % 2]));
        }
        for (int i = 0; i < 2; i++) {
            buttonList.add(new GuiButton(index, guiLeft + 52 + 35 * i, guiTop + 45, 10, 20, mp[index++ % 2]));
            buttonList.add(new GuiButton(index, guiLeft + 77 + 35 * i, guiTop + 45, 10, 20, mp[index++ % 2]));
        }
        assert index >= 0; // dummy use
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LOCATION);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }
}
