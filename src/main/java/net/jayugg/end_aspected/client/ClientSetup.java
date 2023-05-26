package net.jayugg.end_aspected.client;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.renderer.AspectedArrowRenderer;
import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntityTypes.ASPECTED_ARROW.get(), AspectedArrowRenderer::new);
    }
}