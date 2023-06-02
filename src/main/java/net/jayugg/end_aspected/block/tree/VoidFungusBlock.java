package net.jayugg.end_aspected.block.tree;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.IVeinNetworkElement;
import net.jayugg.end_aspected.util.IVoidVeinPlacer;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VoidFungusBlock extends BushBlock implements IGrowable, IWaterLoggable, IVeinNetworkElement, IVoidVeinPlacer {
    public static final IntegerProperty POWER = IVeinNetworkElement.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private final Tree tree;

    public VoidFungusBlock(Tree treeIn, Properties properties) {
        super(properties);
        this.tree = treeIn;
        this.setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false).with(POWER, 0));
    }

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.isSolid();
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isRemote) {
            // Schedule a task to tick the block
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random rand) {
        // Schedule Tick every second
        serverWorld.getPendingBlockTicks().scheduleTick(blockPos, this, 20);

        if (isNotFull(blockState)) {
            // Define search area
            AxisAlignedBB searchArea = new AxisAlignedBB(blockPos).grow(3.5D);

            // Get Nearby Entities
            List<LivingEntity> nearbyEntities = serverWorld.getEntitiesWithinAABB(LivingEntity.class, searchArea);

            // Kill them if their health is low enough
            for (LivingEntity entity : nearbyEntities) {
                entity.addPotionEffect(new EffectInstance(ModEffects.VOIDRUE.get(), 40, 0));
                if (entity.getHealth() <= 2.0F) {
                    // Kill the entity without dropping loot
                    entity.setHealth(0.0F);
                    entity.onDeath(DamageSource.OUT_OF_WORLD.setMagicDamage());
                    // Add power based on amount of health the entity had
                    blockState = IVeinNetworkElement.addPowerFromHealth(blockState, (int) entity.getMaxHealth());
                    serverWorld.setBlockState(blockPos, blockState);
                }
            }
        }
        if (!serverWorld.isAreaLoaded(blockPos, 1)) {
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        }
        this.placeTree(serverWorld, blockPos, blockState, rand);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return false;
    }

    public void placeTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
        if (state.get(POWER) == IVeinNetworkElement.MAX_POWER) {
            if (!ForgeEventFactory.saplingGrowTree(world, rand, pos)) return;
            if (this.tree.attemptGrowTree(world, world.getChunkProvider().getChunkGenerator(), pos, state, rand)) {
                // Tree has successfully grown, so spread the void veins around the base of the tree.
                placeVoidVeins(world, pos, rand);
            }
        }
    }

    private void placeVoidVeins(ServerWorld serverWorld, BlockPos blockPos, Random rand) {
        VoidVeinBlock voidVeinBlock = (VoidVeinBlock) ModBlocks.VOID_VEIN.get();
        int range = 4;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                // Adding a random offset to the distance calculation
                double distanceToTree = Math.sqrt(dx * dx + dz * dz) + (rand.nextFloat() * 2.0F - 1.0F);
                if (distanceToTree <= range) {
                    BlockPos groundPos = blockPos.add(dx, 0, dz);
                    placeVeinAtPosition(serverWorld, groundPos, voidVeinBlock);
                }
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(WATERLOGGED, fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8);
    }

    @Override
    public BlockState updatePostPlacement(BlockState blockState, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (blockState.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return blockState;
    }

    @Override
    public boolean isValidPosition(BlockState blockState, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isSolid();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(WATERLOGGED, POWER);
    }

    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        this.placeTree(worldIn, pos, state, rand);
    }

}