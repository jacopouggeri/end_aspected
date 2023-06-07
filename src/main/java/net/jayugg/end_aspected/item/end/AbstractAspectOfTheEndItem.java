package net.jayugg.end_aspected.item.end;

import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.entity.VoidMiteEntity;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.util.FormatUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.jayugg.end_aspected.EndAspected.LOGGER;
import static net.minecraft.util.Direction.DOWN;
import static net.minecraft.util.Direction.UP;

public abstract class AbstractAspectOfTheEndItem extends SwordItem {
    private double cooldown;

    private int maxTeleports;

    private long teleportDistance;
    private boolean firstRunFlag;

    private boolean enableCooldown;
    private boolean enableLostDurability;
    private int lostDurability;
    private boolean enableUnstableTeleports;
    private int unstableTeleportLimit;
    private double voidlingSpawnChance;

    public String TELEPORTS_REMAINING_TAG = "teleports_remaining";
    public String COOLDOWN_CYCLES_TAG = "cooldownCycles";
    public String LAST_USE_TAG = "lastUseTime";
    public TranslationTextComponent tooltip_lore;

    public boolean configLoaded;

    public AbstractAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.firstRunFlag = true;
        this.configLoaded = false;
    }

    public void loadConfigIfNotLoaded(){
        if (!this.configLoaded) {
            // Load config values
            this.teleportDistance = ModConfig.teleportDistance.get();
            this.maxTeleports = ModConfig.maxTeleports.get();
            this.enableUnstableTeleports = ModConfig.unstableTeleports.get();
            this.unstableTeleportLimit = ModConfig.unstableTeleportsLimit.get();
            this.voidlingSpawnChance = ModConfig.voidlingSpawnChance.get();

            // Handle config values for different items
            this.cooldown = loadCooldownConfig();
            this.enableCooldown = loadEnableCooldownConfig();
            this.enableLostDurability = loadEnableLostDurabilityConfig();
            this.lostDurability = loadLostDurabilityConfig();
            this.tooltip_lore = getLore();

            configLoaded = true;
        }
    }

    public abstract Double loadCooldownConfig();
    public abstract boolean loadEnableCooldownConfig();
    public abstract boolean loadEnableLostDurabilityConfig();
    public abstract int loadLostDurabilityConfig();
    public abstract TranslationTextComponent getLore();

    public int getTeleportsRemaining(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TELEPORTS_REMAINING_TAG);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world,@Nonnull PlayerEntity player,@Nonnull Hand hand) {
        loadConfigIfNotLoaded();
        if (!player.getEntityWorld().isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if ((getTeleportsRemaining(stack) != maxTeleports) && firstRunFlag) {
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                firstRunFlag = false;
            }
            EntityTeleportEvent.EnderEntity teleportEvent = new EntityTeleportEvent.EnderEntity (player, player.getPosX(), player.getPosY(), player.getPosZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);
            // Handle teleport jamming events
            if (teleportEvent.isCanceled()) {
                return ActionResult.resultFail(player.getHeldItem(hand));
            }

            handleTeleport(player);
            // Remove fall damage
            player.fallDistance = 0;

            soundAndParticles(world, player);
            // Reduce durability
            if (enableLostDurability) {
                stack.damageItem(lostDurability, player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
            }
            // Set last use
            stack.getOrCreateTag().putLong(LAST_USE_TAG, world.getGameTime());

            // Handle cooldown
            if (enableCooldown && !player.isCreative()) {
                LOGGER.info("HANDLING COOLDOWN");
                // Decrement the teleports remaining
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, stack.getOrCreateTag().getInt(TELEPORTS_REMAINING_TAG) -  1);

                // Check if teleports remaining is zero and reset cooldown
                if (getTeleportsRemaining(stack) <= 0) {
                    LOGGER.info("COOLDOWN TRIGGERED");
                    // Set new time of last cooldown
                    int cooldownTime = (int) (cooldown * 20);
                    player.getCooldownTracker().setCooldown(this, cooldownTime);
                    stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                    int cooldownCycles = stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG);
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, cooldownCycles + 1);
                }

                if (enableUnstableTeleports && (stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG) > unstableTeleportLimit)) {
                    int i = calculateUnstableDuration();
                    player.addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 0));
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, 0);
                }

            }
        }
        // Spawn Voidling
        spawnVoidling(world, player);
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    private void spawnVoidling(World world, PlayerEntity player) {
        if (random.nextFloat() > 1 - voidlingSpawnChance) {
            VoidMiteEntity voidling = new VoidMiteEntity(ModEntityTypes.VOIDMITE.get(), world);
            voidling.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
            world.addEntity(voidling);
        }
    }

    private void soundAndParticles(World world, PlayerEntity player) {
        double dx = player.getPosX();
        double dy = player.getPosY();
        double dz = player.getPosZ();
        BlockPos playerPos = new BlockPos(dx, dy, dz);

        // Play the Enderman sound at the destination position
        world.playSound(null, playerPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        // Spawn the Enderman particle effect at the destination position
        ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);
    }

    private void handleTeleport(LivingEntity user) {
        // Get the player's eye position and look vector
        Vector3d eyePos = user.getEyePosition(1.0f);
        Vector3d lookVec = user.getLook(1.0f);

        // Calculate the end position of the ray trace
        Vector3d teleportPos = eyePos.add(lookVec.scale(teleportDistance)).add(0, -1 * user.getEyeHeight(), 0);

        // Get the world and perform the ray trace
        World world = user.world;
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eyePos, teleportPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, user));

        Vector3d finalTeleportPos = teleportPos;

        if (result.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = result.getPos();
            Direction face = result.getFace();
            BlockPos adjustedPos = hitPos.offset(face).offset(DOWN);
            finalTeleportPos = new Vector3d(adjustedPos.getX() + 0.5, adjustedPos.getY() + 1.0, adjustedPos.getZ() + 0.5);
        }
        finalTeleportPos = adjustTeleportPosition(finalTeleportPos, user, world);
        // Teleport the player
        user.setPositionAndUpdate(finalTeleportPos.x, finalTeleportPos.y, finalTeleportPos.z);
        if (isPlayerInOneBlockSpace(user.getPositionVec(), world)) {
            user.setPositionAndUpdate(user.getPosX(), user.getPosY() + 1, user.getPosZ());
            user.setPose(Pose.SWIMMING);
        }
    }

    private boolean isPlayerInOneBlockSpace(Vector3d teleportPos, World world){
        BlockPos blockPos = new BlockPos(teleportPos.x, teleportPos.y, teleportPos.z);
        BlockPos abovePos = blockPos.offset(UP).offset((UP));
        BlockPos belowPos = blockPos.offset(DOWN);

        boolean aboveSolid = world.getBlockState(abovePos).isSolid();
        boolean belowSolid = world.getBlockState(belowPos).isSolid();

        LOGGER.info("aboveSolid: " + aboveSolid);
        LOGGER.info("belowSolid: " + belowSolid);
        return aboveSolid && belowSolid;
    }

    public Vector3d adjustTeleportPosition(Vector3d teleportPos, LivingEntity user, World world) {
        for (int i = 1; i <= 2; i++) {
            teleportPos = raiseFeet(teleportPos, world);
        }
        for (int i = 1; i <= 2; i++) {
            teleportPos = lowerHead(teleportPos, user, world);
        }
        return teleportPos;
    }

    public Vector3d lowerHead(Vector3d teleportPos, LivingEntity user, World world) {
        BlockPos headPos = new BlockPos(teleportPos.getX(), teleportPos.getY() + user.getHeight(), teleportPos.getZ());
        if (world.getBlockState(headPos).isSolid()) {
            // If the head would be inside a block, adjust the teleport position up by the difference between the block bottom and the player's head
            double headOverlap = teleportPos.getY() + user.getHeight() - headPos.getY();
            teleportPos = teleportPos.add(0, -headOverlap, 0);
        }
        return teleportPos;
    }

    public Vector3d raiseFeet(Vector3d teleportPos, World world) {
        BlockPos feetPos = new BlockPos(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
        if (world.getBlockState(feetPos).isSolid()) {
            // If the feet would be inside a block, adjust the teleport position up by the difference between the block top and the player's feet
            double blockY = feetPos.getY() + world.getBlockState(feetPos).getShape(world, feetPos).getEnd(Direction.Axis.Y);
            double feetOverlap = blockY - teleportPos.getY();
            teleportPos = teleportPos.add(0, feetOverlap, 0);
        }
        return teleportPos;
    }


    public int calculateUnstableDuration() {
        return (int) (cooldown * 20 * ModConfig.unstablePhaseCooldownMultiplier.get());
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() == ModItems.ASPECT_SHARD.get();
    }

    // Update the last use time to decrease cooldown counter when not in use
    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean selected) {
        loadConfigIfNotLoaded();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!world.isRemote && enableCooldown && !player.isCreative()) {
                CompoundNBT tag = stack.getOrCreateTag();

                long lastUse = tag.getLong(LAST_USE_TAG);
                long currentTime = world.getGameTime();

                if (currentTime - lastUse >= 20L* cooldown && tag.contains(TELEPORTS_REMAINING_TAG)) {
                    int usesLeft = tag.getInt(TELEPORTS_REMAINING_TAG);
                    if (usesLeft < maxTeleports) {
                        tag.putInt(TELEPORTS_REMAINING_TAG, usesLeft + 1);
                    }
                }

                // Reset cooldown cycles if the tool isn't being used
                if (currentTime - lastUse >= 20L * cooldown * 5L) {
                    int cycles = tag.getInt(COOLDOWN_CYCLES_TAG);
                    if (cycles > 0) {
                        stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, cycles - 1);
                    }
                }
            }
        }
    }

    public long getTeleportDistance() {
        return teleportDistance;
    }

    public double getCooldown() {
        return cooldown;
    }

    @Override
    public void addInformation(@Nonnull ItemStack item, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        loadConfigIfNotLoaded();
        if (Screen.hasShiftDown()) {

            String reachString = FormatUtil.formatNumber(this.getTeleportDistance());
            String cooldownString = FormatUtil.formatNumber(this.getCooldown());
            TranslationTextComponent ability = new TranslationTextComponent("tooltip.end_aspected.aspect_of_the_end_ability");
            TranslationTextComponent reach = new TranslationTextComponent("tooltip.end_aspected.teleport_reach", "§2" + reachString + "§r");
            TranslationTextComponent cooldown = new TranslationTextComponent("tooltip.end_aspected.cooldown", "§2" + cooldownString + "§r");

            TranslationTextComponent stats = new TranslationTextComponent("tooltip.end_aspected.stats");

            // Handle no cooldown in config
            if (enableCooldown) {
                TranslationTextComponent message_final = (TranslationTextComponent) ability
                        .appendSibling(tooltip_lore)
                        .appendSibling(stats)
                        .appendSibling(reach)
                        .appendString("\n")
                        .appendSibling(cooldown);

                tooltip.add(message_final);
            } else {
                TranslationTextComponent message_final_2 = (TranslationTextComponent) ability
                        .appendSibling(tooltip_lore)
                        .appendSibling(stats)
                        .appendSibling(reach);
                tooltip.add(message_final_2);
            }

        } else {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.more"));
        }
    }

}
