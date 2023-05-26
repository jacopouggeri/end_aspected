package net.jayugg.end_aspected.item;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
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

public class VoidSeedItem extends Item {

    private static final int MAX_FULLNESS = 1024;
    private static final int EATING_DURATION = 32; // You can adjust this value to change the duration of eating

    public VoidSeedItem(Properties properties) {
        super(properties.maxStackSize(1));
    }

    public static boolean isFull(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        return nbt.getInt("fullness") >= MAX_FULLNESS;
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
                heldItem.getItem() instanceof VoidSeedItem ||
                isFull(thisItem)) {
            return ActionResult.resultFail(thisItem);
        }

        int itemCount = heldItem.getCount();
        // Calculates the amount of items to eat, if fullness overflows, it will return the amount of items that can be eaten
        int reduceAmount = addFullness(thisItem, itemCount);
        heldItem.shrink(reduceAmount);
        playerIn.playSound(SoundEvents.BLOCK_CONDUIT_ACTIVATE, 1.0F, random.nextFloat() * 0.4F + 0.8F);
        ((ServerWorld) worldIn).spawnParticle(ParticleTypes.WARPED_SPORE, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), 50, 0.5, 0.5, 0.5, 0.0);
        return ActionResult.resultSuccess(thisItem);
    }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return EATING_DURATION;
    }

    private static int addFullness(ItemStack stack, int toAdd) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int fullness = nbt.getInt("fullness");
        if (fullness > MAX_FULLNESS) {
            fullness = MAX_FULLNESS;
            nbt.putInt("fullness", fullness);
        }
        int added = Math.min(toAdd, MAX_FULLNESS - fullness);
        nbt.putInt("fullness", fullness + added);
        return added;
    }

    private int getFullness(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int fullness = nbt.getInt("fullness");
        if (fullness > MAX_FULLNESS) {
            fullness = MAX_FULLNESS;
            nbt.putInt("fullness", fullness);
        }
        return fullness;
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return isFull(stack);
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerIn = (PlayerEntity) entityLiving;
            ItemStack heldItem = playerIn.getHeldItemMainhand();
            int itemCount = heldItem.getCount();
            // Calculates the amount of items to eat, if fullness overflows, it will return the amount of items that can be eaten
            int reduce = addFullness(stack, itemCount);
            heldItem.shrink(reduce);
        }

        return stack;
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
        if (isFull(stack)) {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.void_seed.full"));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.void_seed.fullness", "§2" + fullness, "§2" + MAX_FULLNESS));
        }
    }

}
