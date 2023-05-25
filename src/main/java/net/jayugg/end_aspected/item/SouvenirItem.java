package net.jayugg.end_aspected.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SouvenirItem extends Item {

    public SouvenirItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity user, int itemSlot, boolean isSelected) {
        if (!level.isClientSide && user instanceof Player && level.getRandom().nextFloat() > 0.99) {
            Player player = (Player) user;
            // Get the player's position
            BlockPos playerPos = user.blockPosition();

            // Get a list of all valid block positions within the 2 block radius sphere
            List<BlockPos> validPositions = getValidBlockPositions(playerPos, 3);

            // Select a random position from the valid positions list
            BlockPos randomPos = getRandomBlockPosition(validPositions, level.getRandom());

            if (randomPos != null) {
                BlockState blockState = level.getBlockState(randomPos);
                Block block = blockState.getBlock();

                // Add the block to the player's inventory
                ItemStack blockStack = new ItemStack(block);
                if (!blockStack.isEmpty()) {
                    boolean addResult = player.getInventory().add(blockStack);
                    if(addResult){
                        level.destroyBlock(randomPos, false);
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
                    BlockPos pos = centerPos.offset(x, y, z);
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

    private BlockPos getRandomBlockPosition(List<BlockPos> positions, RandomSource randomSource) {
        if (positions.isEmpty()) {
            return null;
        }
        return positions.get(randomSource.nextInt(positions.size()));
    }
}


