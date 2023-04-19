package com.jayugg.end_aspected.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayugg.end_aspected.item.ModItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModTrades{
    public static void fillTradeData(){
        MultiItemsForEmeralds shardOffer1 = new MultiItemsForEmeralds(ImmutableList.of(ModItems.ASPECT_SHARD.get()), ImmutableList.of(1, 1, 1), ImmutableList.of(20, 22, 25), 2, 10);
        MultiItemsForEmeralds shardOffer2 = new MultiItemsForEmeralds(ImmutableList.of(ModItems.ASPECT_SHARD.get()), ImmutableList.of(1, 2, 4), ImmutableList.of(15, 25, 30), 4, 20);
        MultiItemsForEmeralds swordOffer = new MultiItemsForEmeralds(ImmutableList.of(ModItems.ASPECT_OF_THE_END.get(), ModItems.ASPECT_OF_THE_END.get(), ModItems.SHULKER_WAND.get()), ImmutableList.of(1, 1, 1), ImmutableList.of(75, 90, 150), 1, 50);

        VillagerTrades.ItemListing[] level1 = new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(ModItems.ASPECT_SHARD.get(), 5, 3, 10), new VillagerTrades.EmeraldForItems(Items.COAL, 15, 16, 2), new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2F), new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_SWORD, 2, 3, 1)};
        VillagerTrades.ItemListing[] level2 = new VillagerTrades.ItemListing[]{shardOffer1, new VillagerTrades.EmeraldForItems(ModItems.ASPECT_SHARD.get(), 10, 10, 10), new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)};
        VillagerTrades.ItemListing[] level3 = new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(ModItems.ASPECT_SHARD.get(), 10, 10, 10), swordOffer, new VillagerTrades.EmeraldForItems(Items.FLINT, 24, 12, 20)};
        VillagerTrades.ItemListing[] level4 = new VillagerTrades.ItemListing[]{shardOffer2, new VillagerTrades.EmeraldForItems(Items.DIAMOND, 1, 12, 30), new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2F)};
        VillagerTrades.ItemListing[] level5 = new VillagerTrades.ItemListing[]{swordOffer, new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_SWORD, 8, 3, 30, 0.2F)};

        VillagerTrades.TRADES.put(VillagerProfession.WEAPONSMITH, gatAsIntMap(ImmutableMap.of(1, level1, 2, level2, 3, level3, 4, level4, 5, level5)));
    }
    private static Int2ObjectMap<VillagerTrades.ItemListing[]> gatAsIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> p_221238_0_) {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    public static class MultiItemsForEmeralds implements VillagerTrades.ItemListing {
        private final List<Item> items;
        private final List<Integer> amount;
        private final List<Integer> cost;
        private final int uses;
        private final int villagerExp;
        public MultiItemsForEmeralds(List<Item> items, List<Integer> amount, List<Integer> cost, int uses, int villagerExp) {

            this.items = items;
            this.amount = amount;
            this.cost = cost;
            this.uses = uses;
            this.villagerExp = villagerExp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@Nonnull Entity trader, RandomSource random) {
            int choose = (int) (random.nextFloat() * items.size());
            return new MerchantOffer(new ItemStack(Items.EMERALD, cost.get(choose)), new ItemStack(items.get(choose), amount.get(0)), this.uses, this.villagerExp, 0.05f);
        }

    }
}
