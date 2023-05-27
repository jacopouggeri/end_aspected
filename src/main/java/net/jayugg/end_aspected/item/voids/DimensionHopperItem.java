package net.jayugg.end_aspected.item.voids;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class DimensionHopperItem extends Item {
    public DimensionHopperItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            if (worldIn.getServer().registryAccess() == null) {
                return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
            }

            // Get the list of all dimensions
            List<ResourceLocation> allDimensions = worldIn.getServer().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).keySet().stream()
                    .toList();

            if (!allDimensions.isEmpty()) {
                // Choose a random dimension from the list
                ResourceLocation randomDimensionLocation = allDimensions.get(worldIn.random.nextInt(allDimensions.size()));

                // Check if the chosen dimension is the current dimension
                if (!worldIn.dimension().location().equals(randomDimensionLocation)) {
                    // Teleport the player to the same coordinates in the new dimension
                    ServerLevel destinationWorld = worldIn.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, randomDimensionLocation));
                    if (destinationWorld != null) {
                        double scale = getDimensionScale(worldIn.dimension(), destinationWorld.dimension());
                        double x = playerIn.getX() * scale;
                        double z = playerIn.getZ() * scale;
                        playerIn.changeDimension(destinationWorld, new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            Entity repositionedEntity = repositionEntity.apply(false);
                            // Set the entity's position in the new dimension
                            repositionedEntity.teleportTo(x, playerIn.getY(), z);
                            return repositionedEntity;
                        }
                        });
                    }
                }
            }
        }

        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    private double getDimensionScale(ResourceKey<Level> currentDimension, ResourceKey<Level> destinationDimension) {
        ResourceKey<Level> OVERWORLD = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
        ResourceKey<Level> NETHER = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_nether"));

        if (currentDimension.equals(OVERWORLD) && destinationDimension.equals(NETHER)) {
            return 0.125;
        } else if (currentDimension.equals(NETHER) && destinationDimension.equals(OVERWORLD)) {
            return 8;
        }
        return 1;  // For all other dimensions, no scaling is applied
    }
}
