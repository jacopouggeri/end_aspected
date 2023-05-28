package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.item.ModItemGroup;
import net.jayugg.end_aspected.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, EndAspected.MOD_ID);

    public static final RegistryObject<Block> ENDER_TRAP_BLOCK = registerBlock("ender_trap",
            () -> new EnderTrapBlock(Block.Properties.of(Material.STONE)
                    .strength(5f)
                    .sound(SoundType.LODESTONE)
                    .strength(3)
            ));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static final RegistryObject<Block> VOID_VEIN_BLOCK = registerBlock("void_vein",
            () -> new VoidVeinBlock(Block.Properties.of(Material.SCULK)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
                    .sound(SoundType.SCULK_VEIN)
                    .lightLevel((state) -> 4),
                    ModTileEntities.VOID_VEIN));

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ModItemGroup.MAIN_GROUP).rarity(ModItems.VOID)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
