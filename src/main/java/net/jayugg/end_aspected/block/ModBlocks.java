package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.block.tree.*;
import net.jayugg.end_aspected.item.ModItemGroup;
import net.jayugg.end_aspected.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, EndAspected.MOD_ID);

    public static final RegistryObject<Block> ENDER_TRAP = registerBlock("ender_trap",
            () -> new EnderTrapBlock(Block.Properties.from(Blocks.LODESTONE)
                    .harvestTool(ToolType.PICKAXE).setRequiresTool()
                    .setLightLevel((state) -> 3)
                    .sound(SoundType.LODESTONE)
            ), ModItems.ASPECT);


    public static RegistryObject<Block> VOID_VEIN = registerBlock("void_vein",
            () -> new VoidVeinBlock(Block.Properties.from(Blocks.WEEPING_VINES)
                    .harvestTool(ToolType.HOE)
                    .notSolid()
                    .setLightLevel((state) -> 4)
                    .sound(SoundType.NETHER_VINE)
            ), ModItems.VOID);

    public static final RegistryObject<Block> VOID_LEAVES = registerBlock("void_leaves",
            () -> new VoidLeavesBlock(Block.Properties.from(Blocks.WARPED_WART_BLOCK)
                    .harvestTool(ToolType.HOE)
                    .notSolid()
                    .setLightLevel((state) -> 4)
            ), ModItems.VOID);

    public static final RegistryObject<Block> VOID_FUNGUS = registerBlock("void_fungus",
            () -> new VoidFungusBlock(new VoidTree(),
                    Block.Properties.from(Blocks.WARPED_FUNGUS)
            ), ModItems.VOID);

    public static final RegistryObject<Block> VOID_STEM = registerBlock("void_stem",
            () -> new VoidStemBlock(Block.Properties.from(Blocks.WARPED_STEM)
                    .hardnessAndResistance(25.0F, 600.0F)
                    .harvestTool(ToolType.AXE)
                    .sound(SoundType.WOOD)
            ), ModItems.VOID);

    public static final RegistryObject<Block> VOID_BLOCK = registerBlock("void",
            () -> new VoidBlock(Block.Properties.create(Material.GLASS)
                    .harvestTool(ToolType.HOE)
                    .hardnessAndResistance(0.2F)
                    .sound(SoundType.FUNGUS)
            ), ModItems.VOID);

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, Rarity rarity) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, Rarity rarity) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(rarity)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}