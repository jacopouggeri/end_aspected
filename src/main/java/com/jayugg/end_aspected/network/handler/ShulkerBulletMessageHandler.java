package com.jayugg.end_aspected.network.handler;

import com.jayugg.end_aspected.item.ShulkerWandItem;
import com.jayugg.end_aspected.network.message.ShulkerBulletMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ShulkerBulletMessageHandler {
    public static void handlePacket(ShulkerBulletMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        PlayerEntity player = context.getSender();
        if (message.sendBullet) {
            ShulkerWandItem.spawnBulletEntity(Objects.requireNonNull(player), player.getEntityWorld());
        }
    }
}
