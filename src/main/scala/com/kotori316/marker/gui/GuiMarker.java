package com.kotori316.marker.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.kotori316.marker.Marker;
import com.kotori316.marker.TileFlexMarker;
import com.kotori316.marker.packet.ButtonMessage;
import com.kotori316.marker.packet.PacketHandler;

public class GuiMarker extends GuiContainer implements IHandleButton {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");
    private static final String[] upSide = {"UP"};
    private static final String[] center = {"Left", "Forward", "Right"};
    private static final String[] downSide = {"Down"};
    private static final int[] amounts = {-16, -1, 1, 16};
    private final TileFlexMarker marker;

    public GuiMarker(EntityPlayer player, TileFlexMarker marker) {
        super(new ContainerMarker(player));
        this.marker = marker;
        //217, 188
        this.xSize = 217;
        this.ySize = 188;
    }

    @Override
    public void initGui() {
        super.initGui();
        String[] mp = {"--", "-", "+", "++"};
        int index = 0;
        int w = 10;
        int h = 20;
        int top = 16;

        for (int i = 0; i < upSide.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                addButton(new IHandleButton.Button(index++, guiLeft + xSize / 2 - 4 * w * upSide.length / 2 + i * w * mp.length + w * j, guiTop + top, w, h, mp[j], this));
            }
        }
        for (int i = 0; i < center.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                addButton(new IHandleButton.Button(index++, guiLeft + xSize / 2 - 4 * w * center.length / 2 + i * w * mp.length + w * j, guiTop + top + 35, w, h, mp[j], this));
            }
        }
        for (int i = 0; i < downSide.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                addButton(new IHandleButton.Button(index++, guiLeft + xSize / 2 - 4 * w * downSide.length / 2 + i * w * mp.length + w * j, guiTop + top + 70, w, h, mp[j], this));
            }
        }

    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LOCATION);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format(TileFlexMarker.Movable.UP.transName);
        fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.FORWARD.transName);
        fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.LEFT.transName);
        fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2 - 40, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.RIGHT.transName);
        fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2 + 40, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.DOWN.transName);
        fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6 + 70, 0x404040);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        TileFlexMarker.Movable movable = TileFlexMarker.Movable.valueOf(button.id / 4);
        ButtonMessage message = new ButtonMessage(marker.getPos(), PacketHandler.getDimId(marker.getWorld()), movable, amounts[button.id % 4]);
        PacketHandler.sendToServer(message);
    }
}
