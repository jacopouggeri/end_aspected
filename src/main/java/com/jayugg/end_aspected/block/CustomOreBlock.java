package com.jayugg.end_aspected.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class CustomOreBlock extends OreBlock {
    public CustomOreBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    public int getExperience(Random rand) {
        if (this == Blocks.COAL_ORE) {
            return MathHelper.nextInt(rand, 0, 2);
        } else if (this == Blocks.DIAMOND_ORE) {
            return MathHelper.nextInt(rand, 3, 7);
        } else if (this == Blocks.EMERALD_ORE) {
            return MathHelper.nextInt(rand, 3, 7);
        } else if (this == Blocks.LAPIS_ORE) {
            return MathHelper.nextInt(rand, 2, 5);
        } else if (this == Blocks.NETHER_QUARTZ_ORE) {
            return MathHelper.nextInt(rand, 2, 5);
        } else {
            return this == Blocks.NETHER_GOLD_ORE ? MathHelper.nextInt(rand, 0, 1) : 0;
        }
    }
}
