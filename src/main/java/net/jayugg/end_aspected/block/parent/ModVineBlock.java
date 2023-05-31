package net.jayugg.end_aspected.block.parent;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModVineBlock extends MultiFaceBlock implements IForgeShearable {
    public ModVineBlock(Properties properties) {
        super(properties);
    }
    /*
    Determine if the vine can attach to the block in the given direction
     */
    public static boolean canAttachTo(IBlockReader blockReader, BlockPos worldIn, Direction neighborPos) {
        BlockState blockstate = blockReader.getBlockState(worldIn);
        return Block.doesSideFillSquare(blockstate.getCollisionShapeUncached(blockReader, worldIn), neighborPos.getOpposite());
    }

    /*
    Determines if the vine is attached to a legal block in the given direction
     */
    private boolean hasAttachment(IBlockReader blockReader, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.offset(direction);
        if (canAttachTo(blockReader, blockpos, direction)) {
            // If vines can attach to the block in the given direction, return true
            return true;
        } else if (direction.getAxis() == Direction.Axis.Y) {
            // Vines cannot attach to the top or bottom of a block
            return false;
        } else {
            BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
            // Get state of above block
            BlockState blockstate = blockReader.getBlockState(pos.up());
            // Vine can attach if the above block is a vine with the correct blockstate
            return blockstate.matchesBlock(this) && blockstate.get(booleanproperty);
        }
    }

    /*
    Get an updated blockstate based on the blocks the vine is attached to
    Basically removes all illegal directions from the blockstate
     */
    private BlockState getUpdatedBlockState(BlockState state, IBlockReader blockReader, BlockPos pos) {
        BlockPos blockpos = pos.up();
        BlockState blockstate = null;

        // Remove corresponding state if the block above doesn't exist anymore
        for(Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            if (state.get(booleanproperty)) {
                // Check that block can still be attached
                boolean flag = this.hasAttachment(blockReader, pos, direction);
                // If it can't, check whether it can attach to the block above
                if (!flag) {
                    if (blockstate == null) {
                        // Get current state of the block above
                        blockstate = blockReader.getBlockState(blockpos);
                    }
                    // If block above is a vine with the same directions, keep the direction
                    flag = blockstate.matchesBlock(this) && blockstate.get(booleanproperty);
                }
                // Keep the direction if it's considered legal (has vine above or is attached to block)
                state = state.with(booleanproperty, Boolean.valueOf(flag));
            }
        }

        return state;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState blockstate = this.getUpdatedBlockState(stateIn, worldIn, currentPos);
        // Remove the vine if it's not attached to anything
        return this.getPresentFaces(blockstate) ? blockstate : Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        BlockState blockstate = useContext.getWorld().getBlockState(useContext.getPos());
        if (blockstate.matchesBlock(this)) {
            // If not all sides are covered, allow to place an extra vine (positioning will be handled in getStateForPlacement)
            return this.countPresentFaces(blockstate) < FACING_TO_PROPERTY_MAP.size();
        } else {
            // Else use the default method
            return super.isReplaceable(state, useContext);
        }
    }

    /*
    Returns the blockstate that will be used when the vine is placed

     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        // Check if new block has an acceptable blockstate, if it doesn't, use the default state
        boolean flag = blockstate.matchesBlock(this);
        BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

        // Will get closest available direction if direction is not available
        for(Direction direction : context.getNearestLookingDirections()) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            // Check if there's a vine already attached to the block in the given direction
            boolean flag1 = flag && blockstate.get(booleanproperty);
            if (!flag1 && this.hasAttachment(context.getWorld(), context.getPos(), direction)) {
                // If there isn't and the vine can attach to the block, add the direction to the blockstate
                return blockstate1.with(booleanproperty, Boolean.valueOf(true));
            }
        }

        // Return null if the incoming blockstate is not acceptable, otherwise return the new blockstate
        return flag ? blockstate1 : null;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Nonnull
    public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
        // Drop the vine when sheared
        BlockState blockState = world.getBlockState(pos);
        return Collections.singletonList(new ItemStack(this, countPresentFaces(blockState)));
    }
}
