package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.EndAspected;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, EndAspected.MOD_ID);
    public static final RegistryObject<EntityType<AspectedArrowEntity>> ASPECTED_ARROW = ENTITY_TYPES.register("aspected_arrow",
            () -> EntityType.Builder.<AspectedArrowEntity>create(AspectedArrowEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F)
                    .build("end_aspected:aspected_arrow"));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
