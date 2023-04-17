package com.jayugg.end_aspected.network.message;

import com.jayugg.end_aspected.network.handler.ShulkerBulletMessageHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ShulkerBulletMessage {

    public boolean sendBullet;

    public ShulkerBulletMessage(){
    }
    public ShulkerBulletMessage(boolean sendBullet){
        this.sendBullet = sendBullet;
    }

    public static void encode(ShulkerBulletMessage message, PacketBuffer buffer) {
        buffer.writeBoolean(message.sendBullet);
    }

    public static ShulkerBulletMessage decode(PacketBuffer buffer) {
        return new ShulkerBulletMessage(buffer.readBoolean());
    }

    public static void handle(ShulkerBulletMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        contextSupplier.get().enqueueWork(() ->
                // Make sure it's only executed on the physical client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ShulkerBulletMessageHandler.handlePacket(message, contextSupplier))
        );
        context.setPacketHandled(true);
    }
}
