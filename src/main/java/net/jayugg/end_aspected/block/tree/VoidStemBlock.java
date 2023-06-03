package net.jayugg.end_aspected.block.tree;

import net.jayugg.end_aspected.block.parent.IVeinNetworkElement;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class VoidStemBlock extends RotatedPillarBlock implements IVeinNetworkElement {
    public static final IntegerProperty POWER = IVeinNetworkElement.POWER;
    public static final BooleanProperty ALIVE = BooleanProperty.create("alive");

    public VoidStemBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(AXIS, Direction.Axis.Y).with(POWER, 0).with(ALIVE, true));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(POWER) > 0;
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
        BlockState newState = sharePowerToNeighbors(blockState, serverWorld, blockPos);
        serverWorld.setBlockState(blockPos, newState, 3);
    }


    @Override
    public void tick(@Nonnull BlockState blockState, ServerWorld serverWorld, @Nonnull BlockPos blockPos, @Nonnull Random rand) {
        serverWorld.getPendingBlockTicks().scheduleTick(blockPos, this, 20);

        if (isNotFull(blockState) && blockState.get(ALIVE)) {
            if (rand.nextFloat() > 0.98) {
                blockState = reducePower(blockState, 1);
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
        builder.add(POWER, ALIVE);
    }

}
