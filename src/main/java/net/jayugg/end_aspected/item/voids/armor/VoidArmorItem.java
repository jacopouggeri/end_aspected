package net.jayugg.end_aspected.item.voids.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.item.voids.VoidItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidArmorItem extends VoidItem<IArmorMaterial, VoidArmorMaterial> implements IArmorVanishable {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    protected final EquipmentSlotType slot;
    protected final float knockbackResistance;
    protected final VoidArmorMaterial material;

    public VoidArmorItem(VoidArmorMaterial material, EquipmentSlotType slot, Item.Properties builderIn) {
        super(material, builderIn.defaultMaxDamage(material.getDurability(slot)));
        this.material = material;
        this.slot = slot;
        this.knockbackResistance = material.getKnockbackResistance();
        DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack itemStack) {
        if (equipmentSlot != this.slot) {
            return super.getAttributeModifiers(equipmentSlot);
        }
        VoidArmorMaterial material = getTierFromStack(itemStack);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", material.getDamageReductionAmount(equipmentSlot), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", material.getToughness(), AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", material.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }


    public EquipmentSlotType getEquipmentSlot() {
        return this.slot;
    }

    @Override
    protected VoidArmorMaterial getNewTier(ItemStack thisItem, ItemStack toConsume, IArmorMaterial tierIn) {
        return getTierFromStack(thisItem).consume(tierIn);
    }

    @Override
    public boolean canConsume(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return this.getEquipmentSlot() == ((ArmorItem) stack.getItem()).getEquipmentSlot();
        }
        return false;
    }

    @Override
    public void consumeStack(ItemStack thisItem, ItemStack toConsume) {
        if (toConsume.getItem() instanceof ArmorItem) {
            IArmorMaterial materialIn = ((ArmorItem) toConsume.getItem()).getArmorMaterial();
            VoidArmorMaterial newMaterial = getNewTier(thisItem, toConsume, materialIn);
            setTierToStack(newMaterial, thisItem);
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        super.onItemRightClick(worldIn, playerIn, handIn);
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);
        if (itemstack1.isEmpty()) {
            playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
            itemstack.setCount(0);
            return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        } else {
            return ActionResult.resultFail(itemstack);
        }
    }
}
