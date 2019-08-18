package com.kotori316.marker.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.kotori316.marker.IAreaConfigurable;
import com.kotori316.marker.TileFlexMarker;

/**
 * To Client Only
 */
public class AreaMessage implements IMessage {
    private BlockPos pos;
    private int dim;
    private BlockPos min, max;

    @SuppressWarnings("unused")
    public AreaMessage() {
    }

    public AreaMessage(BlockPos pos, int dim, BlockPos min, BlockPos max) {
        this.pos = pos;
        this.dim = dim;
        this.min = min;
        this.max = max;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        pos = p.readBlockPos();
        dim = p.readInt();
        min = p.readBlockPos();
        max = p.readBlockPos();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        p.writeBlockPos(pos).writeInt(dim);
        p.writeBlockPos(min).writeBlockPos(max);
    }

    public static IMessage onReceive(AreaMessage message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().world;
        TileEntity entity = world.getTileEntity(message.pos);
        if (entity instanceof IAreaConfigurable && world.provider.getDimension() == message.dim) {
            IAreaConfigurable marker = (IAreaConfigurable) entity;
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(marker.setMinMax(message.min, message.max));
        }
        return null;
    }
}
