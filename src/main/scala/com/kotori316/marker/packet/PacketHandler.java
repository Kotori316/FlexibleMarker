package com.kotori316.marker.packet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.kotori316.marker.Marker;

public class PacketHandler {
    private static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Marker.ModName);
        int i = 0;
        wrapper.registerMessage(ButtonMessage::onReceive, ButtonMessage.class, i++, Side.SERVER);
        wrapper.registerMessage(AreaMessage::onReceive, AreaMessage.class, i++, Side.CLIENT);
        assert i >= 0;
    }

    public static void sendToServer(IMessage message) {
        wrapper.sendToServer(message);
    }
}
