package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public class EnderTrapBlock extends Block {

    public EnderTrapBlock(Properties builder) {
        super(builder);
    }

    private static void cancelAndTeleport(EntityTeleportEvent event, Entity entity, BlockPos closestPos) {
        event.setCanceled(true);
        //LOGGER.info("CancelTrapEvent" + closestPos);
        entity.teleportTo(closestPos.getX() + 0.5, closestPos.getY() + 1.0, closestPos.getZ() + 0.5);
    }

    public static BlockPos getClosestEnderTrapBlock(Level level, BlockPos center, int radius) {
        int minDistSquared = radius * radius;
        BlockPos closestBlock = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = center.offset(x, y, z);
                    Block currentBlock = level.getBlockState(currentPos).getBlock();

                    if (currentBlock == ModBlocks.ENDER_TRAP_BLOCK.get()) {
                        int distSquared = (int) center.distSqr(currentPos);
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
        if (!entity.getLevel().isClientSide()) {
            BlockPos targetPos = event.getEntity().getOnPos();
            ServerLevel world = (ServerLevel) entity.getLevel();

            // Search radius from config
            int radius = ModConfig.enderTrapRadius.get();

            // Find the closest ender trap block within the specified radius
            BlockPos closestPos = getClosestEnderTrapBlock(world, targetPos, radius);
            //LOGGER.info("ClosestPos" + closestPos);

            if (closestPos != null) {
                if (event instanceof EntityTeleportEvent.EnderEntity ||
                        event instanceof EntityTeleportEvent.EnderPearl ||
                        event instanceof EntityTeleportEvent.ChorusFruit) {
                    cancelAndTeleport(event, entity, closestPos);
                }
            }
        }
    }

}

