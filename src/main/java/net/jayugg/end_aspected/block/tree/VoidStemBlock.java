package net.jayugg.end_aspected.block.tree;

import net.jayugg.end_aspected.block.tile.VoidTreeTileEntity;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
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

@SuppressWarnings("deprecation")
public class VoidStemBlock extends RotatedPillarBlock {
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
    public boolean ticksRandomly(@Nonnull BlockState blockState) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onBlockAdded(blockState, world, blockPos, oldState, isMoving);
        if (!world.isRemote) {
            // Schedule a task to tick the block
            world.getPendingBlockTicks().scheduleTick(blockPos, this, 1);
        }
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState, @Nullable LivingEntity placer, @Nonnull ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, blockState, placer, itemStack);
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity instanceof VoidTreeTileEntity) {
                VoidTreeTileEntity voidTreeTileEntity = (VoidTreeTileEntity) tileEntity;
                voidTreeTileEntity.setGrown(false);
            }
        }
    }

    @Override
    public void tick(@Nonnull BlockState blockState, ServerWorld serverWorld, @Nonnull BlockPos blockPos, @Nonnull Random rand) {
        serverWorld.getPendingBlockTicks().scheduleTick(blockPos, this, 20);

        TileEntity tileEntity = serverWorld.getTileEntity(blockPos);
        if (tileEntity instanceof VoidTreeTileEntity) {
            VoidTreeTileEntity voidTreeTileEntity = (VoidTreeTileEntity) tileEntity;
            if (rand.nextFloat() > 0.98 && voidTreeTileEntity.isGrown()) {
                voidTreeTileEntity.increaseHunger();
            }
            if (voidTreeTileEntity.isHungry() && voidTreeTileEntity.isGrown()) {
                // Define search area (5 blocks radius in this example)
                AxisAlignedBB searchArea = new AxisAlignedBB(blockPos).grow(5.0D);

                // Get Nearby Entities
                List<LivingEntity> nearbyEntities = serverWorld.getEntitiesWithinAABB(LivingEntity.class, searchArea);

                // Apply wither effect if their health is low enough
                for (LivingEntity entity : nearbyEntities) {
                    entity.addPotionEffect(new EffectInstance(ModEffects.VOIDRUE.get(), 40, 0));
                    voidTreeTileEntity.reduceHunger(1);
                }
            }
        }
    }
}
