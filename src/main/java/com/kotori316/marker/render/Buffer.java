package com.kotori316.marker.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector4f;

final class Buffer {
    private final IVertexBuilder bufferBuilder;
    private final MatrixStack matrix;
    private final Vector4f vector4f = new Vector4f();

    Buffer(IVertexBuilder bufferBuilder, MatrixStack matrixStack) {
        this.bufferBuilder = bufferBuilder;
        matrix = matrixStack;
    }

    final Buffer pos(double x, double y, double z) {
        Matrix4f matrix4f = matrix.func_227866_c_().func_227870_a_();

        vector4f.set((float) x, (float) y, (float) z, 1.0F);
        vector4f.func_229372_a_(matrix4f);
        bufferBuilder.func_225582_a_(vector4f.getX(), vector4f.getY(), vector4f.getZ());
        return this;
    }

    /**
     * buffer.color(255, 255, 255, 255)
     *
     * @return this
     */
    final Buffer colored() {
        return this.color(255, 255, 255, 255);
    }

    @SuppressWarnings("SameParameterValue")
    final Buffer color(int red, int green, int blue, int alpha) {
        bufferBuilder.func_225586_a_(red, green, blue, alpha);
        return this;
    }

    final Buffer tex(float u, float v) {
        bufferBuilder.func_225583_a_(u, v);
        return this;
    }

    /**
     * {@code buffer.lightmap(240, 0).endVertex()}
     */
    final void lightedAndEnd() {
        bufferBuilder.func_225585_a_(10, 10).func_225587_b_(240, 0).func_225584_a_(0, 1, 0).endVertex();
    }

    final boolean bufferEq(IVertexBuilder builder) {
        return bufferBuilder == builder;
    }
}
