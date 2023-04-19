package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import com.jayugg.end_aspected.utils.FormatUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.jayugg.end_aspected.EndAspected.LOGGER;

public class AbstractAspectOfTheEndItem extends SwordItem {
    private static final double TELEPORT_OFFSET = 0.4;
    private double cooldown;

    private final int maxTeleports;

    private final long teleportDistance;
    private boolean firstRunFlag;

    private boolean enableCooldown;
    private boolean enableLostDurability;
    private int lostDurability;
    private final boolean enableUnstableTeleports;
    private final int unstableTeleportLimit;

    public String TELEPORTS_REMAINING_TAG = "teleports_remaining";
    public String COOLDOWN_CYCLES_TAG = "cooldownCycles";
    public String LAST_USE_TAG = "lastUseTime";
    protected final RandomSource random = RandomSource.create();
    public Component tooltip_lore;

    public AbstractAspectOfTheEndItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.firstRunFlag = true;

        // Load config values
        this.teleportDistance = ModConfig.teleportDistance.get();
        this.maxTeleports = ModConfig.maxTeleports.get();
        this.enableUnstableTeleports = ModConfig.unstableTeleports.get();
        this.unstableTeleportLimit = ModConfig.unstableTeleportsLimit.get();

        // Handle config values for different items
        if (this instanceof AspectOfTheEndItem) {
            this.cooldown = ModConfig.aoteCooldown.get();
            this.enableCooldown = ModConfig.enableAoteCooldown.get();
            this.enableLostDurability = ModConfig.enableAoteLostDurability.get();
            this.lostDurability = ModConfig.aoteLostDurability.get();
            this.tooltip_lore = Component.translatable("tooltip.end_aspected.aspect_of_the_end_shift");
        } else if (this instanceof NetherforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.naoteCooldown.get();
            this.enableCooldown = ModConfig.enableNaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableNaoteLostDurability.get();
            this.lostDurability = ModConfig.naoteLostDurability.get();
            this.tooltip_lore = Component.translatable("tooltip.end_aspected.netherforged_aspect_of_the_end_shift");
        } else if (this instanceof DragonforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.daoteCooldown.get();
            this.enableCooldown = ModConfig.enableDaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableDaoteLostDurability.get();
            this.lostDurability = ModConfig.daoteLostDurability.get();
            this.tooltip_lore = Component.translatable("tooltip.end_aspected.dragonforged_aspect_of_the_end_shift");
        }
    }

    public static Vec3 getTeleportPosition(Entity entity, double teleportDistance, float partialTicks) {
        // Get the player's eye position and look vector
        Vec3 eyePos = entity.getEyePosition(partialTicks);
        Vec3 lookVec = entity.getLookAngle();

        // Calculate the end position of the ray trace
        Vec3 teleportPos = eyePos.add(lookVec.scale(teleportDistance));

        // Get the world and perform the ray trace
        Level world = entity.level;
        BlockHitResult result = world.clip(new ClipContext(eyePos, teleportPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        // If the ray trace hits a block, check if the block is solid
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = result.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);

            // If the block is solid, move the end position to the first collision with a solid block along the ray
            if (blockState.canOcclude()) {
                teleportPos = result.getLocation().subtract(lookVec.normalize().scale(TELEPORT_OFFSET));
            }
        }

        return adjustTeleportPosition(world, entity, teleportPos);
    }


    private static boolean doesPlayerOverlap(Level world, Entity entity, Vec3 position) {
        AABB entityBoundingBox = entity.getBoundingBox().move(position).inflate(TELEPORT_OFFSET);
        return !world.noCollision(entity, entityBoundingBox);
    }

    private static Vec3 adjustTeleportPosition(Level world, Entity entity, Vec3 teleportPos) {

        // Check if the player's bounding box overlaps with any solid block's bounding box at the teleport position
        if (doesPlayerOverlap(world, entity, teleportPos)) {

            // If there is an overlap, raytrace back to find a valid teleport position
            Vec3 rayTraceStart = entity.getLookAngle();
            Vec3 rayTraceEnd = teleportPos;
            Vec3 rayTraceDir = rayTraceEnd.subtract(rayTraceStart).normalize();
            double rayTraceDist = rayTraceEnd.distanceTo(rayTraceStart);
            for (double i = rayTraceDist; i >= 0; i -= 0.1) {
                Vec3 rayTracePos = rayTraceStart.add(rayTraceDir.scale(i));
                if (!doesPlayerOverlap(world, entity, rayTracePos)) {
                    teleportPos = rayTracePos;
                    break;
                }
            }
        }
        // Return the found safe teleport position
        return teleportPos;
    }

    public int getTeleportsRemaining(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TELEPORTS_REMAINING_TAG);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!player.level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if ((getTeleportsRemaining(stack) != maxTeleports) && firstRunFlag) {
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                firstRunFlag = false;
            }

            Vec3 teleportPos;
            teleportPos = getTeleportPosition(player, teleportDistance, 1.0f);

            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            EntityTeleportEvent teleportEvent = new EntityTeleportEvent.EnderEntity (player, teleportPos.x, teleportPos.y, teleportPos.z);
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            // Handle teleport jamming events
            if (teleportEvent.isCanceled()) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            // Spawn the Enderman particle effect at the destination position
            ((ServerLevel) world).sendParticles(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);
            // Teleport the player
            player.setPos(teleportPos);
            // Remove fall damage
            player.fallDistance = 0;
            // Reduce durability
            if (enableLostDurability) {
                stack.setDamageValue(stack.getDamageValue() + lostDurability); // reduce durability by 1
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
                    player.getCooldowns().addCooldown(this, cooldownTime);
                    stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                    int cooldownCycles = stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG);
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, cooldownCycles + 1);
                }

                if (enableUnstableTeleports && (stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG) > unstableTeleportLimit)) {
                    int i = calculateUnstableDuration();
                    player.addEffect(new MobEffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 0));
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, 0);
                }

            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public int calculateUnstableDuration() {
        return (int) (cooldown * 20 * ModConfig.unstablePhaseCooldownMultiplier.get());
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    // Update the last use time to decrease cooldown counter when not in use
    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            if (!world.isClientSide() && enableCooldown && !player.isCreative()) {
                CompoundTag tag = stack.getOrCreateTag();

                long lastUse = tag.getLong(LAST_USE_TAG);
                long currentTime = world.getGameTime();

                if (currentTime - lastUse >= 20L* cooldown && tag.contains(TELEPORTS_REMAINING_TAG)) {
                    int usesLeft = tag.getInt(TELEPORTS_REMAINING_TAG);
                    if (usesLeft < maxTeleports) {
                        tag.putInt(TELEPORTS_REMAINING_TAG, usesLeft + 1);
                        tag.putLong(LAST_USE_TAG, currentTime);
                    }
                }

                // Reset cooldown cycles if the item isn't being used
                if (currentTime - lastUse >= 20L * cooldown * 5L) {
                    int cycles = tag.getInt(COOLDOWN_CYCLES_TAG);
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, cycles - 1);
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
    public void appendHoverText(@Nonnull ItemStack item, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (Screen.hasShiftDown()) {

            String reachString = FormatUtils.formatNumber(this.getTeleportDistance());
            String cooldownString = FormatUtils.formatNumber(this.getCooldown());
            Component ability = Component.translatable("tooltip.end_aspected.aspect_of_the_end_ability");
            Component reach = Component.translatable("tooltip.end_aspected.teleport_reach", "§2" + reachString + "§r");
            Component cooldown = Component.translatable("tooltip.end_aspected.cooldown", "§2" + cooldownString + "§r");

            Component stats = Component.translatable("tooltip.end_aspected.stats");

            tooltip.add(ability);
            tooltip.add(tooltip_lore);
            tooltip.add(stats);
            tooltip.add(reach);

            // Handle no cooldown in config
            if (enableCooldown) {
                tooltip.add(Component.literal("\n"));
                tooltip.add(cooldown);
            }

        } else {
            tooltip.add(Component.translatable("tooltip.end_aspected.more"));
        }
    }

}
