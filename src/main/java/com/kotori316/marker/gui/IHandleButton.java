package com.kotori316.marker.gui;

import net.minecraft.client.gui.widget.button.AbstractButton;

public interface IHandleButton {
    void actionPerformed(final Button button);

    class Button extends AbstractButton {
        public final int id;
        private final IHandleButton handler;

        public Button(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, IHandleButton handler) {
            super(x, y, widthIn, heightIn, buttonText);
            this.id = buttonId;
            this.handler = handler;
        }

        @Override
        public void onPress() {
            handler.actionPerformed(this);
        }
    }
}
