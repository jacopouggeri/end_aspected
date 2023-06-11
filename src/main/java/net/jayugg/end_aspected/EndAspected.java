package net.jayugg.end_aspected;

import net.jayugg.end_aspected.block.EnderTrapBlock;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.IVeinNetworkElement;
import net.jayugg.end_aspected.block.tree.decorator.ModTreeDecorators;
import net.jayugg.end_aspected.block.tree.VoidStemBlock;
import net.jayugg.end_aspected.capabilities.ModCapabilities;
import net.jayugg.end_aspected.capabilities.TeleportData;
import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.entity.render.*;
import net.jayugg.end_aspected.particle.ModParticleTypes;
import net.jayugg.end_aspected.potion.BetterBrewingRecipe;
import net.jayugg.end_aspected.potion.ModPotions;
import net.jayugg.end_aspected.effect.UnstablePhaseEffect;
import net.jayugg.end_aspected.enchantment.EnderSlayerEnchantment;
import net.jayugg.end_aspected.enchantment.ModEnchantments;
import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.villager.ModTrades;
import net.jayugg.end_aspected.world.biome.ModBiomes;
import net.jayugg.end_aspected.world.gen.ModBiomeGeneration;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.IntNBT;
import net.minecraft.potion.Potions;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

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
        // Register Potions
        ModPotions.register(eventBus);
        // Register Entities
        ModEntityTypes.register(eventBus);
        // Register Tree Decorators
        ModTreeDecorators.register(eventBus);
        // Register Biomes
        ModBiomes.register(eventBus);
        // Register Particle Types
        ModParticleTypes.register(eventBus);

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
            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.WATER, ModBlocks.VOID_LEAVES.get().asItem(), ModPotions.VOIDRUE.get()));
        });
        event.enqueueWork(ModBiomeGeneration::generateBiomes);
        event.enqueueWork(ModCapabilities::register);  // Register Capabilities
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOIDMITE.get(), VoidMiteRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOIDBAT.get(), VoidBatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOID_SHADE.get(), VoidShadeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.VOID_BEAST.get(), VoidBeastRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.ASPECTED_ARROW.get(), AspectedArrowRenderer::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_BLOCK.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_VEIN.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_LEAVES.get(), RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(ModBlocks.VOID_FUNGUS.get(), RenderType.getCutoutMipped());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            TeleportData teleportData = new TeleportData();
            event.addCapability(prefix("teleport_data"), new ICapabilitySerializable<IntNBT>() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                    return ModCapabilities.TELEPORT_DATA.orEmpty(cap, LazyOptional.of(() -> teleportData));
                }

                @Override
                public IntNBT serializeNBT() {
                    return IntNBT.valueOf(teleportData.getTeleportCount());
                }

                @Override
                public void deserializeNBT(IntNBT nbt) {
                    teleportData.setTeleportCount(nbt.getInt());
                }
            });
        }
    }

    @SubscribeEvent
    public void onEntityTeleport(EntityTeleportEvent event) {
        if (!(event instanceof EntityTeleportEvent.EnderEntity) && !(event instanceof EntityTeleportEvent.EnderPearl)) {
            return;
        }

        Entity entity = event.getEntity();
        // Check for teleport hijacking or jamming effects
        UnstablePhaseEffect.damageTeleporter(entity);
        EnderTrapBlock.trapEventEntity(event, entity);
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.getCapability(ModCapabilities.TELEPORT_DATA).ifPresent(TeleportData::incrementTeleportCount);
        }
    }

    @SubscribeEvent
    public void voidVeinChargeOnDeath(LivingDeathEvent event) {
        // Get the world the entity is in
        World world = event.getEntity().world;
        if (world.isRemote) {
            return;
        }
        // Get the bounding box around the entity's position
        AxisAlignedBB boundingBox = event.getEntity().getBoundingBox().grow(1.0D);

        // Get the blocks within the bounding box
        List<BlockPos> blockPositions = BlockPos.getAllInBox(boundingBox)
                .map(BlockPos::toImmutable) // Convert to BlockPos
                .filter(pos -> (world.getBlockState(pos).getBlock() == ModBlocks.VOID_VEIN.get() &&
                        world.getBlockState(pos).get(IVeinNetworkElement.CHARGE) < IVeinNetworkElement.MAX_CHARGE)
                        ||
                        world.getBlockState(pos) == ModBlocks.VOID_STEM.get().getDefaultState().with(VoidStemBlock.ALIVE, true))
                .collect(Collectors.toList());

        if (blockPositions.isEmpty()) {
            return;
        }

        LOGGER.info(blockPositions);

        // Choose a block in the list at random
        int health = (int) event.getEntityLiving().getMaxHealth();
        BlockPos blockPos = blockPositions.get(world.rand.nextInt(blockPositions.size()));
        // Update the block states
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.matchesBlock(ModBlocks.VOID_VEIN.get())) {
            world.setBlockState(blockPos, IVeinNetworkElement.addChargeFromHealth(blockState, health));
            world.addParticle(ModParticleTypes.VOID_CHARGE.get(), blockPos.getX() + 0.5D, blockPos.getY() + 0.75D, blockPos.getZ() + 0.5D,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @SubscribeEvent
    public void voidVeinSpawnOnDeath(LivingDeathEvent event) {
        World world = event.getEntity().world;
        if (world.isRemote) {
            return;
        }
        boolean voidRueActive = event.getEntityLiving().isPotionActive(ModEffects.VOIDRUE.get());
        if (voidRueActive) {
            LivingEntity livingEntity = event.getEntityLiving();
            BlockPos entityPos = livingEntity.getPosition();
            double health = livingEntity.getMaxHealth();
            int radius =  health > 20 ? 2 * (int) Math.log10(livingEntity.getMaxHealth()/2) : 1;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (dx * dx + dy * dy + dz * dz <= radius * radius) { // only affect blocks within the sphere
                            BlockPos pos = entityPos.add(dx, dy, dz);
                            if (world.isAirBlock(pos) && world.getBlockState(pos.down()).isSolid()) {
                                world.setBlockState(pos, ModBlocks.VOID_VEIN.get().getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void getEntityLastDamage(LivingHurtEvent event) {
        EnderSlayerEnchantment.getLastDamageInflicted(event);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
