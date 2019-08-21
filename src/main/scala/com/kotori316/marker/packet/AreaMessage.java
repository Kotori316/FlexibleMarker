package com.kotori316.marker.packet;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import com.kotori316.marker.IAreaConfigurable;

/**
 * To Client Only
 */
public class AreaMessage {
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

    public static AreaMessage fromBytes(PacketBuffer p) {
        AreaMessage message = new AreaMessage();
        message.pos = p.readBlockPos();
        message.dim = p.readInt();
        message.min = p.readBlockPos();
        message.max = p.readBlockPos();
        return message;
    }

    public void toBytes(PacketBuffer p) {
        p.writeBlockPos(pos).writeInt(dim);
        p.writeBlockPos(min).writeBlockPos(max);
    }

    public void onReceive(Supplier<NetworkEvent.Context> ctx) {
        Optional.ofNullable(Minecraft.getInstance().world)
            .map(world -> world.getTileEntity(this.pos))
            .filter(t -> t instanceof IAreaConfigurable && PacketHandler.getDimId(t.getWorld()) == dim)
            .ifPresent(entity -> {
                IAreaConfigurable marker = (IAreaConfigurable) entity;
                ctx.get().enqueueWork(marker.setMinMax(this.min, this.max));
            });
        ctx.get().setPacketHandled(true);
    }
}
