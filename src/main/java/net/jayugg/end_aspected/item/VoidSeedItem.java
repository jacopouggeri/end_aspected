package net.jayugg.end_aspected.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VoidSeedItem extends Item {

    private static final Random random = new Random();

    private static final int MAX_FULLNESS = 1024;

    public VoidSeedItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean isFull(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getInt("fullness") >= MAX_FULLNESS;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand handIn) {
        // Works when in offhand only!
        if (handIn != InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
        }
        ItemStack heldItem = playerIn.getMainHandItem();
        ItemStack thisItem = playerIn.getOffhandItem();

        // Fail if items are missing or shift is not held
        if (thisItem.isEmpty() ||
                heldItem.isEmpty() ||
                !playerIn.isCrouching() ||
                heldItem.getItem() instanceof VoidSeedItem ||
                isFull(thisItem)) {
            return InteractionResultHolder.fail(thisItem);
        }

        int itemCount = heldItem.getCount();
        // Calculates the amount of items to eat, if fullness overflows, it will return the amount of items that can be eaten
        int reduceAmount = addFullness(thisItem, itemCount);
        heldItem.shrink(reduceAmount);
        playerIn.playSound(SoundEvents.CONDUIT_ACTIVATE, 1.0F, random.nextFloat() * 0.4F + 0.8F);
        if (!worldIn.isClientSide()) {
            ((ServerLevel) worldIn).sendParticles(ParticleTypes.WARPED_SPORE, playerIn.getX(), playerIn.getY(), playerIn.getZ(), 50, 0.5, 0.5, 0.5, 0.0);
        }
        return InteractionResultHolder.success(thisItem);
    }

    private static int addFullness(ItemStack stack, int toAdd) {
        CompoundTag nbt = stack.getOrCreateTag();
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
        CompoundTag nbt = stack.getOrCreateTag();
        int fullness = nbt.getInt("fullness");
        if (fullness > MAX_FULLNESS) {
            fullness = MAX_FULLNESS;
            nbt.putInt("fullness", fullness);
        }
        return fullness;
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return isFull(stack);
    }


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.end_aspected.void_seed.desc"));
        } else {
            tooltip.add(Component.translatable("tooltip.end_aspected.more"));
        }

        int fullness = getFullness(stack);
        if (isFull(stack)) {
            tooltip.add(Component.translatable("tooltip.end_aspected.void_seed.full"));
        } else {
            tooltip.add(Component.translatable("tooltip.end_aspected.void_seed.fullness", "§2" + fullness, "§2" + MAX_FULLNESS));
        }
    }


}
