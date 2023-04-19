package com.jayugg.end_aspected.block;

import com.jayugg.end_aspected.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public class EnderTrapBlock extends Block {

    public EnderTrapBlock(Properties builder) {
        super(builder);
    }

    public static void trapEventEntity(EntityTeleportEvent event, Entity entity) {
        if (true) {
            BlockPos targetPos = event.getEntity().getOnPos();
            ServerLevel world = (ServerLevel) entity.getLevel();

            // Search radius from config
            int radius = ModConfig.enderTrapRadius.get();

            // System.out.println("TrapEvent: " + entity.getDisplayName());

            // Find the closest ender trap block within the specified radius

            // Initialize variables for storing the closest ender trap block
            BlockPos closestPos = null;
            double closestDistSq = Double.MAX_VALUE;

            // Iterate over all block positions within the search radius
            for (BlockPos pos : BlockPos.betweenClosedStream(targetPos.offset(-radius, -radius, -radius), targetPos.offset(radius, radius, radius)).toArray(BlockPos[]::new)) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() == ModBlocks.ENDER_TRAP_BLOCK.get()) {
                    double distSq = entity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    if (distSq < closestDistSq) {
                        closestPos = pos.immutable();
                        closestDistSq = distSq;
                    }
                }
            }



            if (closestPos != null) {
                event.setCanceled(true);
                entity.teleportTo(closestPos.getX() + 0.5, closestPos.getY() + 1.0, closestPos.getZ() + 0.5);
            }
        }
    }

}

