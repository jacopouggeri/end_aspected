package net.jayugg.end_aspected.item.voids;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IVoidItem<Interface, Type extends IConsumingStatSet<Interface, ?>> {
    Random rand = new Random();
    void consumeStack(ItemStack thisItem, ItemStack toConsume);
    Type getNewTier(ItemStack thisItem, ItemStack toConsume, Interface tierIn);
    boolean canConsume(ItemStack stack);
    Type fromNBT(CompoundNBT tag);

    default Type getTierFromStack(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        if (nbt.contains("tier", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT tierNBT = nbt.getCompound("tier");
            return fromNBT(tierNBT);
        } else {
            return getTier();
        }
    }

    default void setTierToStack(Type tier, ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();
        CompoundNBT tierNBT = tier.toNBT();
        nbt.put("tier", tierNBT);
        itemStack.setTag(nbt);
    }

    Type getTier();

    default ActionResult<ItemStack> consumeOnRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        // Works when in offhand only!
        if (handIn != Hand.OFF_HAND) {
            return ActionResult.resultFail(playerIn.getHeldItem(handIn));
        }
        ItemStack heldItem = playerIn.getHeldItemMainhand();
        ItemStack thisItem = playerIn.getHeldItemOffhand();

        // Fail if cannot consume item
        if (thisItem.isEmpty() ||
                heldItem.isEmpty() ||
                !playerIn.isSneaking() ||
                !canConsume(heldItem)) {
            return ActionResult.resultFail(thisItem);
        }
        if (!worldIn.isRemote) {
            consumeStack(thisItem, heldItem);
            heldItem.shrink(1);
            playerIn.playSound(SoundEvents.BLOCK_CONDUIT_ACTIVATE, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            ((ServerWorld) worldIn).spawnParticle(ParticleTypes.WARPED_SPORE, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), 50, 0.5, 0.5, 0.5, 0.0);
        }
        return ActionResult.resultSuccess(thisItem);
    }
}
