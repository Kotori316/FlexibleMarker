package com.kotori316.marker.render;

class ColorBox {
    public final int red;
    public final int green;
    public final int blue;
    public final int alpha;

    public ColorBox(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public static final ColorBox redColor = new ColorBox(0xFF, 0x3D, 0x63, 0xFF);
    public static final ColorBox blueColor = new ColorBox(0x75, 0xCC, 0xFF, 0xFF);
    public static final ColorBox white = new ColorBox(0xFF, 0xFF, 0xFF, 0xFF);
}