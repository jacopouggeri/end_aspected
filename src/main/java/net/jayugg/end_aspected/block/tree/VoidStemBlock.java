package net.jayugg.end_aspected.block.tree;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.parent.IVeinNetworkNode;
import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.item.voids.tool.VoidAxeItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
@MethodsReturnNonnullByDefault
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

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        if (toolType == ToolType.AXE) return VoidAxeItem.getAxeStrippingState(state);
        return super.getToolModifiedState(state, world, pos, player, stack, toolType);
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() == Items.GLASS_BOTTLE) {
            if (this.canExtractSap(state)) {
                if (!world.isRemote()) {
                    // Change the block state to indicate that it's been tapped
                    this.tap(world, pos, state);

                    // Replace the bottle in the player's hand with a sap bottle, or drop it at their feet
                    if (!player.abilities.isCreativeMode) {
                        heldItem.shrink(1);
                    }
                    ItemStack sapStack = new ItemStack(ModItems.VOID_SAP.get());
                    if (!player.addItemStackToInventory(sapStack)) {
                        player.dropItem(sapStack, false);
                    }
                }

                // Play a sound effect
                world.playSound(player, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

                return ActionResultType.SUCCESS;
            }
        }

        // If the player isn't holding a bottle, or the block isn't ready to give sap, do the usual thing
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    private void tap(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(CHARGE, 0));
    }

    private boolean canExtractSap(BlockState state) {
        return state.get(CHARGE) == 4;
    }
}
