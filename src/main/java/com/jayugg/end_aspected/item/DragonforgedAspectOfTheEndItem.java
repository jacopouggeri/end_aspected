package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import com.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class DragonforgedAspectOfTheEndItem extends AspectOfTheEndItem{

    private long cooldownEndTime;
    private int teleportsRemaining;
    private boolean firstRunFlag;
    private int teleportsAfterCooldown;

    public DragonforgedAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
        this.cooldownEndTime = 0;
        this.teleportsRemaining = ModConfig.maxTeleports.get();
        this.firstRunFlag = true;
        this.teleportsAfterCooldown = 0;
    }

    public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {

            if ((teleportsRemaining != ModConfig.maxTeleports.get()) && firstRunFlag) {
                teleportsRemaining = ModConfig.maxTeleports.get();
                //System.out.println(teleportsRemaining);
                firstRunFlag = false;
            }

            Vector3d teleportPos;

            teleportPos = getTeleportPosition(player, ModConfig.teleportDistance.get(), Minecraft.getInstance().getRenderPartialTicks());

            EntityTeleportEvent.EnderEntity teleportEvent = new EntityTeleportEvent.EnderEntity (player, teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            MinecraftForge.EVENT_BUS.post(teleportEvent);

            double dx = teleportPos.x;
            double dy = teleportPos.y;
            double dz = teleportPos.z;
            BlockPos destPos = new BlockPos(dx, dy, dz);

            if (teleportEvent.isCanceled()) {
                //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.disrupted"), true);
                return ActionResult.resultFail(player.getHeldItem(hand));
            }

            if (ModConfig.enableDaoteCooldown.get() && !player.isCreative()) {
                // Check if the cooldown has ended, if not reduce durability
                if (hasCooldown(cooldownEndTime, world)) {
                    if (ModConfig.enableDaoteLostDurability.get()) {
                        ItemStack stack = player.getHeldItem(hand);
                        stack.damageItem(ModConfig.daoteLostDurability.get(), player, (entity) -> entity.sendBreakAnimation(hand)); // reduce durability by 1
                        //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown1"), true);
                    } else {
                        // int remainingSeconds = (int) (cooldownLeft(cooldownEndTime, world) / 20);
                        //player.sendStatusMessage(new TranslationTextComponent("msg.aspect_of_the_end.cooldown2", remainingSeconds), true);
                        return ActionResult.resultFail(player.getHeldItem(hand));
                    }
                    spawnCooldownParticles(world, dx, dy, dz);
                }

            }


            // Play the Enderman sound at the destination position
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, dx, dy, dz, 50, 0.5, 0.5, 0.5, 0.0);

            player.setPositionAndUpdate(dx, dy, dz);
            player.fallDistance = 0;

            if (ModConfig.enableDaoteCooldown.get() && !player.isCreative()) {

                // Decrement the teleports remaining
                teleportsRemaining--;

                // Check if teleports remaining is zero and reset cooldown
                if (teleportsRemaining <= 0) {
                    // Set new time of last cooldown
                    cooldownEndTime = world.getGameTime() + ModConfig.daoteCooldown.get()*20;
                    teleportsRemaining = ModConfig.maxTeleports.get();
                }

                if (ModConfig.unstableTeleports.get()) {
                    if (hasCooldown(cooldownEndTime, world)) {
                        teleportsAfterCooldown += 1;
                        if (teleportsAfterCooldown > ModConfig.unstableTeleportsLimit.get()) {
                            int i = calculateUnstableDuration(cooldownEndTime, world);
                            player.addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 1));
                        }
                    } else {
                        teleportsAfterCooldown = 0;
                    }
                }

            }
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
}
