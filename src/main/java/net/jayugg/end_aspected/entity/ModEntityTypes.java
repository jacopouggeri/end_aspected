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

    public static final RegistryObject<EntityType<VoidMiteEntity>> VOIDMITE = ENTITY_TYPES.register("voidmite",
            () -> EntityType.Builder.create(VoidMiteEntity::new, EntityClassification.MONSTER)
                    .size(0.5F, 0.5F)
                    .build("end_aspected:voidmite"));

    public static final RegistryObject<EntityType<VoidBatEntity>> VOIDBAT = ENTITY_TYPES.register("voidbat",
            () -> EntityType.Builder.create(VoidBatEntity::new, EntityClassification.MONSTER)
                    .size(0.5F, 0.5F)
                    .build("end_aspected:voidbat"));
    public static final RegistryObject<EntityType<VoidShadeEntity>> VOID_SHADE = ENTITY_TYPES.register("void_shade",
            () -> EntityType.Builder.create(VoidShadeEntity::new, EntityClassification.MONSTER)
                    .size(0.9F, 0.5F)
                    .build("end_aspected:void_shade"));
    public static final RegistryObject<EntityType<VoidBeastEntity>> VOID_BEAST = ENTITY_TYPES.register("void_beast",
            () -> EntityType.Builder.create(VoidBeastEntity::new, EntityClassification.MONSTER)
                    .size(0.9F, 0.5F)
                    .build("end_aspected:void_beast"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
