package net.jayugg.end_aspected.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Supplier;

public class VoidVeinBlock extends Block {
    private final Supplier<TileEntityType<VoidVeinTileEntity>> tileEntityTypeSupplier;
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.1D, 16.0D);
    public VoidVeinBlock(Properties properties, Supplier<TileEntityType<VoidVeinTileEntity>> tileEntityTypeSupplier) {
        super(properties.tickRandomly());
        this.tileEntityTypeSupplier = tileEntityTypeSupplier;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean ticksRandomly(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isRemote) {
            // Schedule a task to tick the block every 20 ticks (1 second)
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
    }

    @Override
    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public @Nonnull VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void tick(@Nonnull BlockState state, ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (!worldIn.isRemote) {

            // Retrieve the TileEntity
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if ((tileEntity instanceof VoidVeinTileEntity)) {
                VoidVeinTileEntity voidVeinTileEntity = (VoidVeinTileEntity) tileEntity;

                if (voidVeinTileEntity.isPlacedByVoidling()) {
                    voidVeinTileEntity.increaseLifetime();
                    int lifetime = voidVeinTileEntity.getLifetime();
                    // Schedule the next tick after a random number of ticks following a Poisson distribution
                    int nextTick = MathHelper.nextInt(worldIn.rand, 10, 30); // Adjust the range as desired
                    worldIn.getPendingBlockTicks().scheduleTick(pos, this, nextTick);
                    LOGGER.info("Lifetime: " + lifetime);
                    if (lifetime > 20) {
                        worldIn.destroyBlock(pos, true);
                    }
                }
            }

            BlockPos blockBelowPos = pos.down();
            BlockState blockBelowState = worldIn.getBlockState(blockBelowPos);

            // if the block below is not solid
            if (!blockBelowState.isSolidSide(worldIn, blockBelowPos, Direction.UP)) {
                // break this block
                worldIn.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public boolean isReplaceable(@Nonnull BlockState state, @Nonnull BlockItemUseContext useContext) {
        Item item = useContext.getItem().getItem();
        return item != Item.getItemFromBlock(this); // Replace the block only if the item used is not the same block
    }
}
