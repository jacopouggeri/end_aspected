package com.jayugg.end_aspected.block;

import com.jayugg.end_aspected.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

public class EnderTrapBlock extends Block {

    public EnderTrapBlock(Properties builder) {
        super(builder);
    }

    public static void trapEventEntity(EntityTeleportEvent.EnderEntity event, Entity entity) {
        if (entity.world instanceof ServerWorld) {
            BlockPos targetPos = new BlockPos(event.getTarget());
            ServerWorld world = (ServerWorld) entity.world;

            // Search radius from config
            int radius = ModConfig.enderTrapRadius.get();

            // System.out.println("TrapEvent: " + entity.getDisplayName());

            // Find the closest ender trap block within the specified radius

            // Initialize variables for storing the closest ender trap block
            BlockPos closestPos = null;
            double closestDistSq = Double.MAX_VALUE;

            // Iterate over all block positions within the search radius
            for (BlockPos pos : BlockPos.getAllInBoxMutable(targetPos.add(-radius, -radius, -radius), targetPos.add(radius, radius, radius))) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() == ModBlocks.ENDER_TRAP_BLOCK.get()) {
                    double distSq = entity.getDistanceSq(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    if (distSq < closestDistSq) {
                        closestPos = pos.toImmutable(); // <-- Added toImmutable()
                        closestDistSq = distSq;
                    }
                }
            }

            if (closestPos != null) {
                event.setCanceled(true);
                entity.setPositionAndUpdate(closestPos.getX() + 0.5, closestPos.getY() + 1.0, closestPos.getZ() + 0.5);
            }
        }
    }

    // Alternative method to jam teleporting
    public static void jamEventEntity(EntityTeleportEvent.EnderEntity event, Entity entity) {
        if (entity.world instanceof ServerWorld) {
            BlockPos targetPos = new BlockPos(event.getTarget());
            ServerWorld world = (ServerWorld) entity.world;
            int radius = ModConfig.enderTrapRadius.get();
            // Check for an ender trap block within the specified radius
            for (BlockPos pos : BlockPos.getAllInBoxMutable(targetPos.add(-radius, -radius, -radius), targetPos.add(radius, radius, radius))) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() == ModBlocks.ENDER_TRAP_BLOCK.get()) {
                    event.setCanceled(true);
                    break;
                }
            }
        }
    }

}

