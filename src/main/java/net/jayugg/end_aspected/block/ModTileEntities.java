package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EndAspected.MOD_ID);

    public static final RegistryObject<TileEntityType<VoidVeinTileEntity>> VOID_VEIN =
            TILE_ENTITIES.register("void_vein",
                    () -> TileEntityType.Builder.create(VoidVeinTileEntity::new, ModBlocks.VOID_VEIN_BLOCK.get()).build(null));
    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
