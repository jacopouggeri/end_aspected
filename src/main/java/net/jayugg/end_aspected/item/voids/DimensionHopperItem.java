package net.jayugg.end_aspected.item.voids;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DimensionHopperItem extends Item {
    public DimensionHopperItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        if (!worldIn.isRemote()) {
            if (worldIn.getServer().getDynamicRegistries() == null) {
                return ActionResult.resultFail(playerIn.getHeldItem(handIn));
            }
            // Get the list of all dimensions
            List<RegistryKey<World>> allDimensions = worldIn.getServer().getDynamicRegistries().getRegistry(Registry.DIMENSION_TYPE_KEY).getEntries().stream()
                    .map(entry -> RegistryKey.getOrCreateKey(Registry.WORLD_KEY, entry.getKey().getLocation()))
                    .collect(Collectors.toList());

            if (!allDimensions.isEmpty()) {
                // Choose a random dimension from the list
                RegistryKey<World> randomDimension = allDimensions.get(worldIn.getRandom().nextInt(allDimensions.size()));

                // Check if the chosen dimension is the current dimension
                if (!worldIn.getDimensionKey().equals(randomDimension)) {
                    // Teleport the player to the same coordinates in the new dimension
                    ServerWorld destinationWorld = worldIn.getServer().getWorld(randomDimension);
                    if (destinationWorld != null) {
                        double scale = getDimensionScale(worldIn.getDimensionKey(), randomDimension);
                        double x = playerIn.getPosX() * scale;
                        double z = playerIn.getPosZ() * scale;
                        playerIn.changeDimension(destinationWorld, new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);
                                // Set the entity's position in the new dimension
                                repositionedEntity.setPositionAndUpdate(x, playerIn.getPosY(), z);
                                return repositionedEntity;
                            }
                        });
                    }
                }
            }
        }

        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    private double getDimensionScale(RegistryKey<World> currentDimension, RegistryKey<World> destinationDimension) {
        if (currentDimension == World.OVERWORLD && destinationDimension == World.THE_NETHER) {
            return 0.125;
        } else if (currentDimension == World.THE_NETHER && destinationDimension == World.OVERWORLD) {
            return 8;
        }
        return 1;  // For all other dimensions, no scaling is applied
    }

}
