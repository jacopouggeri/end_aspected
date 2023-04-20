package com.jayugg.end_aspected.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AbsorbingItem extends Item {
    private int score;
    private final int MAX_SCORE = 100;

    public AbsorbingItem(Properties settings) {
        super(settings.maxStackSize(1));
        this.score = 0;
    }

    public int findScore(ItemStack item) {
        return (int) Math.floor(item.getMaxDamage()/100f);
    }

    @Override
    public void onCreated(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull PlayerEntity player) {
        score = findScore(stack);
        stack.getOrCreateTag().putInt("score", score);
    }

    @Override
    public void addInformation(@Nonnull ItemStack item, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("tooltip.end_aspected.absorbing_item_counter", score, MAX_SCORE));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean isSelected) {
        if (!world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            ItemStack itemInSlot = player.inventory.getStackInSlot(slot);
            if (!itemInSlot.isEmpty() && itemInSlot != stack) {
                stack.grow(findScore(itemInSlot));
                player.inventory.removeStackFromSlot(slot);
            }
        }
    }


}

