package net.jayugg.end_aspected.block.tree;

import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidVeinTreeDecorator extends TreeDecorator {
    public static final Codec<VoidVeinTreeDecorator> CODEC;
    public static final VoidVeinTreeDecorator DECORATOR = new VoidVeinTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getDecoratorType() {
        return ModTreeDecorators.VOID_LEAVE_VINE.get();
    }

    protected void placeVine(IWorldWriter world, BlockPos pos, BooleanProperty vineProperty, Set<BlockPos> decorations, MutableBoundingBox mutableBoundingBox) {
        this.func_227423_a_(world, pos, ModBlocks.VOID_VEIN.get().getDefaultState().with(vineProperty, true), decorations, mutableBoundingBox);
    }

    @Override
    public void func_225576_a_(ISeedReader reader, Random rand, List<BlockPos> logs, List<BlockPos> leaves, Set<BlockPos> decorations, MutableBoundingBox mutableBoundingBox) {
        leaves.forEach((leafBlock) -> {
            if (rand.nextInt(4) == 0) {
                BlockPos blockpos = leafBlock.west();
                if (Feature.isAirAt(reader, blockpos)) {
                    this.placeVineWithDirection(reader, blockpos, VoidVeinBlock.EAST, decorations, mutableBoundingBox);
                }
            }

            if (rand.nextInt(4) == 0) {
                BlockPos blockpos1 = leafBlock.east();
                if (Feature.isAirAt(reader, blockpos1)) {
                    this.placeVineWithDirection(reader, blockpos1, VoidVeinBlock.WEST, decorations, mutableBoundingBox);
                }
            }

            if (rand.nextInt(4) == 0) {
                BlockPos blockpos2 = leafBlock.north();
                if (Feature.isAirAt(reader, blockpos2)) {
                    this.placeVineWithDirection(reader, blockpos2, VoidVeinBlock.SOUTH, decorations, mutableBoundingBox);
                }
            }

            if (rand.nextInt(4) == 0) {
                BlockPos blockpos3 = leafBlock.south();
                if (Feature.isAirAt(reader, blockpos3)) {
                    this.placeVineWithDirection(reader, blockpos3, VoidVeinBlock.NORTH, decorations, mutableBoundingBox);
                }
            }

        });
    }

    private void placeVineWithDirection(IWorldGenerationReader worldGenReader, BlockPos pos, BooleanProperty vineFacingProperty, Set<BlockPos> decorations, MutableBoundingBox mutableBoundingBox) {
        this.placeVine(worldGenReader, pos, vineFacingProperty, decorations, mutableBoundingBox);
        int i = 4;

        for(BlockPos blockpos = pos.down(); Feature.isAirAt(worldGenReader, blockpos) && i > 0; --i) {
            this.placeVine(worldGenReader, blockpos, vineFacingProperty, decorations, mutableBoundingBox);
            blockpos = blockpos.down();
        }

    }

    static {
        CODEC = Codec.unit(() -> DECORATOR);
    }
}

