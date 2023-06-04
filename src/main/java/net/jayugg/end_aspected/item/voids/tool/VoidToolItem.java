package net.jayugg.end_aspected.item.voids.tool;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class VoidToolItem extends VoidHandheldItem implements IVanishable {
    public VoidToolItem(float attackDamageIn, float attackSpeedIn, Set<Block> effectiveBlocksIn, VoidItemTier tier, Properties properties) {
        super(attackDamageIn, attackSpeedIn, effectiveBlocksIn, tier, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (getToolTypes(stack).stream().anyMatch(state::isToolEffective)) return getTierFromStack(stack).getEfficiency();
        return this.effectiveBlocks.contains(state.getBlock()) ? getTierFromStack(stack).getEfficiency() : 1.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(1, entityLiving, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }

        return true;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(2, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return true;
    }
}
