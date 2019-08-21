package com.kotori316.marker.packet;

import java.util.Optional;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import com.kotori316.marker.Marker;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Marker.modID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static int getDimId(@Nullable World world) {
        return Optional.ofNullable(world)
            .map(World::getDimension)
            .map(Dimension::getType)
            .map(DimensionType::getId)
            .orElse(0);
    }

    public static void init() {
        int i = 0;
        INSTANCE.registerMessage(i++, ButtonMessage.class, ButtonMessage::toBytes, ButtonMessage::fromBytes, ButtonMessage::onReceive);
        INSTANCE.registerMessage(i++, AreaMessage.class, AreaMessage::toBytes, AreaMessage::fromBytes, AreaMessage::onReceive);
        INSTANCE.registerMessage(i++, Button16Message.class, Button16Message::toBytes, Button16Message::fromBytes, Button16Message::onReceive);
        assert i >= 0;
    }

    public static void sendToClient(Object message, World world) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), message);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}
