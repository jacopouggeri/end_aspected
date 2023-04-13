package com.jayu.end_aspected.item;

import com.jayu.end_aspected.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class AspectOfTheEndItem extends SwordItem {

    private long cooldownTime;
    private long teleportsRemaining;

    private boolean haveCooldown;

    private long cooldownEndTime;
    private static final double TELEPORT_OFFSET = 0.4;

    public AspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        if (tier == ItemTier.DIAMOND) {
            this.cooldownTime = ModConfig.aoteCooldown.get();
        } else if (tier == ItemTier.NETHERITE) {
            this.cooldownTime = ModConfig.aoteCooldown.get()/2;
        }
        this.teleportsRemaining = ModConfig.maxTeleports.get();
        this.cooldownEndTime = 0;
        if (attackDamageIn == 3) {
            this.haveCooldown = ModConfig.enableAoteCooldown.get();
        } else if (attackDamageIn == 4) {
            this.haveCooldown = false;
        }
    }

    public static boolean doesPlayerOverlap(World world, Entity entity, Vector3d position) {
        AxisAlignedBB entityBoundingBox = entity.getBoundingBox().offset(position).grow(TELEPORT_OFFSET);
        return !world.hasNoCollisions(entity, entityBoundingBox);
    }

    private static Vector3d getTeleportPosition(Entity entity, double teleportDistance, float partialTicks) {
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

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world,@Nonnull PlayerEntity player,@Nonnull Hand hand) {
        if (!world.isRemote) {
            Vector3d teleportPos;

            teleportPos = getTeleportPosition(player, ModConfig.teleportDistance.get(), Minecraft.getInstance().getRenderPartialTicks());

            EntityTeleportEvent.EnderEntity teleportEvent = new EntityTeleportEvent.EnderEntity (player, teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            if (teleportEvent.isCanceled()) {
                player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.trapped"), true);
                return ActionResult.resultFail(player.getHeldItem(hand));
            }


            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);

            player.setPositionAndUpdate(dx, dy, dz);

            if (haveCooldown) {

                // Decrement the teleports remaining
                teleportsRemaining--;


                // Check if the cooldown has ended, if not reduce durability
                if (cooldownEndTime > world.getGameTime()) {
                    ItemStack stack = player.getHeldItem(hand);
                    stack.damageItem(ModConfig.aoteLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                    player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown"), true);
                }
                System.out.println("Teleports remaining: " + teleportsRemaining);

                // Check if teleports remaining is zero and reset cooldown
                if (teleportsRemaining <= 0) {
                    // Set new time of last cooldown
                    cooldownEndTime = world.getGameTime() + cooldownTime*20;
                    teleportsRemaining = ModConfig.maxTeleports.get();
                }

            }
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        // The repair item must be an iron ingot
        return repair.getItem() instanceof AspectShardItem;
    }
}
