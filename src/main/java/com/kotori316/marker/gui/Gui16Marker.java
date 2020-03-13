package com.kotori316.marker.gui;

import com.mojang.blaze3d.systems.RenderSystem;
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
    private static final int CHUNK = 16;
    private final Tile16Marker marker;
    private static final int BUTTON_WIDTH = 40;

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
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(LOCATION);
        this.blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        this.font.drawString("Size", (this.xSize - this.font.getStringWidth("Size")) / 2f, 6, 0x404040);
        String sizeText = Integer.toString(marker.getSize() / CHUNK);
        this.font.drawString(sizeText, (this.xSize - this.font.getStringWidth(sizeText)) / 2f, 15 + 23, 0x404040);
        String yMaxText = Integer.toString(marker.max().getY());
        String yMinText = Integer.toString(marker.min().getY());
        this.font.drawString(yMaxText, (this.xSize - this.font.getStringWidth(yMaxText)) / 2f + 10 + BUTTON_WIDTH, 15 + 23, 0x404040);
        this.font.drawString(yMinText, (this.xSize - this.font.getStringWidth(yMinText)) / 2f - 10 - BUTTON_WIDTH, 15 + 23, 0x404040);
    }

    @Override
    public void init() {
        super.init();
        final int tp = 15;
        final int middle = guiLeft + this.xSize / 2;
        this.addButton(new IHandleButton.Button(0, middle - BUTTON_WIDTH / 2, guiTop + tp, BUTTON_WIDTH, 20, "+", this));
        this.addButton(new IHandleButton.Button(1, middle - BUTTON_WIDTH / 2, guiTop + tp + 33, BUTTON_WIDTH, 20, "-", this));
        this.addButton(new IHandleButton.Button(2, middle + BUTTON_WIDTH / 2 + 10, guiTop + tp, BUTTON_WIDTH, 20, "Top+", this));
        this.addButton(new IHandleButton.Button(3, middle + BUTTON_WIDTH / 2 + 10, guiTop + tp + 33, BUTTON_WIDTH, 20, "Top-", this));
        this.addButton(new IHandleButton.Button(4, middle - BUTTON_WIDTH / 2 - 10 - BUTTON_WIDTH, guiTop + tp, BUTTON_WIDTH, 20, "Bottom+", this));
        this.addButton(new IHandleButton.Button(5, middle - BUTTON_WIDTH / 2 - 10 - BUTTON_WIDTH, guiTop + tp + 33, BUTTON_WIDTH, 20, "Bottom-", this));

    }

    @Override
    public void actionPerformed(Button button) {
        int size = marker.getSize();
        int yMin = marker.min().getY(), yMax = marker.max().getY();
        switch (button.id) {
            case 0: // Plus
                size = marker.getSize() + CHUNK;
                break;
            case 1: // Minus
                if (marker.getSize() > CHUNK) {
                    size = marker.getSize() - CHUNK;
                } else {
                    size = marker.getSize();
                }
                break;
            case 2:
                yMax = marker.max().getY() + 1;
                break;
            case 3:
                yMax = Math.max(marker.max().getY() - 1, yMin);
                break;
            case 4:
                yMin = Math.min(marker.min().getY() + 1, yMax);
                break;
            case 5:
                yMin = Math.max(marker.min().getY() - 1, 0);
                break;
        }
        PacketHandler.sendToServer(new Button16Message(marker.getPos(), PacketHandler.getDimId(marker.getWorld()), size, yMax, yMin));
    }
}
