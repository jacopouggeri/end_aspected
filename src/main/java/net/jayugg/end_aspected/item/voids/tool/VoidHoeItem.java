package net.jayugg.end_aspected.item.voids.tool;

import com.google.common.collect.ImmutableSet;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidHoeItem extends VoidToolItem {
    private static final Set<Block> EFFECTIVE_ON_BLOCKS = ImmutableSet.of(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES);

    public VoidHoeItem(VoidItemTier tier, float attackDamageIn, float attackSpeedIn, Properties properties) {
        super(tier, attackDamageIn, attackSpeedIn, EFFECTIVE_ON_BLOCKS, properties.addToolType(ToolType.HOE, tier.getHarvestLevel()));
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        int hook = ForgeEventFactory.onHoeUse(context);
        if (hook != 0) return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        if (context.getFace() != Direction.DOWN && world.isAirBlock(blockpos.up())) {
            BlockState blockstate = world.getBlockState(blockpos).getToolModifiedState(world, blockpos, context.getPlayer(), context.getItem(), ToolType.HOE);
            if (blockstate != null) {
                PlayerEntity playerentity = context.getPlayer();
                world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isRemote) {
                    world.setBlockState(blockpos, blockstate, 11);
                    if (playerentity != null) {
                        context.getItem().damageItem(1, playerentity, (player) -> player.sendBreakAnimation(context.getHand()));
                    }
                }

                return ActionResultType.func_233537_a_(world.isRemote);
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean canConsume(ItemStack stack) {
        return stack.getItem() instanceof HoeItem || stack.getItem() instanceof VoidHoeItem;
    }
}
