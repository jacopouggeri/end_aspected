package net.jayugg.end_aspected;

import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.entity.VoidlingEntity;
import net.jayugg.end_aspected.entity.renderer.VoidlingRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.VOIDLING.get(), VoidlingRenderer::new);
    }
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.VOIDLING.get(), VoidlingEntity.createAttributes().build());
    }
}
