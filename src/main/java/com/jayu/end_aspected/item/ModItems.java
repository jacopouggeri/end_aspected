package com.jayu.end_aspected.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.jayu.end_aspected.EndAspected.MOD_ID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> ASPECT_OF_THE_END = ITEMS.register("aspect_of_the_end",
            () -> new AspectOfTheEndItem(ItemTier.DIAMOND,
                    3,
                    -2.4F,
                    new Item.Properties().group(ItemGroup.COMBAT).rarity(Rarity.EPIC),
                    8,
                    6,
                    (long) 3.0
            ));

    public static final Rarity LEGENDARY = Rarity.create("Legendary", TextFormatting.GOLD);
    public static final RegistryObject<Item> NETHERFORGED_ASPECT_OF_THE_END = ITEMS.register("netherforged_aspect_of_the_end",
            () -> new AspectOfTheEndItem(ItemTier.NETHERITE,
                    3,
                    -2.4F,
                    new Item.Properties().group(ItemGroup.COMBAT).rarity(LEGENDARY),
                    8,
                    12,
                    (long) 1.5
            ));

    public static final Rarity MYTHICAL = Rarity.create("Mythical", TextFormatting.DARK_PURPLE);

    public static final RegistryObject<Item> DRAGONFORGED_ASPECT_OF_THE_END = ITEMS.register("dragonforged_aspect_of_the_end",
            () -> new AspectOfTheEndItem(ItemTier.NETHERITE,
                    4,
                    -2.4F,
                    new Item.Properties().group(ItemGroup.COMBAT).rarity(MYTHICAL),
                    8,
                    20,
                    (long) 1.5
            ));
    
    public static final RegistryObject<Item> ENDER_EYE_GEM = ITEMS.register("ender_gem",
            () -> new EnderGemItem(new Item.Properties().group(ItemGroup.MISC))
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}