package com.kotori316.marker.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.kotori316.marker.Marker;
import com.kotori316.marker.Tile16Marker;
import com.kotori316.marker.packet.Button16Message;
import com.kotori316.marker.packet.PacketHandler;

public class Gui16Marker extends GuiContainer {
    private static final ResourceLocation LOCATION = new ResourceLocation(Marker.modID, "textures/gui/marker.png");
    private static final int CHUNK = 16;
    private final Tile16Marker marker;
    private static final int BUTTON_WIDTH = 40;

    public Gui16Marker(EntityPlayer player, Tile16Marker marker) {
        super(new ContainerMarker(player));
        this.marker = marker;
        //217, 188
        this.xSize = 217;
        this.ySize = 188;
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
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
        this.fontRenderer.drawString("Size", this.xSize / 2 - this.fontRenderer.getStringWidth("Size") / 2, 6, 0x404040);
        String sizeText = Integer.toString(marker.getSize() / CHUNK);
        this.fontRenderer.drawString(sizeText, this.xSize / 2 - this.fontRenderer.getStringWidth(sizeText) / 2, 15 + 23, 0x404040);
        String yMaxText = Integer.toString(marker.max().getY());
        String yMinText = Integer.toString(marker.min().getY());
        this.fontRenderer.drawString(yMaxText, this.xSize / 2 + 10 + BUTTON_WIDTH - this.fontRenderer.getStringWidth(yMaxText) / 2, 15 + 23, 0x404040);
        this.fontRenderer.drawString(yMinText, this.xSize / 2 - 10 - BUTTON_WIDTH - this.fontRenderer.getStringWidth(yMinText) / 2, 15 + 23, 0x404040);

        for (GuiButton button : this.buttonList) {
            if (button.isMouseOver()) {
                button.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        final int tp = 15;
        final int middle = guiLeft + this.xSize / 2;
        this.buttonList.add(new GuiButton(0, middle - BUTTON_WIDTH / 2, guiTop + tp, BUTTON_WIDTH, 20, "+"));
        this.buttonList.add(new GuiButton(1, middle - BUTTON_WIDTH / 2, guiTop + tp + 33, BUTTON_WIDTH, 20, "-"));
        this.buttonList.add(new YChangeButton(2, middle + BUTTON_WIDTH / 2 + 10, guiTop + tp, BUTTON_WIDTH, 20, "Top+"));
        this.buttonList.add(new YChangeButton(3, middle + BUTTON_WIDTH / 2 + 10, guiTop + tp + 33, BUTTON_WIDTH, 20, "Top-"));
        this.buttonList.add(new YChangeButton(4, middle - BUTTON_WIDTH / 2 - 10 - BUTTON_WIDTH, guiTop + tp, BUTTON_WIDTH, 20, "Bottom+"));
        this.buttonList.add(new YChangeButton(5, middle - BUTTON_WIDTH / 2 - 10 - BUTTON_WIDTH, guiTop + tp + 33, BUTTON_WIDTH, 20, "Bottom-"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        int size = marker.getSize();
        int yMin = marker.min().getY(), yMax = marker.max().getY();
        int n;
        if (isShiftKeyDown() && isCtrlKeyDown()) { // Shift + Ctrl
            n = 64;
        } else if (isShiftKeyDown()) { // Shift
            n = 16;
        } else if (isCtrlKeyDown()) { // Ctrl
            n = 4;
        } else {
            n = 1;
        }
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
                yMax = marker.max().getY() + n;
                break;
            case 3:
                yMax = Math.max(marker.max().getY() - n, yMin);
                break;
            case 4:
                yMin = Math.min(marker.min().getY() + n, yMax);
                break;
            case 5:
                yMin = Math.max(marker.min().getY() - n, 0);
                break;
        }
        PacketHandler.sendToServer(new Button16Message(marker.getPos(), marker.getWorld().provider.getDimension(), size, yMax, yMin));
    }

    private class YChangeButton extends GuiButton {
        public YChangeButton(int index, int x, int y, int width, int height, String text) {
            super(index, x, y, width, height, text);
        }

        @Override
        public void drawButtonForegroundLayer(int x, int y) {
            super.drawButtonForegroundLayer(x, y);
            Gui16Marker.this.drawHoveringText("Ctrl: 4, Shift: 16, Ctrl+Shift: 64", x, y);
        }
    }
}
