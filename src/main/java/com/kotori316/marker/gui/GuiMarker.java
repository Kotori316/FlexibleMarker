package com.kotori316.marker.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.kotori316.marker.Marker;
import com.kotori316.marker.TileFlexMarker;
import com.kotori316.marker.packet.ButtonMessage;
import com.kotori316.marker.packet.PacketHandler;

public class GuiMarker extends ContainerScreen<ContainerMarker> implements IHandleButton {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");
    private static final String[] upSide = {"UP"};
    private static final String[] center = {"Left", "Forward", "Right"};
    private static final String[] downSide = {"Down"};
    private static final int[] amounts = {-16, -1, 1, 16};

    public GuiMarker(ContainerMarker containerMarker, PlayerInventory inv, ITextComponent component) {
        super(containerMarker, inv, component);
        //217, 188
        this.xSize = 217;
        this.ySize = 188;
    }

    @Override
    public void init() {
        super.init();
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
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(LOCATION);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format(TileFlexMarker.Movable.UP.transName);
        font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.FORWARD.transName);
        font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.LEFT.transName);
        font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2 - 40, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.RIGHT.transName);
        font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2 + 40, 6 + 35, 0x404040);
        s = I18n.format(TileFlexMarker.Movable.DOWN.transName);
        font.drawString(s, this.xSize / 2 - this.font.getStringWidth(s) / 2, 6 + 70, 0x404040);
    }

    @Override
    public void actionPerformed(IHandleButton.Button button) {
        TileFlexMarker.Movable movable = TileFlexMarker.Movable.valueOf(button.id / 4);
        ButtonMessage message = new ButtonMessage(container.pos, PacketHandler.getDimId(container.player.world), movable, amounts[button.id % 4]);
        PacketHandler.sendToServer(message);
    }
}
