package com.jayugg.end_aspected.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ModItemGroup {
    public static final CreativeModeTab MAIN_GROUP = new CreativeModeTab("mainGroupTab") {
        @Override
        public @Nonnull ItemStack makeIcon() {
            return new ItemStack(
                    ModItems.NETHERFORGED_ASPECT_OF_THE_END.get()
            );
        }
    };
}
