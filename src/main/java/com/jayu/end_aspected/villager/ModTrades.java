package com.jayu.end_aspected.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.jayu.end_aspected.item.ModItems.*;

public class ModTrades {
    public static void fillTradeData(){
        MultiItemsForEmeraldsTrade shardOffer1 = new MultiItemsForEmeraldsTrade(ImmutableList.of(ASPECT_SHARD.get()), ImmutableList.of(1, 1, 1), ImmutableList.of(10, 20, 22), 2, 10);
        MultiItemsForEmeraldsTrade shardOffer2 = new MultiItemsForEmeraldsTrade(ImmutableList.of(ASPECT_SHARD.get()), ImmutableList.of(1, 2, 3), ImmutableList.of(10, 10, 23), 4, 10);
        MultiItemsForEmeraldsTrade swordOffer = new MultiItemsForEmeraldsTrade(ImmutableList.of(ASPECT_SHARD.get(), ASPECT_OF_THE_END.get(), ASPECT_OF_THE_END.get(), SHULKER_WAND.get()), ImmutableList.of(10, 1, 1, 1), ImmutableList.of(20, 50, 75, 100), 1, 40);

        VillagerTrades.ITrade[] level1 = new VillagerTrades.ITrade[]{new VillagerTrades.EmeraldForItemsTrade(ASPECT_SHARD.get(), 5, 3, 3)};
        VillagerTrades.ITrade[] level2 = new VillagerTrades.ITrade[]{shardOffer1, new VillagerTrades.EmeraldForItemsTrade(ASPECT_SHARD.get(), 10, 10, 5)};
        VillagerTrades.ITrade[] level3 = new VillagerTrades.ITrade[]{shardOffer2, new VillagerTrades.EmeraldForItemsTrade(ASPECT_SHARD.get(), 10, 10, 5), swordOffer};
        VillagerTrades.ITrade[] level4 = new VillagerTrades.ITrade[]{swordOffer};
        VillagerTrades.ITrade[] level5 = new VillagerTrades.ITrade[]{swordOffer};

        VillagerTrades.VILLAGER_DEFAULT_TRADES.put(VillagerProfession.WEAPONSMITH, gatAsIntMap(ImmutableMap.of(1, level1, 2, level2, 3, level3, 4, level4, 5, level5)));
    }
    private static Int2ObjectMap<VillagerTrades.ITrade[]> gatAsIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_221238_0_) {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    public static class MultiItemsForEmeraldsTrade implements VillagerTrades.ITrade {
        private final List<Item> items;
        private final List<Integer> amount;
        private final List<Integer> cost;
        private final int uses;
        private final int villagerExp;
        public MultiItemsForEmeraldsTrade(List<Item> items, List<Integer> amount, List<Integer> cost, int uses, int villagerExp) {

            this.items = items;
            this.amount = amount;
            this.cost = cost;
            this.uses = uses;
            this.villagerExp = villagerExp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(@Nonnull Entity trader, Random random) {
            int choose = (int) (random.nextFloat() * items.size());
            return new MerchantOffer(new ItemStack(Items.EMERALD, cost.get(choose)), new ItemStack(items.get(choose), amount.get(0)), this.uses, this.villagerExp, 0.05f);
        }

    }
}
