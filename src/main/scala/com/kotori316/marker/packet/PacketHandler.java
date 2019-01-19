package com.kotori316.marker.packet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.kotori316.marker.Marker;

public class PacketHandler {
    private static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Marker.ModName);
    }

    public static void sendToServer(IMessage message) {
        wrapper.sendToServer(message);
    }
}
