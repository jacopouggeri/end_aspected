package net.jayugg.end_aspected.block.parent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.IntSupplier;

@ParametersAreNonnullByDefault
public class DropExperienceBlock extends Block {
    private final IntSupplier xpRange;

    public DropExperienceBlock(Block.Properties pProperties, IntSupplier xpRange) {
        super(pProperties);
        this.xpRange = xpRange;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (worldIn.isRemote()) {
            return;
        }
        if (this.canDropExp(state)) {
            ItemStack heldItem = player.getHeldItemMainhand();
            int silkTouchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, heldItem);
            int exp = silkTouchLevel == 0 ? this.xpRange.getAsInt() : 0;
            dropXpOnBlockBreak((ServerWorld) worldIn, pos, exp);
        }
    }

    protected boolean canDropExp(BlockState blockState) {
        return true;
    }
}