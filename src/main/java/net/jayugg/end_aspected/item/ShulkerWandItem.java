package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.config.ModConfig;
import net.jayugg.end_aspected.entity.AspectedShulkerBullet;
import net.jayugg.end_aspected.utils.FormatUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ShulkerWandItem extends SwordItem {
    protected final RandomSource random = RandomSource.create();

    public ShulkerWandItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public static void spawnBulletEntity(Player player, Level world) {
        AspectedShulkerBullet shulkerBullet = new AspectedShulkerBullet(world, player);
        shulkerBullet.setNoGravity(true);
        world.addFreshEntity(shulkerBullet);
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() instanceof AspectShardItem;
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!player.level.isClientSide()) {
            if (ModConfig.enableShulkerWandCooldown.get() && !player.isCreative()) {
                // Add to cooldown
                int cooldownTime = 20 * ModConfig.shulkerWandCooldown.get(); // cooldown in ticks;
                player.getCooldowns().addCooldown(this, cooldownTime);
            }

            // Reduce durability
            if (ModConfig.enableShulkerWandLostDurability.get()) {
                ItemStack stack = player.getItemInHand(hand);
                stack.setDamageValue(stack.getDamageValue() + 1); // reduce durability by 1
            }

            world.playSound(player, player.getOnPos(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
            spawnBulletEntity(player, world);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }


    @Override
    public void appendHoverText(@Nonnull ItemStack item, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (Screen.hasShiftDown()) {

            String cooldownString = FormatUtils.formatNumber(ModConfig.shulkerWandCooldown.get());
            Component ability = Component.translatable("tooltip.end_aspected.shulker_wand_shift");
            Component cooldown = Component.translatable("tooltip.end_aspected.cooldown", "§2" + cooldownString + "§r");
            Component stats = Component.translatable("tooltip.end_aspected.stats");

            tooltip.add(ability);

            // Handle no cooldown in config
            if (ModConfig.enableShulkerWandCooldown.get()) {
                tooltip.add(stats);
                tooltip.add(cooldown);
            }
        } else {
            tooltip.add(Component.translatable("tooltip.end_aspected.more"));
        }
    }

}
