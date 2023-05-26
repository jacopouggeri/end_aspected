package net.jayugg.end_aspected.entity;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, EndAspected.MOD_ID);
    public static final RegistryObject<EntityType<AspectedArrowEntity>> ASPECTED_ARROW = ENTITY_TYPES.register("aspected_arrow",
            () -> EntityType.Builder.<AspectedArrowEntity>create(AspectedArrowEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F)
                    .build("end_aspected:aspected_arrow"));

    public static final RegistryObject<EntityType<AspectedShulkerBulletEntity>> SHULKER_BULLET = ENTITY_TYPES.register("shulker_bullet",
            () -> EntityType.Builder.<AspectedShulkerBulletEntity>create(AspectedShulkerBulletEntity::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F)
                    .build("minecraft:shulker_bullet"));

    public static final RegistryObject<EntityType<VoidlingEntity>> VOIDLING = ENTITY_TYPES.register("voidling",
            () -> EntityType.Builder.create(VoidlingEntity::new, EntityClassification.MONSTER)
                    .size(1F, 1F)
                    .build("end_aspected:voidling"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
