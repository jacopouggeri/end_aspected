package net.jayugg.end_aspected.item.end;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SouvenirItem extends Item{

    public SouvenirItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity user, int itemSlot, boolean isSelected) {
        if (!world.isRemote && user instanceof PlayerEntity && random.nextFloat() > 0.99) {
            PlayerEntity player = (PlayerEntity) user;
            // Get the player's position
            BlockPos playerPos = user.getPosition();

            // Get a list of all valid block positions within the 2 block radius sphere
            List<BlockPos> validPositions = getValidBlockPositions(playerPos, 3);

            // Select a random position from the valid positions list
            BlockPos randomPos = getRandomBlockPosition(validPositions, world.getRandom());

            if (randomPos != null) {
                BlockState blockState = world.getBlockState(randomPos);
                Block block = blockState.getBlock();

                // Add the block to the player's inventory
                ItemStack blockStack = new ItemStack(block);
                if (!blockStack.isEmpty()) {
                    boolean addResult = player.inventory.addItemStackToInventory(blockStack);
                    if(addResult){
                        world.removeBlock(randomPos, false);
                    }
                }
            }
        }
    }

    private List<BlockPos> getValidBlockPositions(BlockPos centerPos, int radius) {
        List<BlockPos> validPositions = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centerPos.add(x, y, z);
                    if (isValidBlockPosition(pos)) {
                        validPositions.add(pos);
                    }
                }
            }
        }

        return validPositions;
    }

    private boolean isValidBlockPosition(BlockPos pos) {
        return pos.getY() >= 0 && pos.getY() <= 255; // Optional: Add additional conditions if needed
    }

    private BlockPos getRandomBlockPosition(List<BlockPos> positions, Random random) {
        if (positions.isEmpty()) {
            return null;
        }
        return positions.get(random.nextInt(positions.size()));
    }
}

