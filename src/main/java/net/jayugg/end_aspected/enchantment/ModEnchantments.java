package net.jayugg.end_aspected.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

public class ModEnchantments{
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

    public static final RegistryObject<Enchantment> ENDER_SLAYER = ENCHANTMENTS.register("ender_slayer",
            () -> new EnderSlayerEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND)
    );

    public static final RegistryObject<Enchantment> DESTABILISE = ENCHANTMENTS.register("destabilise",
            () -> new DestabiliseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND)
    );

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}