package net.jayugg.end_aspected;

import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.entity.VoidBatEntity;
import net.jayugg.end_aspected.entity.VoidMiteEntity;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.item.ModSpawnEggItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndAspected.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.VOIDMITE.get(), VoidMiteEntity.setCustomAttributes().create());
        event.put(ModEntityTypes.VOIDBAT.get(), VoidBatEntity.setCustomAttributes().create());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        ModSpawnEggItem.initSpawnEggs();
    }

}
