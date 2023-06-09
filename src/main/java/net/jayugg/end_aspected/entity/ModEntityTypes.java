package net.jayugg.end_aspected.entity;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EndAspected.MOD_ID);
    public static final RegistryObject<EntityType<AspectedArrowEntity>> ASPECTED_ARROW = ENTITY_TYPES.register("aspected_arrow",
            () -> EntityType.Builder.of((EntityType.EntityFactory<AspectedArrowEntity>) AspectedArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("end_aspected:aspected_arrow"));

    public static final RegistryObject<EntityType<AspectedShulkerBullet>> SHULKER_BULLET = ENTITY_TYPES.register("shulker_bullet",
            () -> EntityType.Builder.of((EntityType.EntityFactory<AspectedShulkerBullet>) AspectedShulkerBullet::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("minecraft:shulker_bullet"));

    public static final RegistryObject<EntityType<VoidlingEntity>> VOIDLING = ENTITY_TYPES.register("voidling",
            () -> EntityType.Builder.of(VoidlingEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.5F)
                    .build("end_aspected:voidling"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
