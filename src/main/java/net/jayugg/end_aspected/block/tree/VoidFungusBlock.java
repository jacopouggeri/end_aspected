package net.jayugg.end_aspected.block.tree;

import com.mojang.authlib.GameProfile;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.tile.VoidTreeTileEntity;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VoidFungusBlock extends SaplingBlock implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private static final GameProfile VOID_FUNGUS_PROFILE = new GameProfile(UUID.randomUUID(), "void_fungus");
    private final Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier;

    private final Tree tree;

    public VoidFungusBlock(Tree treeIn, Properties properties, Supplier<TileEntityType<VoidTreeTileEntity>> tileEntityTypeSupplier) {
        super(treeIn, properties.tickRandomly());
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false));
        this.tree = treeIn;
        this.tileEntityTypeSupplier = tileEntityTypeSupplier;
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this.tileEntityTypeSupplier.get().create();
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
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
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
                    entity.addPotionEffect(new EffectInstance(ModEffects.VOIDRUE.get(), 40, 0));
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
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void placeTree(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
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
        VoidVeinBlock voidVeinBlock = (VoidVeinBlock) ModBlocks.VOID_VEIN.get();
        FakePlayer fakePlayer = FakePlayerFactory.get(world, VOID_FUNGUS_PROFILE);
        int range = 4;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                // Adding a random offset to the distance calculation
                double distanceToTree = Math.sqrt(dx * dx + dz * dz) + (random.nextFloat() * 2.0F - 1.0F);
                if (distanceToTree <= range) {
                    BlockPos groundPos = pos.add(dx, 0, dz);
                    BlockItemUseContext context = new BlockItemUseContext(
                            new ItemUseContext(fakePlayer, Hand.MAIN_HAND, new BlockRayTraceResult(new Vector3d(dx, 0, dz), Direction.DOWN, groundPos, false))
                    );
                    BlockState currentBlockState = world.getBlockState(groundPos);
                    boolean flag = currentBlockState.getMaterial().isReplaceable() ||
                            currentBlockState.matchesBlock(Blocks.WATER) ||
                            currentBlockState.matchesBlock(voidVeinBlock);
                    BlockState state = voidVeinBlock.getStateForPlacement(context);
                    if (state != null && flag) {
                        world.setBlockState(groundPos, state, 3); // Flags=3 for client update
                    }
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
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, STAGE);
    }
}