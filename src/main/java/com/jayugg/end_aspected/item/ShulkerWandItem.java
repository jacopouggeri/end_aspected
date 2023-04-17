package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.entity.AspectedShulkerBulletEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShulkerWandItem extends SwordItem {
    private static final String COOLDOWN_END_TAG = "cooldownEndTime";

    public ShulkerWandItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
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
        ItemStack stack = player.getHeldItem(hand);
        long cooldownEndTime = stack.getOrCreateTag().getLong(COOLDOWN_END_TAG);
        if (!player.getEntityWorld().isRemote) {
            if (ModConfig.enableShulkerWandCooldown.get() && !player.isCreative()) {
                if (hasCooldown(cooldownEndTime, world)) {
                    if (ModConfig.enableShulkerWandLostDurability.get()) {
                        stack.damageItem(ModConfig.shulkerWandLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                    } else {
                        return ActionResult.resultFail(player.getHeldItem(hand));
                    }
                } else {
                    // Add to cooldown
                    long cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;
                    cooldownEndTime = world.getGameTime() + cooldownTime;
                    stack.getOrCreateTag().putLong(COOLDOWN_END_TAG, cooldownEndTime);
                }
            }

            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SHULKER_SHOOT, SoundCategory.PLAYERS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            spawnBulletEntity(player, world);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    public boolean hasCooldown(long endTime, World world) {
        return endTime > world.getGameTime();
    }

}
