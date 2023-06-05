package net.jayugg.end_aspected;

import net.jayugg.end_aspected.entity.*;
import net.jayugg.end_aspected.item.ModSpawnEggItem;
import net.minecraft.entity.EntityType;
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

}
