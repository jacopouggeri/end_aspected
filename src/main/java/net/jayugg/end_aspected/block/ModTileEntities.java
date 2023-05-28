package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EndAspected.MOD_ID);

    public static final RegistryObject<BlockEntityType<VoidVeinTileEntity>> VOID_VEIN =
            TILE_ENTITIES.register("void_vein",
                    () -> BlockEntityType.Builder.of(VoidVeinTileEntity::new, ModBlocks.VOID_VEIN_BLOCK.get()).build(null));
    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}