package net.jayugg.end_aspected.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientMessage {

    public boolean sendBullet;

    public ClientMessage(){
    }
    public ClientMessage(boolean sendBullet){
        this.sendBullet = sendBullet;
    }

    public static void encode(ClientMessage message, PacketBuffer buffer) {
        buffer.writeBoolean(message.sendBullet);
    }

    public static ClientMessage decode(PacketBuffer buffer) {
        return new ClientMessage(buffer.readBoolean());
    }

    public static void handle(ClientMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        //contextSupplier.get().enqueueWork(() -> {});
        context.setPacketHandled(true);
    }
}
