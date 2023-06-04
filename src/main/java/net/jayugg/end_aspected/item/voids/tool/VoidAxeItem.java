package net.jayugg.end_aspected.item.voids.tool;

import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidAxeItem extends VoidToolItem {
    private static final Set<Material> EFFECTIVE_ON_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.PLANTS, Material.TALL_PLANTS, Material.BAMBOO, Material.GOURD);
    private static final Set<Block> EFFECTIVE_ON_BLOCKS = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);

    public VoidAxeItem(VoidItemTier tier, float attackDamageIn, float attackSpeedIn, Properties properties) {
        super(tier, attackDamageIn, attackSpeedIn, EFFECTIVE_ON_BLOCKS, properties.addToolType(ToolType.AXE, tier.getHarvestLevel()));
    }

    @Override
    public boolean canConsume(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || stack.getItem() instanceof VoidAxeItem;
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return EFFECTIVE_ON_MATERIALS.contains(material) ? this.efficiency : super.getDestroySpeed(stack, state);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);
        BlockState block = blockstate.getToolModifiedState(world, blockpos, context.getPlayer(), context.getItem(), ToolType.AXE);
        if (block != null) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
                world.setBlockState(blockpos, block, 11);
                if (playerentity != null) {
                    context.getItem().damageItem(1, playerentity, (player) -> player.sendBreakAnimation(context.getHand()));
                }
            }

            return ActionResultType.func_233537_a_(world.isRemote);
        } else {
            return ActionResultType.PASS;
        }
    }
}
