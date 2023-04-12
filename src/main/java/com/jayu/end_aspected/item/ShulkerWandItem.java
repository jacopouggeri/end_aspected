package com.jayu.end_aspected.item;

import com.jayu.end_aspected.config.ModConfig;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShulkerWandItem extends Item {

    private final Long2LongMap cooldowns = new Long2LongOpenHashMap();

    public ShulkerWandItem(Properties builder) {
        super(builder);
    }

    @Override
    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {
            long cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;

            long currentTime = world.getGameTime();
            long playerId = player.getUniqueID().getLeastSignificantBits();
            long lastTime = cooldowns.getOrDefault(playerId, - cooldownTime);

            if (currentTime < lastTime + cooldownTime) {
                return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
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

            cooldowns.put(playerId, currentTime);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

}
