package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.block.tile.ModTileEntities;
import net.jayugg.end_aspected.block.tree.VoidTree;
import net.jayugg.end_aspected.item.ModItemGroup;
import net.jayugg.end_aspected.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, EndAspected.MOD_ID);

    public static final RegistryObject<Block> ENDER_TRAP_BLOCK = registerBlock("ender_trap",
            () -> new EnderTrapBlock(Block.Properties.from(Blocks.LODESTONE)
                    .harvestTool(ToolType.PICKAXE).setRequiresTool()
                    .setLightLevel((state) -> 3)
                    .sound(SoundType.LODESTONE)
            ));

    public static final RegistryObject<Block> VOID_VEIN_BLOCK = registerBlock("void_vein",
            () -> new VoidVeinBlock(Block.Properties.create(Material.PLANTS)
                    .hardnessAndResistance(0.0F)
                    .harvestTool(ToolType.HOE)
                    .notSolid()
                    .setLightLevel((state) -> 3)
                    .sound(SoundType.NETHER_VINE),
                    ModTileEntities.VOID_VEIN));

    public static final RegistryObject<Block> VOID_STEM = registerBlock("void_stem",
            () -> new VoidStemBlock(Block.Properties.create(Material.NETHER_WOOD)
                    .harvestTool(ToolType.AXE)
                    .sound(SoundType.WOOD),
                    ModTileEntities.VOID_TREE));

    public static final RegistryObject<Block> VOID_LEAVES = registerBlock("void_leaves",
            () -> new LeavesBlock(Block.Properties.create(Material.LEAVES)
                    .hardnessAndResistance(0.2f)
                    .setLightLevel((state) -> 3)
                    .harvestTool(ToolType.HOE)
                    .sound(SoundType.PLANT)));

    public static final RegistryObject<Block> VOID_FUNGUS = registerBlock("void_fungus",
            () -> new VoidFungusBlock(new VoidTree(),
                    Block.Properties.from(Blocks.WARPED_FUNGUS),
                    ModTileEntities.VOID_TREE));


    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(ModItems.VOID)));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}