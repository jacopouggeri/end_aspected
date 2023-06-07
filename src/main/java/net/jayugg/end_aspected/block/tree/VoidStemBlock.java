package net.jayugg.end_aspected.block.tree;

import net.jayugg.end_aspected.block.parent.IVeinNetworkNode;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.item.voids.tool.VoidAxeItem;
import net.jayugg.end_aspected.particle.ModParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class VoidStemBlock extends RotatedPillarBlock implements IVeinNetworkNode {
    public static final BooleanProperty ALIVE = BooleanProperty.create("alive");

    public VoidStemBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(AXIS, Direction.Axis.Y).with(CHARGE, 0).with(ALIVE, true));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(CHARGE) > 0;
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
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState finalState = super.getStateForPlacement(context);
        return finalState != null ? finalState.with(ALIVE, false) : null;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random rand) {
        BlockState newState = shareChargeToNeighbors(blockState, serverWorld, blockPos);
        serverWorld.setBlockState(blockPos, newState, 3);
    }


    @Override
    public void tick(@Nonnull BlockState blockState, ServerWorld serverWorld, @Nonnull BlockPos blockPos, @Nonnull Random rand) {
        serverWorld.getPendingBlockTicks().scheduleTick(blockPos, this, 20);

        if (isNotFull(blockState) && blockState.get(ALIVE)) {
            if (rand.nextFloat() > 0.98) {
                blockState = reduceCharge(blockState, 1);
            }
            if (isNotFull(blockState)) {
                // Define search area (5 blocks radius in this example)
                AxisAlignedBB searchArea = new AxisAlignedBB(blockPos).grow(5.0D);

                // Get Nearby Entities
                List<LivingEntity> nearbyEntities = serverWorld.getEntitiesWithinAABB(LivingEntity.class, searchArea);

                // Apply wither effect if their health is low enough
                for (LivingEntity entity : nearbyEntities) {
                    entity.addPotionEffect(new EffectInstance(ModEffects.VOIDRUE.get(), 40, 0));
                }
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(CHARGE, ALIVE);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (rand.nextFloat() > 0.7) {
            worldIn.addParticle(ModParticleTypes.VOID_CHARGE_POP.get(), pos.getX() + 0.5D, pos.getY() + 1.15D, pos.getZ() + 0.5D, 0.0D, 0.1D, 0.0D);
        }
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        if (toolType == ToolType.AXE) return VoidAxeItem.getAxeStrippingState(state);
        return super.getToolModifiedState(state, world, pos, player, stack, toolType);
    }
}
