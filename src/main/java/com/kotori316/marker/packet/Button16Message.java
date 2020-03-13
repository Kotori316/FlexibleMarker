package com.kotori316.marker.packet;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import com.kotori316.marker.Tile16Marker;

/**
 * To server only.
 */
public class Button16Message {
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

    public static Button16Message fromBytes(PacketBuffer p) {
        Button16Message message = new Button16Message();
        message.pos = p.readBlockPos();
        message.dim = p.readInt();
        message.amount = p.readInt();
        message.yMax = p.readInt();
        message.yMin = p.readInt();
        return message;
    }

    public void toBytes(PacketBuffer p) {
        p.writeBlockPos(pos).writeInt(dim);
        p.writeInt(amount);
        p.writeInt(yMax).writeInt(yMin);
    }

    public void onReceive(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Optional.ofNullable(ctx.get().getSender())
            .map(Entity::getEntityWorld)
            .map(world -> world.getTileEntity(this.pos))
            .filter(t -> t instanceof Tile16Marker && PacketHandler.getDimId(t.getWorld()) == dim)
            .ifPresent(entity -> {
                Tile16Marker marker = (Tile16Marker) entity;
                marker.changeSize(this.amount, this.yMax, this.yMin);
                PacketHandler.sendToClient(new AreaMessage(this.pos, this.dim, marker.min(), marker.max()), entity.getWorld());
            }));
    }

}
