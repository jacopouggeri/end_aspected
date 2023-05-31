package net.jayugg.end_aspected.block.parent;

import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MultiFaceBlock extends Block {
    public static final BooleanProperty DOWN = SixWayBlock.DOWN;
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP;
    private final Map<BlockState, VoxelShape> stateToShapeMap;

    private static final double FACE_THICKNESS = 0.2;
    public MultiFaceBlock(Properties properties) {
        super(properties.tickRandomly());
        this.setDefaultState(this.stateContainer.getBaseState().with(DOWN, false).with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
        this.stateToShapeMap = ImmutableMap.copyOf(this.stateContainer.getValidStates().stream().collect(Collectors.toMap(Function.identity(), MultiFaceBlock::getShapeForState)));
    }

    public static VoxelShape getAABBForDirection(Direction direction, double thickness) {
        switch (direction) {
            case DOWN:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, thickness, 16.0D);
            case UP:
                return Block.makeCuboidShape(0.0D, 16.0D - thickness, 0.0D, 16.0D, 16.0D, 16.0D);
            case NORTH:
                return Block.makeCuboidShape(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
            case SOUTH:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
            case WEST:
                return Block.makeCuboidShape(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            case EAST:
                return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D);
            default:
                return VoxelShapes.empty();  // return an empty VoxelShape for invalid input
        }
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelshape = VoxelShapes.empty();
        for (Direction direction : FACING_TO_PROPERTY_MAP.keySet()) {
            if (state.get(FACING_TO_PROPERTY_MAP.get(direction))) {
                if (isCardinal(direction))
                    voxelshape = VoxelShapes.or(voxelshape, getAABBForDirection(direction.getOpposite(), FACE_THICKNESS));
                else
                    voxelshape = VoxelShapes.or(voxelshape, getAABBForDirection(direction, FACE_THICKNESS));
            }
        }
        return voxelshape;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.stateToShapeMap.get(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        boolean flag = blockstate.matchesBlock(this);
        BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

        for (Direction direction : context.getNearestLookingDirections()) {
            BooleanProperty booleanProperty = getPropertyFor(direction);
            boolean flag1 = flag && blockstate.get(booleanProperty);
            if (!flag1) {
                blockstate1 = blockstate1.with(booleanProperty, true);
            }
        }

        return blockstate1;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, EAST, SOUTH, WEST);
    }

    /*
    Return true if the blockstate has at least one face attached (at least one of the directins is true)
     */
    protected boolean getPresentFaces(BlockState state) {
        return this.countPresentFaces(state) > 0;
    }

    /*
    Count how many faces the vine is attached (0-6), one for each direction
     */
    protected int countPresentFaces(BlockState state) {
        int i = 0;

        for(BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values()) {
            if (state.get(booleanproperty)) {
                ++i;
            }
        }

        return i;
    }

    private static boolean isCardinal(Direction direction) {
        return direction.getAxis().isHorizontal();
    }

    public static BooleanProperty getPropertyFor(Direction side) {
        return FACING_TO_PROPERTY_MAP.get(side);
    }
}

