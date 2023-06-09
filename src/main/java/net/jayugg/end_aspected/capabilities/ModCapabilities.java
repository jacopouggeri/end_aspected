package net.jayugg.end_aspected.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class ModCapabilities {
    @CapabilityInject(TeleportData.class)
    public static final Capability<TeleportData> TELEPORT_DATA = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(TeleportData.class, new Storage(), TeleportData::new);
    }

    public static class Storage implements Capability.IStorage<TeleportData> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<TeleportData> capability, TeleportData instance, Direction side) {
            return IntNBT.valueOf(instance.getTeleportCount());
        }

        @Override
        public void readNBT(Capability<TeleportData> capability, TeleportData instance, Direction side, INBT nbt) {
            if (!(nbt instanceof IntNBT)) throw new IllegalArgumentException("Can not deserialize to an instance of TeleportData");
            instance.setTeleportCount(((IntNBT) nbt).getInt());
        }
    }
}

