package net.jayugg.end_aspected;

import net.jayugg.end_aspected.block.EnderTrapBlock;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.effect.UnstablePhaseEffect;
import net.jayugg.end_aspected.enchantment.DestabiliseEnchantment;
import net.jayugg.end_aspected.enchantment.EnderSlayerEnchantment;
import net.jayugg.end_aspected.enchantment.ModEnchantments;
import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.villager.ModTrades;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EndAspected.MOD_ID)
public class EndAspected
{
    public static final String MOD_ID = "end_aspected";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public EndAspected() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, "end_aspected.toml");

        // Register the setup method for modloading
        eventBus.addListener(this::setup);

        // Register Effects
        ModEffects.register(eventBus);
        // Register Enchantments
        ModEnchantments.register(eventBus);
        // Register Blocks
        ModBlocks.register(eventBus);
        // Register Items
        ModItems.register(eventBus);
        // Register Entities
        ModEntityTypes.register(eventBus);

        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModTrades::fillTradeData);
        // EndAspectedNetwork.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }

    @SubscribeEvent
    public void onEntityTeleport(final EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        // LOGGER.info("TELEPORT EVENT" + entity.getName());
        // Check for teleport hijacking or jamming effects
        UnstablePhaseEffect.damageTeleporter(entity);
        EnderTrapBlock.trapEventEntity(event, entity);
    }

    @SubscribeEvent
    public void getEntityLastDamage(LivingHurtEvent event) {
        //LOGGER.info("ENTITY HURT!");
        // Handler Ender Slayer event
        EnderSlayerEnchantment.getLastDamageInflicted(event);
        DestabiliseEnchantment.onLivingHurt(event);
    }

}
