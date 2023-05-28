package net.jayugg.end_aspected.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class VoidVeinBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Supplier<BlockEntityType<VoidVeinTileEntity>> blockEntityTypeSupplier;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.1D, 16.0D);

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return type == ModTileEntities.VOID_VEIN.get() ? VoidVeinTileEntity::tick : null;
    }

    public VoidVeinBlock(Properties properties, Supplier<BlockEntityType<VoidVeinTileEntity>> blockEntityTypeSupplier) {
        super(properties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
        this.blockEntityTypeSupplier = blockEntityTypeSupplier;
    }

    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return this.blockEntityTypeSupplier.get().create(pos, state);
    }

    public boolean canBeReplaced(@Nonnull BlockState pState, BlockPlaceContext pUseContext) {
        return !(pState == this.defaultBlockState())|| super.canBeReplaced(pState, pUseContext);
    }

    public @Nonnull FluidState getFluidState(@Nonnull BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public @Nonnull PushReaction getPistonPushReaction(@Nonnull BlockState pState) {
        return PushReaction.DESTROY;
    }

    public @Nonnull VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return SHAPE;
    }
}
