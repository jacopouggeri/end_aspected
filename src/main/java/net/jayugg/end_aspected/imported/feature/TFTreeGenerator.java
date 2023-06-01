package net.jayugg.end_aspected.imported.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.imported.TFTreeFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.*;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TFTreeGenerator<T extends TFTreeFeatureConfig> extends Feature<T> {

    public TFTreeGenerator(Codec<T> configIn) {
        super(configIn);
    }

    protected boolean generate(IWorld world, Random random, BlockPos pos, Set<BlockPos> logpos, Set<BlockPos> leavespos, MutableBoundingBox mbb, T config) {
        Set<BlockPos> branchSet = Sets.newHashSet();
        Set<BlockPos> rootSet = Sets.newHashSet();
        return generate(world, random, pos, logpos, leavespos, branchSet, rootSet, mbb, config);
    }

    @Override
    public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, T config) {
        Set<BlockPos> logs = Sets.newHashSet();
        Set<BlockPos> leaves = Sets.newHashSet();
        MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
        boolean flag = this.generate(world, random, pos, logs, leaves, mutableboundingbox, config);
        if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !logs.isEmpty()) {
            VoxelShapePart voxelshapepart = this.getVoxelShapePart(world, mutableboundingbox, logs);
            Template.updatePostProcessing(world, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
            return true;
        } else {
            return false;
        }
    }

    //TreeFeature.func_227214_a_ copy, modified to remove decorations
    private VoxelShapePart getVoxelShapePart(IWorld world, MutableBoundingBox mbb, Set<BlockPos> logPosSet) {
        List<Set<BlockPos>> list = Lists.newArrayList();
        VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(mbb.getXSize(), mbb.getYSize(), mbb.getZSize());

        for(int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(BlockPos logPos : Lists.newArrayList(logPosSet)) {
            if (mbb.isVecInside(logPos)) {
                voxelshapepart.setFilled(logPos.getX() - mbb.minX, logPos.getY() - mbb.minY, logPos.getZ() - mbb.minZ, true, true);
            }

            for(Direction direction : Direction.values()) {
                mutable.setAndMove(logPos, direction);
                if (!logPosSet.contains(mutable)) {
                    BlockState blockstate = world.getBlockState(mutable);
                    if (blockstate.hasProperty(BlockStateProperties.DISTANCE_1_7)) {
                        list.get(0).add(mutable.toImmutable());
                        TreeFeature.setBlockStateWithoutUpdate(world, mutable, blockstate.with(BlockStateProperties.DISTANCE_1_7, 1));
                        if (mbb.isVecInside(mutable)) {
                            voxelshapepart.setFilled(mutable.getX() - mbb.minX, mutable.getY() - mbb.minY, mutable.getZ() - mbb.minZ, true, true);
                        }
                    }
                }
            }
        }

        for(int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for(BlockPos blockpos2 : set) {
                if (mbb.isVecInside(blockpos2)) {
                    voxelshapepart.setFilled(blockpos2.getX() - mbb.minX, blockpos2.getY() - mbb.minY, blockpos2.getZ() - mbb.minZ, true, true);
                }

                for(Direction direction1 : Direction.values()) {
                    mutable.setAndMove(blockpos2, direction1);
                    if (!set.contains(mutable) && !set1.contains(mutable)) {
                        BlockState blockstate1 = world.getBlockState(mutable);
                        if (blockstate1.hasProperty(BlockStateProperties.DISTANCE_1_7)) {
                            int k = blockstate1.get(BlockStateProperties.DISTANCE_1_7);
                            if (k > l + 1) {
                                BlockState blockstate2 = blockstate1.with(BlockStateProperties.DISTANCE_1_7, l + 1);
                                TreeFeature.setBlockStateWithoutUpdate(world, mutable, blockstate2);
                                if (mbb.isVecInside(mutable)) {
                                    voxelshapepart.setFilled(mutable.getX() - mbb.minX, mutable.getY() - mbb.minY, mutable.getZ() - mbb.minZ, true, true);
                                }

                                set1.add(mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelshapepart;
    }

    /**
     * This works akin to the AbstractTreeFeature.generate, but put our branches and roots here
     */
    protected abstract boolean generate(IWorld world, Random random, BlockPos pos, Set<BlockPos> logpos, Set<BlockPos> leavespos, Set<BlockPos> branchpos, Set<BlockPos> rootpos, MutableBoundingBox mbb, T config);

    // TODO should move to FeatureUtil
    public static boolean canRootGrowIn(IWorldReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        if (blockState == Blocks.AIR.getDefaultState()) {
            // roots can grow through air if they are near a solid block
            return FeatureUtil.isNearSolid(world, pos);
        } else {
            return (blockState.getBlockHardness(world, pos) >= 0)
                    && (blockState.getMaterial() == Material.ORGANIC || blockState.getMaterial() == Material.EARTH || blockState.getMaterial() == Material.ROCK || blockState.getMaterial() == Material.WATER);
        }
    }
}
