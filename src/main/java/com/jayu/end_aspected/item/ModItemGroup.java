package com.jayu.end_aspected.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static com.jayu.end_aspected.item.ModItems.ASPECT_OF_THE_END;

public class ModItemGroup {
    public static final ItemGroup MAIN_GROUP = new ItemGroup("mainGroupTab") {
        @Override
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(
                    ASPECT_OF_THE_END.get()
            );
        }
    };
}
