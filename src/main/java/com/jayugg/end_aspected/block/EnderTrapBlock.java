package com.jayugg.end_aspected.block;

import com.jayugg.end_aspected.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

public class EnderTrapBlock extends Block {

    public EnderTrapBlock(Properties builder) {
        super(builder);
    }

    public static BlockPos getClosestEnderTrapBlock(World world, BlockPos center, int radius) {
        int minDistSquared = radius * radius;
        BlockPos closestBlock = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = center.offset(Direction.NORTH, x)
                            .offset(Direction.UP, y)
                            .offset(Direction.WEST, z);
                    Block currentBlock = world.getBlockState(currentPos).getBlock();

                    if (currentBlock == ModBlocks.ENDER_TRAP_BLOCK.get()) {
                        int distSquared = (int) center.distanceSq(currentPos);
                        if (distSquared <= minDistSquared) {
                            minDistSquared = distSquared;
                            closestBlock = currentPos;
                        }
                    }
                }
            }
        }

        return closestBlock;
    }

    public static void trapEventEntity(EntityTeleportEvent event, Entity entity) {
        if (entity.world instanceof ServerWorld) {
            BlockPos targetPos = new BlockPos(event.getTarget());
            ServerWorld world = (ServerWorld) entity.world;

            // Search radius from config
            int radius = ModConfig.enderTrapRadius.get();

            // Find the closest ender trap block within the specified radius
            BlockPos closestPos = getClosestEnderTrapBlock(world, targetPos, radius);

            if (closestPos != null) {
                setCanceledAndTeleport(event, entity, closestPos);
            }
        }
    }

    private static void setCanceledAndTeleport(EntityTeleportEvent event, Entity entity, BlockPos closestPos) {
        event.setCanceled(true);
        entity.setPositionAndUpdate(closestPos.getX() + 0.5, closestPos.getY() + 1.0, closestPos.getZ() + 0.5);
    }

}

