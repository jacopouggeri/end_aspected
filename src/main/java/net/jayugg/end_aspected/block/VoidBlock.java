package net.jayugg.end_aspected.block;

import net.jayugg.end_aspected.block.parent.DropExperienceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class VoidBlock extends DropExperienceBlock {
    public static final BooleanProperty FULL = BooleanProperty.create("full");
    public VoidBlock(Properties properties) {
        super(properties, () -> 1);
        this.setDefaultState(this.getStateContainer().getBaseState().with(FULL, false));
    }
    @Override
    protected boolean canDropExp(BlockState blockState) {
        return blockState.get(FULL);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState blockState, PlayerEntity player, IBlockReader worldIn, BlockPos blockPos) {
        return blockState.get(FULL) ? super.getPlayerRelativeBlockHardness(blockState, player, worldIn, blockPos) : -1.0F;
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool) {
        return state.get(FULL) && super.isToolEffective(state, tool);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FULL, true);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FULL);
    }
}
