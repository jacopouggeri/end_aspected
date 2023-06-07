package net.jayugg.end_aspected;

import net.jayugg.end_aspected.entity.*;
import net.jayugg.end_aspected.item.ModSpawnEggItem;
import net.jayugg.end_aspected.particle.ModParticleTypes;
import net.jayugg.end_aspected.particle.VoidChargeParticle;
import net.jayugg.end_aspected.particle.VoidChargePopParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.VOIDMITE.get(), VoidMiteEntity.setCustomAttributes().create());
        event.put(ModEntityTypes.VOIDBAT.get(), VoidBatEntity.setCustomAttributes().create());
        event.put(ModEntityTypes.VOID_SHADE.get(), VoidShadeEntity.setCustomAttributes().create());
        event.put(ModEntityTypes.VOID_BEAST.get(), VoidBeastEntity.setCustomAttributes().create());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.initSpawnEggs();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onParticleFactoryRegistration(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(ModParticleTypes.VOID_CHARGE.get(),
                VoidChargeParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(ModParticleTypes.VOID_CHARGE_POP.get(),
                VoidChargePopParticle.Factory::new);
    }

}
