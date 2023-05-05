package net.jayugg.end_aspected.client;


import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.AspectedArrowRenderer;
import net.jayugg.end_aspected.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        registerEntityRenderingHandler(ModEntities.ASPECTED_ARROW.get(), AspectedArrowRenderer::new);
    }
}
