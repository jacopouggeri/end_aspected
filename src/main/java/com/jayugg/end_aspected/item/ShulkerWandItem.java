package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.entity.AspectedShulkerBulletEntity;
import com.jayugg.end_aspected.utils.FormatUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ShulkerWandItem extends SwordItem {

    public ShulkerWandItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public static void spawnBulletEntity(PlayerEntity player, World world) {
        double pX = player.getPosX();
        double pY = player.getPosY() + player.getEyeHeight();
        double pZ = player.getPosZ();

        Vector3d look = player.getLookVec().normalize().scale(1);
        double vX = look.x;
        double vY = look.y;
        double vZ = look.z;

        AspectedShulkerBulletEntity shulkerBullet = new AspectedShulkerBulletEntity(world, pX, pY, pZ, vX, vY, vZ);
        shulkerBullet.setNoGravity(true);
        world.addEntity(shulkerBullet);
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!player.getEntityWorld().isRemote) {
            if (ModConfig.enableShulkerWandCooldown.get() && !player.isCreative()) {
                // Add to cooldown
                int cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;
                player.getCooldownTracker().setCooldown(this, cooldownTime);
            }

            // Reduce durability
            if (ModConfig.enableShulkerWandLostDurability.get()) {
                ItemStack stack = player.getHeldItem(hand);
                stack.damageItem(ModConfig.shulkerWandLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
            }

            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.PLAYERS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            spawnBulletEntity(player, world);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void addInformation(@Nonnull ItemStack item, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        if (Screen.hasShiftDown()) {

            String cooldownString = FormatUtils.formatNumber(ModConfig.shulkerWandCooldown.get());
            TranslationTextComponent ability = new TranslationTextComponent("tooltip.end_aspected.shulker_wand_shift");
            TranslationTextComponent cooldown = new TranslationTextComponent("tooltip.end_aspected.cooldown", "§2" + cooldownString + "§r");
            TranslationTextComponent stats = new TranslationTextComponent("tooltip.end_aspected.stats");

            // Handle no cooldown in config
            if (ModConfig.enableShulkerWandCooldown.get()) {
                TranslationTextComponent message_final = (TranslationTextComponent) ability
                        .appendSibling(stats)
                        .appendSibling(cooldown);

                tooltip.add(message_final);
            } else {
                tooltip.add(ability);
            }

        } else {
            tooltip.add(new TranslationTextComponent("tooltip.end_aspected.more"));
        }
    }

}
