package net.jayugg.end_aspected.block.tree;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.parent.IConnectedFlora;
import net.jayugg.end_aspected.block.parent.ModVeinBlock;
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
public class VoidVeinBlock extends ModVeinBlock implements IWaterLoggable, IConnectedFlora {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty DISTANCE = IConnectedFlora.DISTANCE;
    public static final BooleanProperty CONNECTED = IConnectedFlora.CONNECTED;

    public VoidVeinBlock(Properties properties) {
        super(properties.tickRandomly());
        this.setDefaultState(super.getDefaultState().with(WATERLOGGED, false).with(CONNECTED, false).with(DISTANCE, MAX_DISTANCE));
    }

    @Override
    public BlockState updatePostPlacement(BlockState blockState, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = super.updatePostPlacement(blockState, facing, facingState, worldIn, currentPos, facingPos);
        if (newState.hasProperty(CONNECTED)) {
            newState = updateConnection(newState, worldIn, currentPos);
            if (!newState.get(CONNECTED)) {
                worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
            }
        }
        return newState;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        BlockState finalState = super.getStateForPlacement(context);
        return finalState != null ? updateConnection(finalState, context.getWorld(), blockpos).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER) : null;
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

    public boolean ticksRandomly(BlockState state) {
        return state.get(DISTANCE) == MAX_DISTANCE;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (!serverWorld.isRemote()) {
            BlockState newState = updateConnection(blockState, serverWorld, blockPos);
            boolean connection = newState.get(CONNECTED);
            // if no connection was found nearby, destroy this block
            if (!connection) {
                serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        worldIn.setBlockState(pos, updateConnection(state, worldIn, pos), 3);
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
        builder.add(WATERLOGGED, CONNECTED, DISTANCE);
    }

    @Override
    public PushReaction getPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

}
