package net.jayugg.end_aspected.item.voids;


import net.jayugg.end_aspected.block.ModBlocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VoidSeedItem extends Item {

    private static final Random random = new Random();

    public VoidSeedItem(Properties properties) {
        super(properties.maxStackSize(1));
    }

    public static boolean isFull(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int fullness = nbt.getInt("fullness");
        int maxFullness = getMaxFullness(stack);
        return fullness >= maxFullness;
    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        // Works when in offhand only!
        if (handIn != Hand.OFF_HAND) {
            return ActionResult.resultFail(playerIn.getHeldItem(handIn));
        }
        ItemStack heldItem = playerIn.getHeldItemMainhand();
        ItemStack thisItem = playerIn.getHeldItemOffhand();

        // Fail if items are missing or shift is not held
        if (thisItem.isEmpty() ||
                heldItem.isEmpty() ||
                !playerIn.isSneaking() ||
                heldItem.getItem() instanceof VoidSeedItem) {
            return ActionResult.resultFail(thisItem);
        }

        int itemCount = heldItem.getCount();
        // Calculates the amount of items to eat, if fullness overflows, it will return the amount of items that can be eaten
        int reduceAmount = addFullness(thisItem, itemCount);
        heldItem.shrink(reduceAmount);
        playerIn.playSound(SoundEvents.BLOCK_CONDUIT_ACTIVATE, 1.0F, random.nextFloat() * 0.4F + 0.8F);
        if (!worldIn.isRemote) {
            ((ServerWorld) worldIn).spawnParticle(ParticleTypes.WARPED_SPORE, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), 50, 0.5, 0.5, 0.5, 0.0);
        }
        return ActionResult.resultSuccess(thisItem);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        PlayerEntity player = (PlayerEntity) entityIn;
        if (isFull(stack)) {
            player.inventory.setInventorySlotContents(itemSlot, new ItemStack(ModBlocks.VOID_FUNGUS.get().asItem()));
        }
    }

    private static int addFullness(ItemStack stack, int toAdd) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int fullness = Math.min(nbt.getInt("fullness"), getMaxFullness(stack));
        int added = Math.min(toAdd, getMaxFullness(stack) - fullness);
        nbt.putInt("fullness", fullness + added);
        return added;
    }

    private int getFullness(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        return nbt.getInt("fullness");
    }

    private static int getMaxFullness(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (!nbt.contains("maxFullness") || nbt.getInt("maxFullness") == 0) {
            nbt.putInt("maxFullness", random.nextInt(64) + 64);
        }
        return nbt.getInt("maxFullness");
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.void_seed.desc"));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.more"));
        }

        int fullness = getFullness(stack);
        if (!isFull(stack)) {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.void_seed.fullness", "§2" + fullness, "§2" + getMaxFullness(stack)));
        }
    }

}
