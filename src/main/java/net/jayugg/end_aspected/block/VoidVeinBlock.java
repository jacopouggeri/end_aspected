package net.jayugg.end_aspected.block;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.tile.VoidVeinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidVeinBlock extends Block implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Supplier<TileEntityType<VoidVeinTileEntity>> tileEntityTypeSupplier;
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.1D, 16.0D);
    public VoidVeinBlock(Properties properties, Supplier<TileEntityType<VoidVeinTileEntity>> tileEntityTypeSupplier) {
        super(properties.tickRandomly());
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false));
        this.tileEntityTypeSupplier = tileEntityTypeSupplier;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isRemote) {
            // Schedule a task to tick the block every 20 ticks (1 second)
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote) {

            // Retrieve the TileEntity
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if ((tileEntity instanceof VoidVeinTileEntity)) {
                VoidVeinTileEntity voidVeinTileEntity = (VoidVeinTileEntity) tileEntity;

                if (voidVeinTileEntity.isPlacedByVoidling()) {
                    voidVeinTileEntity.increaseLifetime();
                    // Tick every 10 seconds
                    worldIn.getPendingBlockTicks().scheduleTick(pos, this, 200);
                    if (voidVeinTileEntity.shouldDestroy()) {
                        worldIn.destroyBlock(pos, false);
                    }
                }
            }

            BlockPos blockBelowPos = pos.down();
            BlockState blockBelowState = worldIn.getBlockState(blockBelowPos);

            // if the block below is not solid
            if (!blockBelowState.isSolidSide(worldIn, blockBelowPos, Direction.UP)) {
                // break this block
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

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        super.getStateForPlacement(context);
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        return this.getDefaultState().with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public PushReaction getPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        return state != this.getDefaultState(); // Replace the block only if the item used is not the same block
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isSolidSide(worldIn, pos, Direction.UP);
    }

}
