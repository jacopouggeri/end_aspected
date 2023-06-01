package net.jayugg.end_aspected;

import net.jayugg.end_aspected.block.EnderTrapBlock;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.tile.ModTileEntities;
import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.entity.render.VoidBatRenderer;
import net.jayugg.end_aspected.potion.BetterBrewingRecipe;
import net.jayugg.end_aspected.potion.ModPotions;
import net.jayugg.end_aspected.effect.UnstablePhaseEffect;
import net.jayugg.end_aspected.enchantment.EnderSlayerEnchantment;
import net.jayugg.end_aspected.enchantment.ModEnchantments;
import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.entity.render.VoidMiteRenderer;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.villager.ModTrades;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
        // Register Tile Entities
        ModTileEntities.register(eventBus);
        // Register Potions
        ModPotions.register(eventBus);

        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModTrades::fillTradeData);
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.WATER, ModItems.ASPECT_SHARD.get(), ModPotions.UNSTABLE_PHASE_POTION.get()));
        });
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.WATER, ModBlocks.VOID_LEAVES.get().asItem(), ModPotions.VOID_SICKNESS_POTION.get()));
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOIDMITE.get(), VoidMiteRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOIDBAT.get(), VoidBatRenderer::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_VEIN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_LEAVES.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_FUNGUS.get(), RenderType.getCutoutMipped());
    }

    @SubscribeEvent
    public void onEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        Entity entity = event.getEntity();
        // Check for teleport hijacking or jamming effects
        UnstablePhaseEffect.damageTeleporter(entity);
        EnderTrapBlock.trapEventEntity(event, entity);
    }

    @SubscribeEvent
    public void getEntityLastDamage(LivingHurtEvent event) {
        EnderSlayerEnchantment.getLastDamageInflicted(event);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
