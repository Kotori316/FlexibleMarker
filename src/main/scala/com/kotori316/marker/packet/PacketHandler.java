package com.kotori316.marker.packet;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.kotori316.marker.Marker;

public class PacketHandler {
    private static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Marker.ModName);
        AtomicInteger i = new AtomicInteger(0);
        wrapper.registerMessage(ButtonMessage::onReceive, ButtonMessage.class, i.getAndIncrement(), Side.SERVER);
        wrapper.registerMessage(AreaMessage::onReceive, AreaMessage.class, i.getAndIncrement(), Side.CLIENT);
        wrapper.registerMessage(Button16Message::onReceive, Button16Message.class, i.getAndIncrement(), Side.SERVER);
    }

    public static void sendToServer(IMessage message) {
        wrapper.sendToServer(message);
    }
}
