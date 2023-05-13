package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.utils.FormatUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.core.Direction.DOWN;
import static net.jayugg.end_aspected.EndAspected.LOGGER;

public abstract class AbstractAspectOfTheEndItem extends SwordItem {
    private double cooldown;
    public boolean configLoaded;
    private int maxTeleports;
    private boolean firstRunFlag;

    private boolean enableCooldown;
    private boolean enableLostDurability;
    private int lostDurability;
    private long teleportDistance;
    private boolean enableUnstableTeleports;

    public String TELEPORTS_REMAINING_TAG = "teleports_remaining";
    public String COOLDOWN_CYCLES_TAG = "cooldownCycles";
    public String LAST_USE_TAG = "lastUseTime";
    protected final RandomSource random = RandomSource.create();
    public Component tooltip_lore;
    private int unstableTeleportLimit;

    public AbstractAspectOfTheEndItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
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
    public abstract Component getLore();


    public int getTeleportsRemaining(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TELEPORTS_REMAINING_TAG);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        loadConfigIfNotLoaded();
        if (!player.level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if ((getTeleportsRemaining(stack) != maxTeleports) && firstRunFlag) {
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                firstRunFlag = false;
            }
            EntityTeleportEvent teleportEvent = new EntityTeleportEvent.EnderEntity (player, player.getX(), player.getY(), player.getZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            // Handle teleport jamming events
            if (teleportEvent.isCanceled()) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            // Teleport player
            handleTeleport(player);
            // Remove fall damage
            player.fallDistance = 0;

            // Play sound and spawn particles
            soundAndParticles(world, player);

            // Reduce durability
            if (enableLostDurability) {
                stack.setDamageValue(stack.getDamageValue() + lostDurability); // reduce durability by 1
            }
            // Set last use
            stack.getOrCreateTag().putLong(LAST_USE_TAG, world.getGameTime());

            // Handle cooldown
            if (enableCooldown && !player.isCreative()) {
                //LOGGER.info("HANDLING COOLDOWN");
                // Decrement the teleports remaining
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, stack.getOrCreateTag().getInt(TELEPORTS_REMAINING_TAG) -  1);

                // Check if teleports remaining is zero and reset cooldown
                if (getTeleportsRemaining(stack) <= 0) {
                    //LOGGER.info("COOLDOWN TRIGGERED");
                    // Set new time of last cooldown
                    int cooldownTime = (int) (cooldown * 20);
                    player.getCooldowns().addCooldown(this, cooldownTime);
                    stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                    int cooldownCycles = stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG);
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, cooldownCycles + 1);
                }

                if (enableUnstableTeleports && (stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG) > unstableTeleportLimit)) {
                    int i = calculateUnstableDuration();
                    player.addEffect(new MobEffectInstance(ModEffects.UNSTABLE_PHASE.get(), i));
                    stack.getOrCreateTag().putInt(COOLDOWN_CYCLES_TAG, 0);
                }

            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private void handleTeleport(LivingEntity user) {
        // Get the player's eye position and look vector
        Vec3 eyePos = user.getEyePosition();
        Vec3 lookVec = user.getLookAngle();

        // Calculate the end position of the ray trace
        Vec3 teleportPos = eyePos.add(lookVec.scale(teleportDistance)).add(0, -1*user.getEyeHeight(), 0);

        // Get the world and perform the ray trace
        Level world = user.getLevel();
        BlockHitResult result = world.clip(new ClipContext(eyePos, teleportPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));

        Vec3 finalTeleportPos = teleportPos;

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = result.getBlockPos();
            Direction face = result.getDirection();
            BlockPos adjustedPos = hitPos.relative(face).relative(DOWN);
            finalTeleportPos = new Vec3(adjustedPos.getX() + 0.5, adjustedPos.getY() + 1.0, adjustedPos.getZ() + 0.5);
        }
        finalTeleportPos = adjustTeleportPosition(finalTeleportPos, user, world);
        // Teleport the player
        user.teleportTo(finalTeleportPos.x, finalTeleportPos.y, finalTeleportPos.z);
        // Check if the player is in a 1-block tall space and make them crawl
        if (isPlayerInOneBlockSpace(user.getPosition(1.0f), world)) {
            user.teleportTo(user.getX(), user.getY() + 1, user.getZ());
            user.setPose(Pose.SWIMMING);
        }
    }

    private boolean isPlayerInOneBlockSpace(Vec3 teleportPos, Level world) {
        BlockPos blockPos = new BlockPos(teleportPos.x(), teleportPos.y(), teleportPos.z());
        BlockPos abovePos = blockPos.above().above();
        BlockPos belowPos = blockPos.below();

        boolean aboveSolid = world.getBlockState(abovePos).canOcclude();
        boolean belowSolid = world.getBlockState(belowPos).canOcclude();

        LOGGER.info("aboveSolid: " + aboveSolid);
        LOGGER.info("belowSolid: " + belowSolid);
        return aboveSolid && belowSolid;
    }


    public Vec3 adjustTeleportPosition(Vec3 teleportPos, LivingEntity user, Level world) {
        for (int i = 1; i <= 2; i++) {
            teleportPos = raiseFeet(teleportPos, world);
        }
        for (int i = 1; i <= 2; i++) {
            teleportPos = lowerHead(teleportPos, user, world);
        }
        return teleportPos;
    }

    public Vec3 lowerHead(Vec3 teleportPos, LivingEntity user, Level world) {
        BlockPos headPos = new BlockPos(teleportPos.x, teleportPos.y + user.getBbHeight(), teleportPos.z);
        if (world.getBlockState(headPos).canOcclude()) {
            // If the head would be inside a block, adjust the teleport position up by the difference between the block bottom and the player's head
            double headOverlap = teleportPos.y + user.getBbHeight() - headPos.getY();
            teleportPos = teleportPos.add(0, -headOverlap, 0);
        }
        return teleportPos;
    }

    public Vec3 raiseFeet(Vec3 teleportPos, Level world) {
        BlockPos feetPos = new BlockPos(teleportPos.x, teleportPos.y, teleportPos.z);
        if (world.getBlockState(feetPos).canOcclude()) {
            // If the feet would be inside a block, adjust the teleport position up by the difference between the block top and the player's feet
            double blockY = feetPos.getY() + world.getBlockState(feetPos).getShape(world, feetPos).max(Direction.Axis.Y);
            double feetOverlap = blockY - teleportPos.y();
            teleportPos = teleportPos.add(0, feetOverlap, 0);
        }
        return teleportPos;
    }

    private void soundAndParticles(Level world, LivingEntity user) {
        double x = user.getX();
        double y = user.getY();
        double z = user.getZ();
        BlockPos destPos = new BlockPos(x, y, z);

        // Play the Enderman sound at the destination position
        world.playSound(null, destPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        // Spawn the Enderman particle effect at the destination position
        ((ServerLevel) world).sendParticles(ParticleTypes.PORTAL, x, y, z, 50, 0.5, 0.5, 0.5, 0.0);

    }


    public int calculateUnstableDuration() {
        return (int) (cooldown * ModConfig.unstablePhaseCooldownMultiplier.get());
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    // Update the last use time to decrease cooldown counter when not in use
    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int slot, boolean selected) {
        loadConfigIfNotLoaded();
        if (entity instanceof Player player) {
            if (!world.isClientSide() && enableCooldown && !player.isCreative()) {

                CompoundTag tag = stack.getOrCreateTag();

                long lastUse = tag.getLong(LAST_USE_TAG);
                long currentTime = world.getGameTime();

                //LOGGER.info("AOTE LAST USE: " + (int) lastUse / 20);

                // Handle cooldown decay
                if (currentTime - lastUse >= 20L*cooldown) {
                    // If more than the cooldown has passed from last use
                    int usesLeft = tag.getInt(TELEPORTS_REMAINING_TAG);
                    // Check if teleports remaining is less than max
                    if (usesLeft < maxTeleports) {
                        // Update teleports remaining
                        tag.putInt(TELEPORTS_REMAINING_TAG, usesLeft + 1);
                    }
                }

                //LOGGER.info("AOTE CYCLE: " + stack.getOrCreateTag().getInt(COOLDOWN_CYCLES_TAG));

                // Reduce cooldown cycles if the item isn't being used for five times as long as the cooldown
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
    public void appendHoverText(@Nonnull ItemStack item, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        loadConfigIfNotLoaded();
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
                tooltip.add(cooldown);
            }

        } else {
            tooltip.add(Component.translatable("tooltip.end_aspected.more"));
        }
    }

}
