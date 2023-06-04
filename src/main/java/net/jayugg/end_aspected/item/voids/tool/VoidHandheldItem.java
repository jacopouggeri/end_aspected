package net.jayugg.end_aspected.item.voids.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.item.ModItemTier;
import net.jayugg.end_aspected.item.voids.IVoidItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class VoidHandheldItem extends ToolItem implements IVoidItem<IItemTier, VoidItemTier> {
    VoidItemTier tier;
    private final float attackDamageBonus;
    private final float attackSpeed;
    protected float efficiency;
    protected final Set<Block> effectiveBlocks;
    public VoidHandheldItem(VoidItemTier tier, float attackDamageIn, float attackSpeedIn, Set<Block> effectiveBlocksIn, Item.Properties properties) {
        super(attackDamageIn, attackSpeedIn, tier, effectiveBlocksIn, properties.defaultMaxDamage(tier.getMaxUses()).rarity(tier.getRarity()));
        this.tier = tier;
        this.effectiveBlocks = effectiveBlocksIn;
        this.efficiency = tier.getEfficiency();
        this.attackDamageBonus = attackDamageIn;
        this.attackSpeed = attackSpeedIn;
    }

    public VoidHandheldItem(VoidItemTier tier, float attackDamageIn, float attackSpeedIn, Item.Properties properties) {
        this(tier, attackDamageIn, attackSpeedIn, new HashSet<>(), properties);
    }

    private static float getAttackDamageFromStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof SwordItem) {
            return ((SwordItem) item).getAttackDamage();
        } else if (item instanceof VoidHandheldItem) {
            return ((VoidHandheldItem) item).getAttackDamage(itemStack);
        } else if (item instanceof ToolItem) {
            return ((ToolItem) item).getAttackDamage();
        } else {
            return 0.0F;
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack itemStack) {
        if (equipmentSlot != EquipmentSlotType.MAINHAND) {
            return super.getAttributeModifiers(equipmentSlot);
        }
        VoidItemTier tier = getTierFromStack(itemStack);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Void tool modifier", this.attackDamageBonus + tier.getAttackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Void tool modifier", this.attackSpeed, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    public float getAttackDamage(ItemStack itemStack) {
        return getTierFromStack(itemStack).getAttackDamage() + this.attackDamageBonus;
    }

    public VoidItemTier getNewTier(ItemStack thisItem, ItemStack toConsume, IItemTier tierIn) {
        VoidItemTier newTier = getTierFromStack(thisItem).consume(tierIn);
        newTier = newTier.addAttackDamage(getAttackDamageFromStack(toConsume) - tierIn.getAttackDamage());
        return newTier;
    }

    @Override
    public VoidItemTier fromNBT(CompoundNBT tag) {
        return VoidItemTier.fromNBT(tag);
    }

    @Override
    public VoidItemTier getTier() {
        return this.tier;
    }

    public void consumeStack(ItemStack thisItem, ItemStack toConsume) {
        if (toConsume.getItem() instanceof TieredItem) {
            IItemTier tierIn = ((TieredItem) toConsume.getItem()).getTier();
            VoidItemTier newTier = getNewTier(thisItem, toConsume, tierIn);
            setTierToStack(newTier, thisItem);
        }
    }

    @Override
    public int getItemEnchantability(ItemStack itemStack) {
        return getTierFromStack(itemStack).getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ModItemTier.VOID.getRepairMaterial().test(repair);
    }

    public abstract float getDestroySpeed(ItemStack stack, BlockState state);
    public abstract boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving);
    public abstract boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker);
    public abstract boolean canHarvestBlock(BlockState blockIn);
}
