package net.jayugg.end_aspected.imported.feature;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.TreeFeature;

import java.util.Random;
import java.util.Set;

public class FeatureUtil {
    public static void putLeafBlock(IWorldGenerationReader world, Random random, BlockPos pos, BlockStateProvider state, Set<BlockPos> leavesPos) {
        if (/*leavesPos.contains(pos) ||*/ !TreeFeature.isReplaceableAt(world, pos))
            return;

        world.setBlockState(pos, state.getBlockState(random, pos), 3);
        leavesPos.add(pos.toImmutable());
    }


    // TODO Determine if we should cut this method
    public static void makeLeafSpheroid(IWorldGenerationReader world, Random random, BlockPos centerPos, float xzRadius, float yRadius, float verticalBias, BlockStateProvider state, Set<BlockPos> leaves) {
        float xzRadiusSquared = xzRadius * xzRadius;
        float yRadiusSquared = yRadius * yRadius;
        float superRadiusSquared = xzRadiusSquared * yRadiusSquared;
        putLeafBlock(world, random, centerPos, state, leaves);

        for (int y = 0; y <= yRadius; y++) {
            if (y > yRadius) continue;

            putLeafBlock(world, random, centerPos.add( 0,  y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0,  y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0,  y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0,  y, 0), state, leaves);

            putLeafBlock(world, random, centerPos.add( 0, -y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0, -y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0, -y, 0), state, leaves);
            putLeafBlock(world, random, centerPos.add( 0, -y, 0), state, leaves);
        }

        for (int x = 0; x <= xzRadius; x++) {
            for (int z = 1; z <= xzRadius; z++) {
                if (x * x + z * z > xzRadiusSquared) continue;

                putLeafBlock(world, random, centerPos.add(  x, 0,  z), state, leaves);
                putLeafBlock(world, random, centerPos.add( -x, 0, -z), state, leaves);
                putLeafBlock(world, random, centerPos.add( -z, 0,  x), state, leaves);
                putLeafBlock(world, random, centerPos.add(  z, 0, -x), state, leaves);

                for (int y = 1; y <= yRadius; y++) {
                    float xzSquare = ((x * x + z * z) * yRadiusSquared);

                    if (xzSquare + (((y - verticalBias) * (y - verticalBias)) * xzRadiusSquared) <= superRadiusSquared) {
                        putLeafBlock(world, random, centerPos.add(  x,  y,  z), state, leaves);
                        putLeafBlock(world, random, centerPos.add( -x,  y, -z), state, leaves);
                        putLeafBlock(world, random, centerPos.add( -z,  y,  x), state, leaves);
                        putLeafBlock(world, random, centerPos.add(  z,  y, -x), state, leaves);
                    }

                    if (xzSquare + (((y + verticalBias) * (y + verticalBias)) * xzRadiusSquared) <= superRadiusSquared) {
                        putLeafBlock(world, random, centerPos.add(  x, -y,  z), state, leaves);
                        putLeafBlock(world, random, centerPos.add( -x, -y, -z), state, leaves);
                        putLeafBlock(world, random, centerPos.add( -z, -y,  x), state, leaves);
                        putLeafBlock(world, random, centerPos.add(  z, -y, -x), state, leaves);
                    }
                }
            }
        }
    }

    /*
     * Draws a line from {x1, y1, z1} to {x2, y2, z2}
     * This takes all variables for setting Branch
     */
    // ===== Pre-1.16.2 below =========================================================================================

    /**
     * Moves distance along the vector.
     * <p>
     * This goofy function takes a float between 0 and 1 for the angle, where 0 is 0 degrees, .5 is 180 degrees and 1 and 360 degrees.
     * For the tilt, it takes a float between 0 and 1 where 0 is straight up, 0.5 is straight out and 1 is straight down.
     */
    public static BlockPos translate(BlockPos pos, double distance, double angle, double tilt) {
        double rangle = angle * 2.0D * Math.PI;
        double rtilt = tilt * Math.PI;

        return pos.add(
                Math.round(Math.sin(rangle) * Math.sin(rtilt) * distance),
                Math.round(Math.cos(rtilt) * distance),
                Math.round(Math.cos(rangle) * Math.sin(rtilt) * distance)
        );
    }

    /**
     * Get an array of values that represent a line from point A to point B
     */
    public static BlockPos[] getBresenhamArrays(BlockPos src, BlockPos dest) {
        return getBresenhamArrays(src.getX(), src.getY(), src.getZ(), dest.getX(), dest.getY(), dest.getZ());
    }

    /**
     * Get an array of values that represent a line from point A to point B
     * todo 1.9 lazify this into an iterable?
     */
    public static BlockPos[] getBresenhamArrays(int x1, int y1, int z1, int x2, int y2, int z2) {
        int i, dx, dy, dz, absDx, absDy, absDz, x_inc, y_inc, z_inc, err_1, err_2, doubleAbsDx, doubleAbsDy, doubleAbsDz;

        BlockPos pixel = new BlockPos(x1, y1, z1);
        BlockPos lineArray[];

        dx = x2 - x1;
        dy = y2 - y1;
        dz = z2 - z1;
        x_inc = (dx < 0) ? -1 : 1;
        absDx = Math.abs(dx);
        y_inc = (dy < 0) ? -1 : 1;
        absDy = Math.abs(dy);
        z_inc = (dz < 0) ? -1 : 1;
        absDz = Math.abs(dz);
        doubleAbsDx = absDx << 1;
        doubleAbsDy = absDy << 1;
        doubleAbsDz = absDz << 1;

        if ((absDx >= absDy) && (absDx >= absDz)) {
            err_1 = doubleAbsDy - absDx;
            err_2 = doubleAbsDz - absDx;
            lineArray = new BlockPos[absDx + 1];
            for (i = 0; i < absDx; i++) {
                lineArray[i] = pixel;
                if (err_1 > 0) {
                    pixel = pixel.up(y_inc);
                    err_1 -= doubleAbsDx;
                }
                if (err_2 > 0) {
                    pixel = pixel.south(z_inc);
                    err_2 -= doubleAbsDx;
                }
                err_1 += doubleAbsDy;
                err_2 += doubleAbsDz;
                pixel = pixel.east(x_inc);
            }
        } else if ((absDy >= absDx) && (absDy >= absDz)) {
            err_1 = doubleAbsDx - absDy;
            err_2 = doubleAbsDz - absDy;
            lineArray = new BlockPos[absDy + 1];
            for (i = 0; i < absDy; i++) {
                lineArray[i] = pixel;
                if (err_1 > 0) {
                    pixel = pixel.east(x_inc);
                    err_1 -= doubleAbsDy;
                }
                if (err_2 > 0) {
                    pixel = pixel.south(z_inc);
                    err_2 -= doubleAbsDy;
                }
                err_1 += doubleAbsDx;
                err_2 += doubleAbsDz;
                pixel = pixel.up(y_inc);
            }
        } else {
            err_1 = doubleAbsDy - absDz;
            err_2 = doubleAbsDx - absDz;
            lineArray = new BlockPos[absDz + 1];
            for (i = 0; i < absDz; i++) {
                lineArray[i] = pixel;
                if (err_1 > 0) {
                    pixel = pixel.up(y_inc);
                    err_1 -= doubleAbsDz;
                }
                if (err_2 > 0) {
                    pixel = pixel.east(x_inc);
                    err_2 -= doubleAbsDz;
                }
                err_1 += doubleAbsDy;
                err_2 += doubleAbsDx;
                pixel = pixel.south(z_inc);
            }
        }
        lineArray[lineArray.length - 1] = pixel;

        return lineArray;
    }


    /**
     * Does the block have at least 1 air block adjacent
     */
    private static final Direction[] directionsExceptDown = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public static boolean hasAirAround(IWorld world, BlockPos pos) {
        for (Direction e : directionsExceptDown) {
            if (world.isAirBlock(pos.offset(e))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNearSolid(IWorldReader world, BlockPos pos) {
        for (Direction e : Direction.values()) {
            if (world.isBlockLoaded(pos.offset(e))
                    && world.getBlockState(pos.offset(e)).getMaterial().isSolid()) {
                return true;
            }
        }

        return false;
    }

}