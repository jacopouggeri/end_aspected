package net.jayugg.end_aspected.block;
import net.jayugg.end_aspected.entity.VoidlingEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof VoidVeinTileEntity) {
            ((VoidVeinTileEntity) tileEntity).setPlacedByVoidling(placer instanceof VoidlingEntity);
        }
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
            int MAX_LIFETIME = 200;

            // Retrieve the TileEntity
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if ((tileEntity instanceof VoidVeinTileEntity)) {
                VoidVeinTileEntity voidVeinTileEntity = (VoidVeinTileEntity) tileEntity;
                voidVeinTileEntity.increaseLifetime();
                int lifetime = voidVeinTileEntity.getLifetime();

                if (lifetime > MAX_LIFETIME && voidVeinTileEntity.isPlacedByVoidling()) {
                    worldIn.destroyBlock(pos, true);
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
}
