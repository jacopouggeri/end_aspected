package net.jayugg.end_aspected.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModItemGroup {
    public static final ItemGroup MAIN_GROUP = new ItemGroup("mainGroupTab") {
        @Override
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(
                    ModItems.NETHERFORGED_ASPECT_OF_THE_END.get()
            );
        }
    };
}
