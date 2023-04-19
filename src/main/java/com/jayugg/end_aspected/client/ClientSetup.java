package com.jayugg.end_aspected.client;

import com.jayugg.end_aspected.EndAspected;
import com.jayugg.end_aspected.entity.AspectedArrowRenderer;
import com.jayugg.end_aspected.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.ASPECTED_ARROW.get(), AspectedArrowRenderer::new);
    }
}