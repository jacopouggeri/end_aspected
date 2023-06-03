package net.jayugg.end_aspected.block.tree.decorator;

import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.tree.VoidVeinBlock;
import net.jayugg.end_aspected.util.IVoidVeinPlacer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
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
public class VoidVeinTreeDecorator extends TreeDecorator implements IVoidVeinPlacer {
    public static final Codec<VoidVeinTreeDecorator> CODEC;
    public static final VoidVeinTreeDecorator DECORATOR = new VoidVeinTreeDecorator();

    int baseHeight = 0;

    @Override
    protected TreeDecoratorType<?> getDecoratorType() {
        return ModTreeDecorators.VOID_LEAVE_VINE.get();
    }

    protected void placeVine(IWorldWriter world, BlockPos pos, BooleanProperty vineProperty, Set<BlockPos> decorations, MutableBoundingBox mutableBoundingBox) {
        this.func_227423_a_(world, pos, ModBlocks.VOID_VEIN.get().getDefaultState().with(vineProperty, true), decorations, mutableBoundingBox);
    }

    public VoidVeinTreeDecorator setBaseHeight(int baseHeight) {
        this.baseHeight = baseHeight;
        return this;
    }

    @Override
    public void func_225576_a_(ISeedReader reader, Random rand, List<BlockPos> logs, List<BlockPos> leaves, Set<BlockPos> decorations, MutableBoundingBox mutableBoundingBox) {
        // Place veins around base
        if (!logs.isEmpty()) {
            BlockPos basePos = logs.get(0).down(baseHeight);
            placeVoidVeins(reader, basePos, rand);
        }
        // Place veins on leaves
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

    private void placeVoidVeins(IWorld world, BlockPos blockPos, Random rand) {
        int range = 4;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                // Adding a random offset to the distance calculation
                double distanceToTree = Math.sqrt(dx * dx + dz * dz) + (rand.nextFloat() * 2.0F - 1.0F);
                if (distanceToTree <= range) {
                    BlockPos groundPos = blockPos.add(dx, 0, dz);
                    placeVeinAtPosition(world, groundPos);
                }
            }
        }
    }

    static {
        CODEC = Codec.unit(() -> DECORATOR);
    }
}

