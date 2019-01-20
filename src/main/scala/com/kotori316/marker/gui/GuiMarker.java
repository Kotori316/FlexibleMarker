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
        //217, 188
        this.xSize = 217;
        this.ySize = 188;
    }

    @Override
    public void initGui() {
        super.initGui();
        String[] mp = {"--", "-", "+", "++"};
        String[] upSide = {"UP"};
        String[] center = {"Left", "Forward", "Right"};
        String[] downSide = {"Down"};
        int index = 0;
        int w = 10;
        int h = 20;

        for (int i = 0; i < upSide.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * upSide.length / 2 + i * w * mp.length + w * j, guiTop + 8, w, h, mp[j]));
            }
        }
        for (int i = 0; i < center.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * center.length / 2 + i * w * mp.length + w * j, guiTop + 43, w, h, mp[j]));
            }
        }
        for (int i = 0; i < downSide.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * downSide.length / 2 + i * w * mp.length + w * j, guiTop + 78, w, h, mp[j]));
            }
        }

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
