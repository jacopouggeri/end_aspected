package net.jayugg.end_aspected.block.tree;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.ModVineBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
public class VoidVeinBlock extends ModVineBlock implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public VoidVeinBlock(Properties properties) {
        super(properties.tickRandomly());
        this.setDefaultState(super.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!worldIn.isRemote()) {
            boolean isStemNearby = false;
            // check for VOID_STEM in a 10 block radius around the current block
            for (int dx = -10; dx <= 10; dx++) {
                for (int dy = -10; dy <= 10; dy++) {
                    for (int dz = -10; dz <= 10; dz++) {
                        // check if current block is within sphere
                        if (dx * dx + dy * dy + dz * dz <= 10 * 10) {
                            BlockPos checkPos = pos.add(dx, dy, dz);
                            if (worldIn.getBlockState(checkPos).getBlock() == ModBlocks.VOID_STEM.get()) {
                                isStemNearby = true;
                                break;
                            }
                        }
                    }
                }
            }

            BlockPos blockBelowPos = pos.down();
            BlockState blockBelowState = worldIn.getBlockState(blockBelowPos);

            // if no VOID_STEM was found nearby, or if the block below is not solid, destroy this block
            if (!isStemNearby || !blockBelowState.isSolidSide(worldIn, blockBelowPos, Direction.UP)) {
                worldIn.destroyBlock(pos, false);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (worldIn.rand.nextFloat() > 0.4f) {
            worldIn.addParticle(ParticleTypes.WARPED_SPORE, pos.getX() + 0.5f, pos.getY() + 0.1f, pos.getZ() + 0.5f, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
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
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public PushReaction getPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

}
