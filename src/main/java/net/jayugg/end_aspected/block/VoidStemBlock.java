package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.block.tile.VoidTreeTileEntity;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class VoidStemBlock extends Block {
    private final Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier;

    public VoidStemBlock(Properties properties, Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier) {
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isRemote) {
            // Schedule a task to tick the block
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof VoidTreeTileEntity) {
                VoidTreeTileEntity voidTreeTileEntity = (VoidTreeTileEntity) tileEntity;
                voidTreeTileEntity.setPlaced();
            }
        }
    }

    @Override
    public void tick(@Nonnull BlockState state, ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 20);

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof VoidTreeTileEntity) {
            VoidTreeTileEntity voidTreeTileEntity = (VoidTreeTileEntity) tileEntity;
            if (rand.nextFloat() > 0.98 && !voidTreeTileEntity.isPlaced()) {
                voidTreeTileEntity.increaseHunger();
            }
            if (voidTreeTileEntity.isHungry() && !voidTreeTileEntity.isPlaced()) {
                // Define search area (5 blocks radius in this example)
                AxisAlignedBB searchArea = new AxisAlignedBB(pos).grow(5.0D);

                // Get Nearby Entities
                List<LivingEntity> nearbyEntities = worldIn.getEntitiesWithinAABB(LivingEntity.class, searchArea);

                // Apply wither effect if their health is low enough
                for (LivingEntity entity : nearbyEntities) {
                    entity.addPotionEffect(new EffectInstance(ModEffects.VOID_SICKNESS.get(), 40, 0));
                    voidTreeTileEntity.reduceHunger(1);
                }
            }
        }
    }
}
