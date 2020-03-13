package com.kotori316.marker.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.kotori316.marker.Tile16Marker;

/**
 * To server only.
 */
public class Button16Message implements IMessage {
    private BlockPos pos;
    private int dim;
    private int amount;
    private int yMax;
    private int yMin;

    @SuppressWarnings("unused")
    public Button16Message() {
    }

    public Button16Message(BlockPos pos, int dim, int amount, int yMax, int yMin) {
        this.pos = pos;
        this.dim = dim;
        this.amount = amount;
        this.yMax = yMax;
        this.yMin = yMin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        pos = p.readBlockPos();
        dim = p.readInt();
        amount = p.readInt();
        yMax = p.readInt();
        yMin = p.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        p.writeBlockPos(pos).writeInt(dim);
        p.writeInt(amount);
        p.writeInt(yMax).writeInt(yMin);
    }

    public static IMessage onReceive(Button16Message message, MessageContext ctx) {
        World world = ctx.getServerHandler().player.world;
        TileEntity entity = world.getTileEntity(message.pos);
        if (entity instanceof Tile16Marker && world.provider.getDimension() == message.dim) {
            Tile16Marker marker = (Tile16Marker) entity;
            marker.changeSize(message.amount, message.yMax, message.yMin);
            return new AreaMessage(message.pos, message.dim, marker.min(), marker.max());
        }
        return null;
    }
}
