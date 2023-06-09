package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.block.parent.DropExperienceBlock;
import net.jayugg.end_aspected.block.parent.IVeinNetworkNode;
import net.jayugg.end_aspected.particle.ModParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.IntSupplier;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class VoidMyceliumBlock extends DropExperienceBlock implements IVeinNetworkNode {

    public static final BooleanProperty PULSE = BooleanProperty.create("bloom");
    private static final IntSupplier xpRange = () -> 5;
    public VoidMyceliumBlock(Properties properties) {
        super(properties, xpRange);
        this.setDefaultState(this.getStateContainer().getBaseState().with(PULSE, false).with(CHARGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PULSE, CHARGE);
    }

    @Override
    public void tick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random rand) {
        super.tick(blockState, serverWorld, blockPos, rand);
        if (blockState.get(PULSE)) {
            serverWorld.setBlockState(blockPos, blockState.with(PULSE, false), 3);
        }
    }

    public static void bloom(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random rand) {
        serverWorld.setBlockState(blockPos, blockState.with(PULSE, true), 3);
        serverWorld.getPendingBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 8);
        serverWorld.spawnParticle(ModParticleTypes.VOID_CHARGE.get(), (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 1.15D, (double)blockPos.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
        serverWorld.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 2.0F, 0.6F + rand.nextFloat() * 0.4F);
    }

}
