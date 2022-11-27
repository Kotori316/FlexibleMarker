package com.kotori316.marker.gui;

import java.io.IOException;

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

public class GuiMarker extends GuiContainer {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");
    private static final String[] upSide = {"UP"};
    private static final String[] center = {"Left", "Forward", "Right"};
    private static final String[] downSide = {"Down"};
    private static final int[] amounts = {-16, -1, 1, 16};
    private final TileFlexMarker marker;
    private static final int yOffsetCenter = 45;
    private static final int yOffsetBottom = 90;

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
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * upSide.length / 2 + i * w * mp.length + w * j, guiTop + top, w, h, mp[j]));
            }
        }
        for (int i = 0; i < center.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * center.length / 2 + i * w * mp.length + w * j, guiTop + top + 35, w, h, mp[j]));
            }
        }
        for (int i = 0; i < downSide.length; i++) {
            for (int j = 0; j < mp.length; j++) {
                buttonList.add(new GuiButton(index++, guiLeft + xSize / 2 - 4 * w * downSide.length / 2 + i * w * mp.length + w * j, guiTop + top + 70, w, h, mp[j]));
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

        /*BlockPos minPos = this.marker.min();
        BlockPos maxPos = this.marker.max();
        String start = String.format("(%d, %d, %d)", minPos.getX(), minPos.getY(), minPos.getZ());
        String end = String.format("(%d, %d, %d)", maxPos.getX(), maxPos.getY(), maxPos.getZ());
        int x = this.xSize - Math.max(fontRenderer.getStringWidth(start), fontRenderer.getStringWidth(end)) - 10;
        fontRenderer.drawString(start, x, 6 + yOffsetBottom + 5, 0x404040);
        fontRenderer.drawString(end, x, 6 + yOffsetBottom + 15, 0x404040);

        String distanceUp = String.valueOf(TileFlexMarker.Movable.UP.distanceFromOrigin(marker.getPos(), minPos, maxPos, marker.direction));
        String distanceLeft = String.valueOf(TileFlexMarker.Movable.LEFT.distanceFromOrigin(marker.getPos(), minPos, maxPos, marker.direction));
        String distanceForward = String.valueOf(TileFlexMarker.Movable.FORWARD.distanceFromOrigin(marker.getPos(), minPos, maxPos, marker.direction));
        String distanceRight = String.valueOf(TileFlexMarker.Movable.RIGHT.distanceFromOrigin(marker.getPos(), minPos, maxPos, marker.direction));
        String distanceDown = String.valueOf(TileFlexMarker.Movable.DOWN.distanceFromOrigin(marker.getPos(), minPos, maxPos, marker.direction));
        fontRenderer.drawString(distanceUp, (this.xSize - fontRenderer.getStringWidth(distanceUp)) / 2, 6 + 32, 0x404040);
        fontRenderer.drawString(distanceLeft, (this.xSize - fontRenderer.getStringWidth(distanceLeft)) / 2 - 40, 6 + 32 + yOffsetCenter, 0x404040);
        fontRenderer.drawString(distanceForward, (this.xSize - fontRenderer.getStringWidth(distanceForward)) / 2, 6 + 32 + yOffsetCenter, 0x404040);
        fontRenderer.drawString(distanceRight, (this.xSize - fontRenderer.getStringWidth(distanceRight)) / 2 + 40, 6 + 32 + yOffsetCenter, 0x404040);
        fontRenderer.drawString(distanceDown, (this.xSize - fontRenderer.getStringWidth(distanceDown)) / 2, 6 + 32 + yOffsetBottom, 0x404040);
         */
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        TileFlexMarker.Movable movable = TileFlexMarker.Movable.valueOf(button.id / 4);
        ButtonMessage message = new ButtonMessage(marker.getPos(), marker.getWorld().provider.getDimension(), movable, amounts[button.id % 4]);
        PacketHandler.sendToServer(message);
    }
}
