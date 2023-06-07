package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.item.end.*;
import net.jayugg.end_aspected.item.voids.DimensionHopperItem;
import net.jayugg.end_aspected.item.voids.VoidSeedItem;
import net.jayugg.end_aspected.item.voids.armor.VoidArmorItem;
import net.jayugg.end_aspected.item.voids.armor.VoidArmorMaterial;
import net.jayugg.end_aspected.item.voids.tool.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

@SuppressWarnings("unused")
public class ModItems {

    public static final Rarity ASPECT = Rarity.create("Aspect", TextFormatting.DARK_AQUA);
    public static final Rarity DRAGON = Rarity.create("Dragon", TextFormatting.DARK_PURPLE);
    public static final Rarity VOID = Rarity.create("Void", TextFormatting.BLUE);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> ASPECT_SHARD = ITEMS.register("aspect_shard",
            () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(ASPECT))
    );

    public static final RegistryObject<Item> SOUVENIR = ITEMS.register("souvenir",
            () -> new SouvenirItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(1).rarity(ASPECT)));

    public static final RegistryObject<Item> ASPECT_OF_THE_END = ITEMS.register("aspect_of_the_end",
            () -> new AspectOfTheEndItem(ItemTier.DIAMOND,
                    3,
                    -2.4F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(ASPECT)
            ));
    public static final RegistryObject<Item> NETHERFORGED_ASPECT_OF_THE_END = ITEMS.register("netherforged_aspect_of_the_end",
            () -> new NetherforgedAspectOfTheEndItem(ItemTier.NETHERITE,
                    3,
                    -2.4F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(ASPECT)
            ));
    public static final RegistryObject<Item> DRAGONFORGED_ASPECT_OF_THE_END = ITEMS.register("dragonforged_aspect_of_the_end",
                () -> new DragonforgedAspectOfTheEndItem(ItemTier.NETHERITE,
                        4,
                        -2.4F,
                        new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(DRAGON)
                ));

    public static final RegistryObject<Item> SHULKER_WAND = ITEMS.register("shulker_wand",
            () -> new ShulkerWandItem(ItemTier.DIAMOND,
                    0,
                    -2.0F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(ASPECT)
            ));

    public static final RegistryObject<Item> ASPECTED_ARROW = ITEMS.register("aspected_arrow",
            () -> new AspectedArrowItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(64).rarity(ASPECT)));
    public static final RegistryObject<Item> VOID_SEED = ITEMS.register("void_seed",
            () -> new VoidSeedItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(1).rarity(VOID)));
    public static final RegistryObject<ModSpawnEggItem> VOIDMITE_SPAWN_EGG = ITEMS.register("voidmite_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.VOIDMITE, 0x07111e, 0x327b97,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<ModSpawnEggItem> VOIDBAT_SPAWN_EGG = ITEMS.register("voidbat_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.VOIDBAT, 0x31dce9, 0x327b97,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)));
    public static final RegistryObject<ModSpawnEggItem> VOIDSHADE_SPAWN_EGG = ITEMS.register("void_shade_spawn_egg",
            () -> new ModSpawnEggItem(ModEntityTypes.VOID_SHADE, 0x31dce9, 0x327b97,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> DIMENSION_HOPPER = ITEMS.register("dimension_hopper",
            () -> new DimensionHopperItem(new Item.Properties().group(ModItemGroup.MAIN_GROUP).maxStackSize(1).rarity(VOID)));

    public static final RegistryObject<Item> VOID_SWORD = ITEMS.register("void_sword",
            () -> new VoidSwordItem(new VoidItemTier(),
                    3, -2.4F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_SHOVEL = ITEMS.register("void_shovel",
            () -> new VoidShovelItem(new VoidItemTier(),
                    1.5F, -3.0F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));
    public static final RegistryObject<Item> VOID_AXE = ITEMS.register("void_axe",
            () -> new VoidAxeItem(new VoidItemTier(),
                    5.0F, -3.0F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_PICKAXE = ITEMS.register("void_pickaxe",
            () -> new VoidPickaxeItem(new VoidItemTier(),
                    1, -2.8F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_HOE = ITEMS.register("void_hoe",
            () -> new VoidHoeItem(new VoidItemTier(),
                    -3, 0.0F,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_HELMET = ITEMS.register("void_helmet",
            () -> new VoidArmorItem(new VoidArmorMaterial(), EquipmentSlotType.HEAD,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_CHESTPLATE = ITEMS.register("void_chestplate",
            () -> new VoidArmorItem(new VoidArmorMaterial(), EquipmentSlotType.CHEST,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_LEGGINGS = ITEMS.register("void_leggings",
            () -> new VoidArmorItem(new VoidArmorMaterial(), EquipmentSlotType.LEGS,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_BOOTS = ITEMS.register("void_boots",
            () -> new VoidArmorItem(new VoidArmorMaterial(), EquipmentSlotType.FEET,
                    new Item.Properties().group(ModItemGroup.MAIN_GROUP)
            ));

    public static final RegistryObject<Item> VOID_SAP = ITEMS.register("void_sap",
            () -> new Item((new Item.Properties()).containerItem(Items.GLASS_BOTTLE).group(ModItemGroup.MAIN_GROUP).rarity(VOID)
            ));

    public static final RegistryObject<Item> VOID_GEM = ITEMS.register("void_gem",
            () -> new Item(new Item.Properties().group(ModItemGroup.MAIN_GROUP).rarity(VOID))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}