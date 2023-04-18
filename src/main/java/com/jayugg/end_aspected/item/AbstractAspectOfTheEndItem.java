package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import com.jayugg.end_aspected.utils.FormatUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
    public TranslationTextComponent tooltip_lore;

    public AbstractAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
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
            this.tooltip_lore = new TranslationTextComponent("tooltip.end_aspected.aspect_of_the_end_shift");
        } else if (this instanceof NetherforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.naoteCooldown.get();
            this.enableCooldown = ModConfig.enableNaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableNaoteLostDurability.get();
            this.lostDurability = ModConfig.naoteLostDurability.get();
            this.tooltip_lore = new TranslationTextComponent("tooltip.end_aspected.netherforged_aspect_of_the_end_shift");
        } else if (this instanceof DragonforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.daoteCooldown.get();
            this.enableCooldown = ModConfig.enableDaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableDaoteLostDurability.get();
            this.lostDurability = ModConfig.daoteLostDurability.get();
            this.tooltip_lore = new TranslationTextComponent("tooltip.end_aspected.dragonforged_aspect_of_the_end_shift");
        }
    }

    public static Vector3d getTeleportPosition(Entity entity, double teleportDistance, float partialTicks) {
        // Get the player's eye position and look vector
        Vector3d eyePos = entity.getEyePosition(partialTicks);
        Vector3d lookVec = entity.getLook(partialTicks);

        // Calculate the end position of the ray trace
        Vector3d teleportPos = eyePos.add(lookVec.scale(teleportDistance));
        // Get the world and perform the ray trace
        World world = entity.world;
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eyePos, teleportPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));

        // If the ray trace hits a block, check if the block is solid
        if (result.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = (result).getPos();
            BlockState blockState = world.getBlockState(blockPos);

            // If the block is solid, move the end position to the first collision with a solid block along the ray
            if (blockState.isSolid()) {
                teleportPos = result.getHitVec().subtract(lookVec.normalize().scale(TELEPORT_OFFSET)); //.subtract(0, entity.getEyeHeight(), 0)
            }
        }

        return adjustTeleportPosition(world, entity, teleportPos, partialTicks);
    }

    private static boolean doesPlayerOverlap(World world, Entity entity, Vector3d position) {
        AxisAlignedBB entityBoundingBox = entity.getBoundingBox().offset(position).grow(TELEPORT_OFFSET);
        return !world.hasNoCollisions(entity, entityBoundingBox);
    }

    private static Vector3d adjustTeleportPosition(World world, Entity entity, Vector3d teleportPos, float partialTicks) {

        // Check if the player's bounding box overlaps with any solid block's bounding box at the teleport position
        if (doesPlayerOverlap(world, entity, teleportPos)) {

            // If there is an overlap, raytrace back to find a valid teleport position
            Vector3d rayTraceStart = entity.getLook(partialTicks);
            Vector3d rayTraceEnd = teleportPos;
            Vector3d rayTraceDir = rayTraceEnd.subtract(rayTraceStart).normalize();
            double rayTraceDist = rayTraceEnd.distanceTo(rayTraceStart);
            for (double i = rayTraceDist; i >= 0; i -= 0.1) {
                Vector3d rayTracePos = rayTraceStart.add(rayTraceDir.scale(i));
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
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world,@Nonnull PlayerEntity player,@Nonnull Hand hand) {
        if (!player.getEntityWorld().isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if ((getTeleportsRemaining(stack) != maxTeleports) && firstRunFlag) {
                stack.getOrCreateTag().putInt(TELEPORTS_REMAINING_TAG, maxTeleports);
                firstRunFlag = false;
            }

            Vector3d teleportPos;
            teleportPos = getTeleportPosition(player, teleportDistance, 1.0f);

            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            EntityTeleportEvent.EnderEntity teleportEvent = new EntityTeleportEvent.EnderEntity (player, teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            // Handle teleport jamming events
            if (teleportEvent.isCanceled()) {
                return ActionResult.resultFail(player.getHeldItem(hand));
            }

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);
            // Teleport the player
            player.setPositionAndUpdate(dx, dy, dz);
            // Remove fall damage
            player.fallDistance = 0;
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
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public int calculateUnstableDuration() {
        return (int) (cooldown * 20 * ModConfig.unstablePhaseCooldownMultiplier.get());
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    // Update the last use time to decrease cooldown counter when not in use
    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean selected) {
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
                        tag.putLong(TELEPORTS_REMAINING_TAG, currentTime);
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
    public void addInformation(@Nonnull ItemStack item, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        if (Screen.hasShiftDown()) {

            String reachString = FormatUtils.formatNumber(this.getTeleportDistance());
            String cooldownString = FormatUtils.formatNumber(this.getCooldown());
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
