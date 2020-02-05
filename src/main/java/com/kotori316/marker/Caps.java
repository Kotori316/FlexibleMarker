package com.kotori316.marker;

import java.util.Optional;

import com.yogpc.qp.machines.base.IMarker;
import com.yogpc.qp.machines.base.IRemotePowerOn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.ModList;

public class Caps {
    public static final class QuarryPlus {
        @CapabilityInject(IMarker.class)
        public static final Capability<IMarker> MARKER_CAPABILITY = null;
        @CapabilityInject(IRemotePowerOn.class)
        public static final Capability<IRemotePowerOn> REMOTE_POWER_ON_CAPABILITY = null;
    }

    private static boolean isModLoaded() {
        return ModList.get().isLoaded("quarryplus");
    }

    @SuppressWarnings("ConstantConditions")
    public static Optional<Capability<IMarker>> markerCapability() {
        if (isModLoaded())
            return Optional.ofNullable(QuarryPlus.MARKER_CAPABILITY);
        else
            return Optional.empty();
    }

    @SuppressWarnings("ConstantConditions")
    public static Optional<Capability<IRemotePowerOn>> remotePowerOnCapability() {
        if (isModLoaded())
            return Optional.ofNullable(QuarryPlus.REMOTE_POWER_ON_CAPABILITY);
        else
            return Optional.empty();
    }
}
