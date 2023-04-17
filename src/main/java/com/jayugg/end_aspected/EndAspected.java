package com.jayugg.end_aspected;

import com.jayugg.end_aspected.block.EnderTrapBlock;
import com.jayugg.end_aspected.block.ModBlocks;
import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import com.jayugg.end_aspected.effect.UnstablePhaseEffect;
import com.jayugg.end_aspected.enchantment.ModEnchantments;
import com.jayugg.end_aspected.entity.ModEntities;
import com.jayugg.end_aspected.item.ModItems;
import com.jayugg.end_aspected.network.EndAspectedNetwork;
import com.jayugg.end_aspected.villager.ModTrades;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EndAspected.MOD_ID)
public class EndAspected
{
    public static final String MOD_ID = "end_aspected";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public EndAspected() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        eventBus.addListener(this::setup);

        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, "end_aspected.toml");

        // Register Effects
        ModEffects.register(eventBus);
        // Register Enchantments
        ModEnchantments.register(eventBus);
        // Register Blocks
        ModBlocks.register(eventBus);
        // Register Items
        ModItems.register(eventBus);
        // Register Entities
        ModEntities.register(eventBus);

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
        EndAspectedNetwork.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("end_aspected", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        /*LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));*/
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)

    @SubscribeEvent
    public void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        Entity entity = event.getEntity();
        // Check for teleport hijacking or jamming effects
        UnstablePhaseEffect.damageTeleporter(entity);
        if (ModConfig.enderTrapJams.get()) {
            EnderTrapBlock.jamEventEntity(event, entity);
        } else {
            EnderTrapBlock.trapEventEntity(event, entity);
        }

    }

}
