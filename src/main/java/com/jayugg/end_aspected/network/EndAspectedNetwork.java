package com.jayugg.end_aspected.network;

import com.jayugg.end_aspected.EndAspected;
import com.jayugg.end_aspected.network.message.ShulkerBulletMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class EndAspectedNetwork {
    public static final String NETWORK_VERSION = "0.1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(EndAspected.MOD_ID, "network"), () -> NETWORK_VERSION, version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init() {

        CHANNEL.registerMessage(0, ShulkerBulletMessage.class, ShulkerBulletMessage::encode, ShulkerBulletMessage::decode, ShulkerBulletMessage::handle);

    }

}
