package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.block.CustomOreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AbsorbingItem extends Item {
    private final int MAX_SCORE = 100;

    public AbsorbingItem(Properties settings) {
        super(settings.maxStackSize(1));
    }

    @Override
    public void onCreated(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull PlayerEntity player) {
        stack.getOrCreateTag().putInt("score", 0);
    }

    @Override
    public void addInformation(@Nonnull ItemStack item, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        int score = item.getOrCreateTag().getInt("score");
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
                stack.grow(getPowerScore(itemInSlot));
                player.inventory.removeStackFromSlot(slot);
            }
        }
    }

    public static int getPowerScore(ItemStack stack) {
        int score = 0;
        // Assign a score based on the characteristics of the item
        score += stack.getEnchantmentTagList().size(); // Adds the number of enchantments to the score
        score += getMaterialScore(stack); // Adds the material score of the item to the score
        return score;
    }

    private static int getMaterialScore(ItemStack stack) {
        int score = 0;
        Item item = stack.getItem();

        // Assign a score based on the item type
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            score += Math.round(tool.getTier().getHarvestLevel() * 1.5f); // Add mining level
            score += Math.round(tool.getTier().getEfficiency() * 2); // Add mining speed
            score += Math.round(tool.getTier().getAttackDamage() * 3); // Add attack damage
            score += Math.round(tool.getTier().getEnchantability() * 0.5f); // Add enchantability
        } else if (item instanceof SwordItem) {
            SwordItem sword = (SwordItem) item;
            score += Math.round(sword.getTier().getHarvestLevel() * 1.5f); // Add mining level
            score += Math.round(sword.getTier().getEfficiency() * 2); // Add mining speed
            score += Math.round(sword.getTier().getAttackDamage() * 3); // Add attack damage
            score += Math.round(sword.getTier().getEnchantability() * 0.5f); // Add enchantability
        } else if (item instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) item;
            score += Math.round(armor.getArmorMaterial().getDamageReductionAmount(EquipmentSlotType.CHEST) * 3f); // Add chestplate protection
            score += Math.round(armor.getArmorMaterial().getEnchantability() * 0.5f); // Add enchantability
            if (armor.getEquipmentSlot() == EquipmentSlotType.HEAD) {
                score += Math.round(armor.getArmorMaterial().getKnockbackResistance() * 5); // Add extra knockback resistance for helmets
            }
        } else if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) item;
            Block block = blockItem.getBlock();
            if (block instanceof OreBlock) {
                OreBlock oreBlock = (OreBlock) block;
                CustomOreBlock ore = (CustomOreBlock) oreBlock;
                score += Math.round(ore.getHarvestLevel(ore.getDefaultState()) * 1.5f); // Add mining level
                score += Math.round(ore.getExperience(random) * 1.5f); // Add exp drops
            }
        }

        // Add durability to all items
        int durability = item.getMaxDamage(stack);
        if (durability > 0) {
            score += Math.round((float)(item.getMaxDamage(stack) - stack.getDamage()) / item.getMaxDamage(stack) * 10); // Add durability as a percentage of remaining durability
        }

        return score;
    }


}

