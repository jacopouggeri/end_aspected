package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShulkerWandItem extends SwordItem {

    private long cooldownEndTime;

    public ShulkerWandItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.cooldownEndTime = 0;
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!player.getEntityWorld().isRemote) {
            if (ModConfig.enableShulkerWandCooldown.get() && !player.isCreative()) {

                if (hasCooldown(cooldownEndTime, world)) {
                    if (ModConfig.enableShulkerWandLostDurability.get()) {
                        ItemStack stack = player.getHeldItem(hand);
                        stack.damageItem(ModConfig.shulkerWandLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                    } else {
                        return ActionResult.resultFail(player.getHeldItem(hand));
                    }
                }
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

            // Add to cooldown
            long cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;
            cooldownEndTime += cooldownTime;
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    public long cooldownLeft(long endTime, World world) {
        return endTime - world.getGameTime();
    }
    public boolean hasCooldown(long endTime, World world) {
        return cooldownLeft(endTime, world) > 0;
    }

}
