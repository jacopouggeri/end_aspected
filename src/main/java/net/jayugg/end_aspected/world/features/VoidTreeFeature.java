package net.jayugg.end_aspected.world.features;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.IConnectedFlora;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidTreeFeature extends Feature<BaseTreeFeatureConfig> {
    public VoidTreeFeature(Codec<BaseTreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean isLogsAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return isReplaceableAt(reader, pos) || reader.hasBlockState(pos, (state) -> state.isIn(BlockTags.LOGS));
    }

    private static boolean isVinesAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return reader.hasBlockState(pos, (state) -> state.matchesBlock(ModBlocks.VOID_VEIN.get()));
    }

    private static boolean isWaterAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return reader.hasBlockState(pos, (state) -> state.matchesBlock(Blocks.WATER));
    }

    public static boolean isAirOrLeavesAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return reader.hasBlockState(pos, (state) -> state.matchesBlock(Blocks.AIR) || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isTallPlantAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return reader.hasBlockState(pos, (state) -> {
            Material material = state.getMaterial();
            return material == Material.TALL_PLANTS;
        });
    }

    public static void setBlockStateWithoutUpdate(IWorldWriter writer, BlockPos pos, BlockState state) {
        writer.setBlockState(pos, state, 19);
    }

    public static boolean isReplaceableAt(IWorldGenerationBaseReader reader, BlockPos pos) {
        return isAirOrLeavesAt(reader, pos) || isTallPlantAt(reader, pos) || isWaterAt(reader, pos);
    }

    private boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> logPositions, Set<BlockPos> foliagePositions, MutableBoundingBox boundingBoxIn, BaseTreeFeatureConfig configIn) {
        int i = configIn.trunkPlacer.getHeight(rand);
        int j = configIn.foliagePlacer.func_230374_a_(rand, i, configIn);
        int k = i - j;
        int l = configIn.foliagePlacer.func_230376_a_(rand, k);
        BlockPos blockpos;
        if (!configIn.forcePlacement) {
            int i1 = generationReader.getHeight(Heightmap.Type.OCEAN_FLOOR, positionIn).getY();
            int j1 = generationReader.getHeight(Heightmap.Type.WORLD_SURFACE, positionIn).getY();
            if (j1 - i1 > configIn.maxWaterDepth) {
                return false;
            }

            int k1;
            if (configIn.heightmap == Heightmap.Type.OCEAN_FLOOR) {
                k1 = i1;
            } else if (configIn.heightmap == Heightmap.Type.WORLD_SURFACE) {
                k1 = j1;
            } else {
                k1 = generationReader.getHeight(configIn.heightmap, positionIn).getY();
            }

            blockpos = new BlockPos(positionIn.getX(), k1, positionIn.getZ());
        } else {
            blockpos = positionIn;
        }

        if (blockpos.getY() >= 1 && blockpos.getY() + i + 1 <= 256) {
            OptionalInt optionalint = configIn.minimumSize.func_236710_c_();
            int l1 = this.getMaxFreeTreeHeightAt(generationReader, i, blockpos, configIn);
            if (l1 >= i || optionalint.isPresent() && l1 >= optionalint.getAsInt()) {
                List<FoliagePlacer.Foliage> list = configIn.trunkPlacer.getFoliages(generationReader, rand, l1, blockpos, logPositions, boundingBoxIn, configIn);
                list.forEach((foliage) -> configIn.foliagePlacer.func_236752_a_(generationReader, rand, configIn, l1, foliage, j, l, foliagePositions, boundingBoxIn));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private int getMaxFreeTreeHeightAt(IWorldGenerationBaseReader reader, int trunkHeight, BlockPos topPosition, BaseTreeFeatureConfig config) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int i = 0; i <= trunkHeight + 1; ++i) {
            int j = config.minimumSize.func_230369_a_(trunkHeight, i);

            for(int k = -j; k <= j; ++k) {
                for(int l = -j; l <= j; ++l) {
                    blockpos$mutable.setAndOffset(topPosition, k, i, l);
                    if (!isLogsAt(reader, blockpos$mutable) || !config.ignoreVines && isVinesAt(reader, blockpos$mutable)) {
                        return i - 2;
                    }
                }
            }
        }

        return trunkHeight;
    }

    protected void setBlockState(IWorldWriter world, BlockPos pos, BlockState state) {
        setBlockStateWithoutUpdate(world, pos, state);
    }

    public final boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, BaseTreeFeatureConfig config) {
        Set<BlockPos> set = Sets.newHashSet();
        Set<BlockPos> set1 = Sets.newHashSet();
        Set<BlockPos> set2 = Sets.newHashSet();
        MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
        boolean flag = this.place(reader, rand, pos, set, set1, mutableboundingbox, config);
        if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !set.isEmpty()) {
            if (!config.decorators.isEmpty()) {
                List<BlockPos> list = Lists.newArrayList(set);
                List<BlockPos> list1 = Lists.newArrayList(set1);
                list.sort(Comparator.comparingInt(Vector3i::getY));
                list1.sort(Comparator.comparingInt(Vector3i::getY));
                config.decorators.forEach((decorator) -> decorator.func_225576_a_(reader, rand, list, list1, set2, mutableboundingbox));
            }

            VoxelShapePart voxelshapepart = this.getFoliageGrowthArea(reader, mutableboundingbox, set, set2);
            Template.updatePostProcessing(reader, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
            return true;
        } else {
            return false;
        }
    }

    private VoxelShapePart getFoliageGrowthArea(IWorld world, MutableBoundingBox boundingBox, Set<BlockPos> logPositions, Set<BlockPos> foliagePositions) {
        List<Set<BlockPos>> list = Lists.newArrayList();
        VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(boundingBox.getXSize(), boundingBox.getYSize(), boundingBox.getZSize());

        for(int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(BlockPos blockpos : Lists.newArrayList(foliagePositions)) {
            if (boundingBox.isVecInside(blockpos)) {
                voxelshapepart.setFilled(blockpos.getX() - boundingBox.minX, blockpos.getY() - boundingBox.minY, blockpos.getZ() - boundingBox.minZ, true, true);
            }
        }

        for(BlockPos blockpos1 : Lists.newArrayList(logPositions)) {
            if (boundingBox.isVecInside(blockpos1)) {
                voxelshapepart.setFilled(blockpos1.getX() - boundingBox.minX, blockpos1.getY() - boundingBox.minY, blockpos1.getZ() - boundingBox.minZ, true, true);
            }

            for(Direction direction : Direction.values()) {
                blockpos$mutable.setAndMove(blockpos1, direction);
                if (!logPositions.contains(blockpos$mutable)) {
                    BlockState blockstate = world.getBlockState(blockpos$mutable);
                    if (blockstate.hasProperty(IConnectedFlora.DISTANCE)) {
                        list.get(0).add(blockpos$mutable.toImmutable());
                        setBlockStateWithoutUpdate(world, blockpos$mutable, blockstate.with(IConnectedFlora.DISTANCE, 1));
                        if (boundingBox.isVecInside(blockpos$mutable)) {
                            voxelshapepart.setFilled(blockpos$mutable.getX() - boundingBox.minX, blockpos$mutable.getY() - boundingBox.minY, blockpos$mutable.getZ() - boundingBox.minZ, true, true);
                        }
                    }
                }
            }
        }

        for(int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for(BlockPos blockpos2 : set) {
                if (boundingBox.isVecInside(blockpos2)) {
                    voxelshapepart.setFilled(blockpos2.getX() - boundingBox.minX, blockpos2.getY() - boundingBox.minY, blockpos2.getZ() - boundingBox.minZ, true, true);
                }

                for(Direction direction1 : Direction.values()) {
                    blockpos$mutable.setAndMove(blockpos2, direction1);
                    if (!set.contains(blockpos$mutable) && !set1.contains(blockpos$mutable)) {
                        BlockState blockstate1 = world.getBlockState(blockpos$mutable);
                        if (blockstate1.hasProperty(IConnectedFlora.DISTANCE)) {
                            int k = blockstate1.get(IConnectedFlora.DISTANCE);
                            if (k > l + 1) {
                                BlockState blockstate2 = blockstate1.with(IConnectedFlora.DISTANCE, Integer.valueOf(l + 1));
                                setBlockStateWithoutUpdate(world, blockpos$mutable, blockstate2);
                                if (boundingBox.isVecInside(blockpos$mutable)) {
                                    voxelshapepart.setFilled(blockpos$mutable.getX() - boundingBox.minX, blockpos$mutable.getY() - boundingBox.minY, blockpos$mutable.getZ() - boundingBox.minZ, true, true);
                                }

                                set1.add(blockpos$mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelshapepart;
    }

}
