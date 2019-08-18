package com.kotori316.marker;

public class Range {
    private final float min;
    private final float max;
    private final float distance;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
        if (max < min) {
            throw new IllegalArgumentException(String.format("min is grater than max. Min: %f, Max:%f", min, max));
        }
        this.distance = max - min;
    }

    public float convert(float f) {
        if (f < min) {
            int i = (int) ((min - f) / distance) + 1;
            return convert(f + distance * i);
        } else if (f > max) {
            int i = (int) ((f - max) / distance) + 1;
            return convert(f - distance * i);
        } else {
            return f;
        }
    }

    public static void main(String[] args) {
        Range range = new Range(0, 8);
        System.out.printf("convert of 4 is %f.\n", range.convert(4));
        System.out.printf("convert of 0 is %f.\n", range.convert(0));
        System.out.printf("convert of 8 is %f.\n", range.convert(8));
        System.out.printf("convert of 12 is %f.\n", range.convert(12));
        System.out.printf("convert of -4 is %f.\n", range.convert(-4));
        System.out.printf("convert of -5 is %f.\n", range.convert(-5));
    }
}
