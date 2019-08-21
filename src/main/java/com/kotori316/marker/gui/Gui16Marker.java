package com.kotori316.marker.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.kotori316.marker.Marker;
import com.kotori316.marker.Tile16Marker;
import com.kotori316.marker.packet.Button16Message;
import com.kotori316.marker.packet.PacketHandler;

public class Gui16Marker extends ContainerScreen<ContainerMarker> implements IHandleButton {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");
    private final Tile16Marker marker;

    public Gui16Marker(ContainerMarker containerMarker, PlayerInventory inv, ITextComponent component) {
        super(containerMarker, inv, component);
        this.marker = ((Tile16Marker) inv.player.getEntityWorld().getTileEntity(containerMarker.pos));
        //217, 188
        this.xSize = 217;
        this.ySize = 188;
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(LOCATION);
        this.blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        this.font.drawString(Integer.toString(marker.getSize() / 16), this.xSize / 2 - this.font.getStringWidth(Integer.toString(marker.getSize() / 16)) / 2, 15 + 23, 0x404040);
    }

    @Override
    public void init() {
        super.init();
        int width = 40;
        int tp = 15;
        this.addButton(new IHandleButton.Button(0, guiLeft + this.xSize / 2 - width / 2, guiTop + tp, width, 20, "+", this));
        this.addButton(new IHandleButton.Button(1, guiLeft + this.xSize / 2 - width / 2, guiTop + tp + 33, width, 20, "-", this));
    }

    @Override
    public void actionPerformed(Button button) {
        int size;
        if (button.id % 2 == 0) {
            size = marker.getSize() + 16;
        } else {
            if (marker.getSize() > 16) {
                size = marker.getSize() - 16;
            } else {
                size = marker.getSize();
            }
        }
        PacketHandler.sendToServer(new Button16Message(marker.getPos(), PacketHandler.getDimId(marker.getWorld()), size));
    }
}
