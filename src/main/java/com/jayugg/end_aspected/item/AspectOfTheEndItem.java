package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
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

public class AspectOfTheEndItem extends SwordItem {
    private long cooldownEndTime;
    private int teleportsRemaining;
    private static final double TELEPORT_OFFSET = 0.4;
    private boolean firstRunFlag;
    private int teleportsAfterCooldown;

    public AspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.cooldownEndTime = 0;
        this.teleportsRemaining = ModConfig.maxTeleports.get();
        this.firstRunFlag = true;
        this.teleportsAfterCooldown = 0;
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

    public static boolean doesPlayerOverlap(World world, Entity entity, Vector3d position) {
        AxisAlignedBB entityBoundingBox = entity.getBoundingBox().offset(position).grow(TELEPORT_OFFSET);
        return !world.hasNoCollisions(entity, entityBoundingBox);
    }

    private static Vector3d adjustTeleportPosition(World world, Entity entity, Vector3d teleportPos, float partialTicks) {
        // Check if the player's bounding box overlaps with any solid blocks's bounding box at the teleport position
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

    public void spawnCooldownParticles(World world, double dx, double dy, double dz) {
        ServerWorld serverWorld = (ServerWorld)  world;
        serverWorld.spawnParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, dx, dy, dz, 10, 0.5, 0.5, 0.5, 0.0);
        serverWorld.spawnParticle(ParticleTypes.ANGRY_VILLAGER, dx, dy, dz, 1, 0.5, 0.5, 0.5, 0.0);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world,@Nonnull PlayerEntity player,@Nonnull Hand hand) {
        if (!world.isRemote) {

            if ((teleportsRemaining != ModConfig.maxTeleports.get()) && firstRunFlag) {
                teleportsRemaining = ModConfig.maxTeleports.get();
                //System.out.println(teleportsRemaining);
                firstRunFlag = false;
            }

            Vector3d teleportPos;

            teleportPos = getTeleportPosition(player, ModConfig.teleportDistance.get(), Minecraft.getInstance().getRenderPartialTicks());

            EntityTeleportEvent.EnderEntity teleportEvent = new EntityTeleportEvent.EnderEntity (player, teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            if (teleportEvent.isCanceled()) {
                //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.disrupted"), true);
                return ActionResult.resultFail(player.getHeldItem(hand));
            }

            if (ModConfig.enableAoteCooldown.get() && !player.isCreative()) {
                // Check if the cooldown has ended, if not reduce durability
                if (hasCooldown(cooldownEndTime, world)) {
                    if (ModConfig.enableAoteLostDurability.get()) {
                        ItemStack stack = player.getHeldItem(hand);
                        stack.damageItem(ModConfig.aoteLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                        //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown1"), true);
                    } else {
                        // int remainingSeconds = (int) (cooldownLeft(cooldownEndtime, world) / 20);
                        //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown2", remainingSeconds), true);
                        return ActionResult.resultFail(player.getHeldItem(hand));
                    }
                    spawnCooldownParticles(world, dx, dy, dz);
                }

            }

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);

            player.setPositionAndUpdate(dx, dy, dz);
            player.fallDistance = 0;

            if (ModConfig.enableAoteCooldown.get() && !player.isCreative()) {

                // Decrement the teleports remaining
                teleportsRemaining--;

                // Check if teleports remaining is zero and reset cooldown
                if (teleportsRemaining <= 0) {
                    // Set new time of last cooldown
                    cooldownEndTime = world.getGameTime() + ModConfig.aoteCooldown.get()*20;
                    teleportsRemaining = ModConfig.maxTeleports.get();
                }

                if (ModConfig.unstableTeleports.get()) {
                    if (hasCooldown(cooldownEndTime, world)) {
                        teleportsAfterCooldown += 1;
                        if (teleportsAfterCooldown > ModConfig.unstableTeleportsLimit.get()) {
                            int i = calculateUnstableDuration(cooldownEndTime, world);
                            player.addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 1));
                        }
                    } else {
                        teleportsAfterCooldown = 0;
                    }
                }

            }
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    public int calculateUnstableDuration(long endTime, World world) {
        float cooldown = cooldownLeft(endTime, world);
        return (int) cooldown/2;
    }

    public long cooldownLeft(long endTime, World world) {
        return endTime - world.getGameTime();
    }
    public boolean hasCooldown(long endTime, World world) {
        return cooldownLeft(endTime, world) > 0;
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }
}
