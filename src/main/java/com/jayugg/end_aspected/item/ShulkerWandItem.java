package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShulkerWandItem extends SwordItem {

    private final Long2LongMap cooldowns = new Long2LongOpenHashMap();

    public ShulkerWandItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {
            if (ModConfig.enableShulkerWandCooldown.get() && !player.isCreative()) {
                long cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;

                long currentTime = world.getGameTime();
                long playerId = player.getUniqueID().getLeastSignificantBits();
                long lastTime = cooldowns.getOrDefault(playerId, -cooldownTime);

                if (currentTime < lastTime + cooldownTime) {
                    if (ModConfig.enableShulkerWandLostDurability.get()) {
                        ItemStack stack = player.getHeldItem(hand);
                        stack.damageItem(ModConfig.shulkerWandLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                        player.sendStatusMessage(new TranslationTextComponent("msg.shulker_wand.cooldown1"), true);
                    } else {
                        player.sendStatusMessage(new TranslationTextComponent("msg.shulker_wand.cooldown2"), true);
                        return ActionResult.resultFail(player.getHeldItem(hand));
                    }
                }

                cooldowns.put(playerId, currentTime);
            }

            double pX = player.getPosX();
            double pY = player.getPosY() + player.getEyeHeight();
            double pZ = player.getPosZ();

            Vector3d look = player.getLookVec().normalize().scale(1);
            double vX = look.x;
            double vY = look.y;
            double vZ = look.z;

            ShulkerBulletEntity shulkerBullet = new ShulkerBulletEntity(world, pX, pY, pZ, vX, vY, vZ);
            shulkerBullet.setNoGravity(true);
            world.addEntity(shulkerBullet);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }


}