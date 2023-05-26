package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.item.aote.AspectOfTheEndItem;
import net.jayugg.end_aspected.item.aote.DragonforgedAspectOfTheEndItem;
import net.jayugg.end_aspected.item.aote.NetherforgedAspectOfTheEndItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> ASPECT_OF_THE_END = ITEMS.register("aspect_of_the_end",
            () -> new AspectOfTheEndItem(Tiers.DIAMOND,
                    3,
                    -2.4F,
                    new Item.Properties().tab(ModItemGroup.MAIN_GROUP).rarity(Rarity.EPIC)
            ));

    public static final Rarity LEGENDARY = Rarity.create("Legendary", ChatFormatting.GOLD);
    public static final RegistryObject<Item> NETHERFORGED_ASPECT_OF_THE_END = ITEMS.register("netherforged_aspect_of_the_end",
            () -> new NetherforgedAspectOfTheEndItem(Tiers.NETHERITE,
                    3,
                    -2.4F,
                    new Item.Properties().tab(ModItemGroup.MAIN_GROUP).rarity(LEGENDARY)
            ));

    public static final Rarity MYTHICAL = Rarity.create("Mythical", ChatFormatting.DARK_PURPLE);
    public static final RegistryObject<Item> DRAGONFORGED_ASPECT_OF_THE_END = ITEMS.register("dragonforged_aspect_of_the_end",
                () -> new DragonforgedAspectOfTheEndItem(Tiers.NETHERITE,
                        4,
                        -2.4F,
                        new Item.Properties().tab(ModItemGroup.MAIN_GROUP).rarity(MYTHICAL)
                ));

    public static final RegistryObject<Item> ASPECT_SHARD = ITEMS.register("aspect_shard",
            () -> new AspectShardItem(new Item.Properties().tab(ModItemGroup.MAIN_GROUP))
            );

    public static final RegistryObject<Item> SHULKER_WAND = ITEMS.register("shulker_wand",
            () -> new ShulkerWandItem(Tiers.DIAMOND,
                    0,
                    -2.0F,
                    new Item.Properties().tab(ModItemGroup.MAIN_GROUP).rarity(MYTHICAL)
            ));

    public static final RegistryObject<Item> ASPECTED_ARROW = ITEMS.register("aspected_arrow",
            () -> new AspectedArrowItem(new Item.Properties().tab(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> SOUVENIR_ITEM = ITEMS.register("souvenir",
            () -> new SouvenirItem(new Item.Properties().tab(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> VOIDLING_SPAWN_EGG = ITEMS.register("voidling_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.VOIDLING, 0x5164d0, 0x030a29,
                    new Item.Properties().tab(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> VOID_SEED = ITEMS.register("void_seed",
            () -> new VoidSeedItem(new Item.Properties().tab(ModItemGroup.MAIN_GROUP)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}