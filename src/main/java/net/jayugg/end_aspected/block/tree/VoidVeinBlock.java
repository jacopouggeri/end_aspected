package net.jayugg.end_aspected.block.tree;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.IConnectedFlora;
import net.jayugg.end_aspected.block.parent.IVeinNetworkElement;
import net.jayugg.end_aspected.block.parent.ModVeinBlock;
import net.jayugg.end_aspected.util.IVoidVeinPlacer;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidVeinBlock extends ModVeinBlock implements IWaterLoggable, IVeinNetworkElement, IVoidVeinPlacer {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty DISTANCE = IConnectedFlora.DISTANCE;
    public static final IntegerProperty POWER = IVeinNetworkElement.POWER;

    public VoidVeinBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(DOWN, false).with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false)
                .with(WATERLOGGED, false).with(DISTANCE, MAX_DISTANCE).with(POWER, 0));
    }

    @Override
    public BlockState updatePostPlacement(BlockState blockState, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = super.updatePostPlacement(blockState, facing, facingState, worldIn, currentPos, facingPos);
        if (newState == Blocks.AIR.getDefaultState()) {
            return newState;
        }
        FluidState fluidState = worldIn.getFluidState(currentPos);
        newState = newState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        if (newState.hasProperty(DISTANCE)) {
            newState = updateDistance(newState, worldIn, currentPos);
            if (!isConnected(newState, worldIn, currentPos)) {
                worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
            }
            newState = sharePowerToNeighbors(newState, worldIn, currentPos);
        }
        return newState;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        BlockState finalState = super.getStateForPlacement(context);
        return finalState != null ? finalState.with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER) : null;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        // After a block has been added, schedule a tick for this block to check its state
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        // When a neighbor block changes, schedule a tick for this block to check its state
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(DISTANCE) == MAX_DISTANCE || state.get(POWER) > 0;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        BlockState newState = updateDistance(blockState, serverWorld, blockPos);
        newState = sharePowerToNeighbors(newState, serverWorld, blockPos);
        boolean connection = isConnected(newState, serverWorld, blockPos);
        boolean flag = connection && getPresentFaces(newState);
        // if no connection was found nearby, destroy this block
        if (!flag) {
            // If the block has power, consume it to sustain itself.
            if (newState.get(POWER) > 0) {
                newState = reducePower(newState, 1);
                serverWorld.setBlockState(blockPos, newState, 3);
            } else {
                serverWorld.setBlockState(blockPos, getFluidState(newState).getBlockState(), 3);
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random rand) {
        super.tick(blockState, serverWorld, blockPos, rand);
        BlockState newState = updateDistance(blockState, serverWorld, blockPos);
        newState = getUpdatedValidState(newState, serverWorld, blockPos);
        serverWorld.setBlockState(blockPos, newState, 3);
        placeVoidFungus(serverWorld, blockPos);
        placeVoidVein(serverWorld, blockPos);
    }

    @Nullable
    private BlockPos findValidPosition(ServerWorld serverWorld, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockPos placePos = blockPos.offset(direction);
            BlockState placeState = serverWorld.getBlockState(placePos);
            if (isValidPosition(placeState, serverWorld, placePos) && !placeState.matchesBlock(ModBlocks.VOID_VEIN.get())) {
                return placePos;
            }
        }
        return null;
    }

    private void placeVoidVein(ServerWorld serverWorld, BlockPos blockPos) {
        // Check if the position has a VoidVeinBlock with blockstate property POWER == MAX_POWER
        VoidVeinBlock voidVeinBlock = (VoidVeinBlock) ModBlocks.VOID_VEIN.get();
        BlockState blockState = serverWorld.getBlockState(blockPos);
        if (blockState.getBlock() instanceof VoidVeinBlock && blockState.get(POWER) == MAX_POWER && blockState.get(DISTANCE) < MAX_DISTANCE) {
            BlockPos placePos = findValidPosition(serverWorld, blockPos);
            if (placePos != null) {
                placeVeinAtPosition(serverWorld, placePos, voidVeinBlock);
                // Reduce the power of the VoidVeinBlock
                blockState = reducePower(blockState, MAX_POWER);
                serverWorld.setBlockState(blockPos, blockState, 3);
            }
        }
    }

    private void placeVoidFungus(ServerWorld serverWorld, BlockPos blockPos) {
        // Check if the position has a VoidVeinBlock with blockstate property POWER == MAX_POWER
        BlockState blockState = serverWorld.getBlockState(blockPos);
        if (blockState.getBlock() instanceof VoidVeinBlock && blockState.get(POWER) == MAX_POWER && blockState.get(DISTANCE) <= MAX_DISTANCE) {
            BlockPos placePos = findValidPosition(serverWorld, blockPos);
            if (placePos != null) {
                // If a valid position is found, place a VoidFungusBlock
                serverWorld.setBlockState(placePos, ModBlocks.VOID_FUNGUS.get().getDefaultState(), 3);
                serverWorld.getPendingBlockTicks().scheduleTick(placePos, ModBlocks.VOID_FUNGUS.get(), 1);
                // Reduce the power of the VoidVeinBlock
                blockState = reducePower(blockState, MAX_POWER);
                serverWorld.setBlockState(blockPos, blockState, 3);
            }
        }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockAtPos = worldIn.getBlockState(pos);
        boolean flag = worldIn.getBlockState(blockpos).isSolidSide(worldIn, blockpos, Direction.UP);
        flag = flag && (blockAtPos.isAir() || blockAtPos.getFluidState().getFluid() == Fluids.WATER);
        return flag;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockState, World world, BlockPos blockPos, Random rand) {
        super.animateTick(blockState, world, blockPos, rand);
        if (world.rand.nextFloat() > 0.4f) {
            world.addParticle(ParticleTypes.WARPED_SPORE, blockPos.getX() + 0.5f, blockPos.getY() + 0.1f, blockPos.getZ() + 0.5f, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(WATERLOGGED, DISTANCE, POWER);
    }

    @Override
    public PushReaction getPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

}
