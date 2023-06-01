package net.jayugg.end_aspected.util;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags {

    public static final Tags.IOptionalNamedTag<Block> VOID_FLORA_BLOCKS = Blocks.createTag("void_flora");
    public static final Tags.IOptionalNamedTag<Block> PERSISTENT_VOID_FLORA_BLOCKS = Blocks.createTag("persistent_void_flora");
    public static final Tags.IOptionalNamedTag<Item> VOID_FLORA_ITEMS = Items.createTag("void_flora");
    public static final Tags.IOptionalNamedTag<Item> VOID_SOURCE_ITEMS = Items.createTag("void_source");
    public static final Tags.IOptionalNamedTag<Item> END_SOURCE_ITEMS = Items.createTag("end_source");
    public static class Blocks {
        public static Tags.IOptionalNamedTag<Block> createTag(String name) {
            return BlockTags.createOptional(EndAspected.prefix(name));
        }
        public static Tags.IOptionalNamedTag<Block> createForgeTag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class Items {
        public static Tags.IOptionalNamedTag<Item> createTag(String name) {
            return ItemTags.createOptional(EndAspected.prefix(name));
        }
        public static Tags.IOptionalNamedTag<Item> createForgeTag(String name) {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }
}
