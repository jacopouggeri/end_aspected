package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class AbstractAspectOfTheEndItem extends SwordItem {
    private static final double TELEPORT_OFFSET = 0.4;
    private int cooldown;

    private final int maxTeleports;
    private int teleportsRemaining;

    private final long teleportDistance;
    private boolean firstRunFlag;

    private boolean enableCooldown;
    private boolean enableLostDurability;
    private int lostDurability;
    private final boolean enableUnstableTeleports;
    private final int unstableTeleportLimit;

    public AbstractAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.firstRunFlag = true;

        // Load config values
        this.teleportDistance = ModConfig.teleportDistance.get();
        this.teleportsRemaining = ModConfig.maxTeleports.get();
        this.maxTeleports = ModConfig.maxTeleports.get();
        this.enableUnstableTeleports = ModConfig.unstableTeleports.get();
        this.unstableTeleportLimit = ModConfig.unstableTeleportsLimit.get();

        // Handle config values for different items
        if (this instanceof AspectOfTheEndItem) {
            this.cooldown = ModConfig.aoteCooldown.get();
            this.enableCooldown = ModConfig.enableAoteCooldown.get();
            this.enableLostDurability = ModConfig.enableAoteLostDurability.get();
            this.lostDurability = ModConfig.aoteLostDurability.get();
        } else if (this instanceof NetherforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.naoteCooldown.get();
            this.enableCooldown = ModConfig.enableNaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableNaoteLostDurability.get();
            this.lostDurability = ModConfig.naoteLostDurability.get();
        } else if (this instanceof DragonforgedAspectOfTheEndItem) {
            this.cooldown = ModConfig.daoteCooldown.get();
            this.enableCooldown = ModConfig.enableDaoteCooldown.get();
            this.enableLostDurability = ModConfig.enableDaoteLostDurability.get();
            this.lostDurability = ModConfig.daoteLostDurability.get();
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


    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world,@Nonnull PlayerEntity player,@Nonnull Hand hand) {
        if (!player.getEntityWorld().isRemote) {
            ItemStack stack = player.getHeldItem(hand);

            if ((teleportsRemaining != maxTeleports) && firstRunFlag) {
                teleportsRemaining = maxTeleports;
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

            if (enableCooldown && player.getCooldownTracker().hasCooldown(this) && !player.isCreative()) {
                spawnCooldownParticles(world, dx, dy, dz);
            }

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);

            player.setPositionAndUpdate(dx, dy, dz);

            // Reduce durability
            if (enableLostDurability) {
                stack.damageItem(lostDurability, player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
            }

            // Remove fall damage
            player.fallDistance = 0;

            stack.getOrCreateTag().putLong("lastUseTime", world.getGameTime());

            // Handle cooldown
            if (enableCooldown && !player.isCreative()) {
                // Decrement the teleports remaining
                long timeSinceLastUse = world.getGameTime() - stack.getOrCreateTag().getLong("lastUseTime");

                int addToCooldownCounter = (int) (1 - Math.max(timeSinceLastUse/cooldown, 1));
                teleportsRemaining = teleportsRemaining - addToCooldownCounter;

                // Check if teleports remaining is zero and reset cooldown
                if (teleportsRemaining <= 0) {
                    // Set new time of last cooldown
                    int cooldownTime = cooldown*20;
                    player.getCooldownTracker().setCooldown(this, cooldownTime);
                    teleportsRemaining = maxTeleports;
                    int cooldownCycles = stack.getOrCreateTag().getInt("cooldownCycles");
                    stack.getOrCreateTag().putInt("cooldownCycles", cooldownCycles + 1);
                }

                if (enableUnstableTeleports && (stack.getOrCreateTag().getInt("cooldownCycles") > unstableTeleportLimit)) {
                    int i = calculateUnstableDuration();
                    player.addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 0));
                    stack.getOrCreateTag().putInt("cooldownCycles", 0);
                }

            }
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public int calculateUnstableDuration() {
        return (int) (cooldown * 20 * ModConfig.unstablePhaseCooldownMultiplier.get());
    }

    public void spawnCooldownParticles(World world, double dx, double dy, double dz) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, dx, dy, dz, 10, 0.5, 0.5, 0.5, 0.0);
            serverWorld.spawnParticle(ParticleTypes.ANGRY_VILLAGER, dx, dy, dz, 1, 0.5, 0.5, 0.5, 0.0);
        }
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

}
