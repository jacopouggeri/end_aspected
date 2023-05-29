package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.block.tile.VoidTreeTileEntity;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class VoidFungusBlock extends SaplingBlock {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private final Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier;

    private final Tree tree;

    public VoidFungusBlock(Tree treeIn, Properties properties, Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier) {
        super(treeIn, properties.tickRandomly());
        this.tree = treeIn;
        this.tileEntityTypeSupplier = tileEntityTypeSupplier;
    }

    @Override
    protected boolean isValidGround(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return state.isSolid();
    }

    public @Nonnull VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
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
    public void onBlockAdded(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isRemote) {
            // Schedule a task to tick the block
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(@Nonnull BlockState state, ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        // Schedule Tick every second
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 20);
        // Retrieve the TileEntity
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && (tileEntity instanceof VoidTreeTileEntity)) {
            VoidTreeTileEntity voidTreeTileEntity = (VoidTreeTileEntity) tileEntity;
            if (voidTreeTileEntity.isHungry()) {
                // Define search area
                AxisAlignedBB searchArea = new AxisAlignedBB(pos).grow(3.5D);

                // Get Nearby Entities
                List<LivingEntity> nearbyEntities = worldIn.getEntitiesWithinAABB(LivingEntity.class, searchArea);

                // Kill them if their health is low enough
                for (LivingEntity entity : nearbyEntities) {
                    entity.addPotionEffect(new EffectInstance(ModEffects.VOID_SICKNESS.get(), 100, 0));
                    if (entity.getHealth() <= 2.0F) {
                        // Kill the entity without dropping loot
                        entity.setHealth(0.0F);
                        entity.onDeath(DamageSource.OUT_OF_WORLD);
                        // Reduce Tree Hunger by amount of health the entity had
                        voidTreeTileEntity.reduceHunger((int) entity.getMaxHealth());
                    }
                }
            } else {
                if (!worldIn.isAreaLoaded(pos, 1))
                    return; // Forge: prevent loading unloaded chunks when checking neighbor's light
                this.placeTree(worldIn, pos, state, rand);
            }
        }
    }

    @Override
    public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return false;
    }

    @Override
    public void placeTree(@Nonnull ServerWorld world, @Nonnull BlockPos pos, BlockState state, @Nonnull Random rand) {
        if (state.get(STAGE) == 0) {
            world.setBlockState(pos, state.cycleValue(STAGE), 4);
        } else {
            if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(world, rand, pos)) return;
            if (this.tree.attemptGrowTree(world, world.getChunkProvider().getChunkGenerator(), pos, state, rand)) {
                // Tree has successfully grown, so spread the void veins around the base of the tree.
                placeVoidVein(world, pos, rand);
            }
        }
    }

    private static void placeVoidVein(ServerWorld world, BlockPos pos, Random random) {
        int range = 4;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                // Adding a random offset to the distance calculation
                double distanceToTree = Math.sqrt(dx * dx + dz * dz) + (random.nextFloat() * 2.0F - 1.0F);
                if (distanceToTree <= range) {
                    BlockPos groundPos = pos.add(dx, 0, dz);
                    if (world.getBlockState(groundPos).getMaterial().isReplaceable()) {
                        // Place the void vein block.
                        world.setBlockState(groundPos, ModBlocks.VOID_VEIN_BLOCK.get().getDefaultState(), 3);
                    }
                }
            }
        }
    }


}
