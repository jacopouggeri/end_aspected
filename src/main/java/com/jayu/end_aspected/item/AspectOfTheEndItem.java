package com.jayu.end_aspected.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AspectOfTheEndItem extends SwordItem {

    private final int teleportDistance;
    private final long maxTeleports;
    private final long cooldownTime;
    private int teleportsRemaining;
    private long cooldownEndTime;
    private final long cooldownDecayPerTick;
    private static final double TELEPORT_OFFSET = 0.4;

    private static Vector3d getTeleportPosition(Entity entity, double teleportDistance, float partialTicks) {
        // Get the player's eye position and look vector
        Vector3d eyePos = entity.getEyePosition(partialTicks);
        Vector3d lookVec = entity.getLook(partialTicks);
        // Calculate the end position of the ray trace
        Vector3d endPos = eyePos.add(lookVec.scale(teleportDistance));
        // Get the world and perform the ray trace
        World world = entity.world;
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
        // If the ray trace hits a block, check if the block is solid
        if (result.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = (result).getPos();
            BlockState blockState = world.getBlockState(blockPos);

            // If the block is solid, move the end position to the first collision with a solid block along the ray
            if (blockState.isSolid()) {
                endPos = result.getHitVec().subtract(lookVec.normalize().scale(TELEPORT_OFFSET));
            }
        }

        return adjustPosition(entity, endPos);
    }

    private static Vector3d adjustPosition(Entity entity, Vector3d pos) {
        return pos;
    }



    public AspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder, int teleportDistance, int maxTeleports, long cooldownTime) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.teleportDistance = teleportDistance;
        this.maxTeleports = maxTeleports;
        this.cooldownTime = cooldownTime;
        this.teleportsRemaining = maxTeleports;
        this.cooldownEndTime = 0;
        this.cooldownDecayPerTick = maxTeleports / (20*cooldownTime);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            // Check if the cooldown has ended
            if (cooldownEndTime > world.getGameTime()) {
                int remainingSeconds = (int) ((cooldownEndTime - world.getGameTime()) / 20);
                player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown", remainingSeconds), true);
                return ActionResult.resultFail(player.getHeldItem(hand));
            }

            Vector3d teleportPos;

            teleportPos = getTeleportPosition(player, teleportDistance, Minecraft.getInstance().getRenderPartialTicks());

            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);

            player.setPositionAndUpdate(dx, dy, dz);

            // Decrement the teleports remaining
            teleportsRemaining--;


            // Check if teleports remaining is zero and reset cooldown
            if (teleportsRemaining <= 0) {
                cooldownEndTime = world.getGameTime() + (cooldownTime * 20);
                teleportsRemaining += maxTeleports;
            } else if (teleportsRemaining < maxTeleports) {
                teleportsRemaining += cooldownDecayPerTick;
            }
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
}
