package net.jayugg.end_aspected.util;

import com.mojang.authlib.GameProfile;
import net.jayugg.end_aspected.block.tree.VoidVeinBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

public interface IVoidVeinPlacer {
    GameProfile VOID_VEIN_PLACER_PROFILE = new GameProfile(UUID.randomUUID(), "void_vein_placer");

    default FakePlayer getFakePlayer(ServerWorld serverWorld) {
        return FakePlayerFactory.get(serverWorld, VOID_VEIN_PLACER_PROFILE);
    }

    default BlockItemUseContext getBlockItemUseContext(FakePlayer fakePlayer, Vector3d posVec, BlockPos groundPos) {
        return new BlockItemUseContext(
                new ItemUseContext(fakePlayer, Hand.MAIN_HAND, new BlockRayTraceResult(posVec, Direction.DOWN, groundPos, false))
        );
    }

    default void placeVeinAtPosition(ServerWorld serverWorld, BlockPos groundPos, VoidVeinBlock voidVeinBlock) {
        FakePlayer fakePlayer = getFakePlayer(serverWorld);
        Vector3d posVec = new Vector3d(groundPos.getX(), groundPos.getY(), groundPos.getZ());
        BlockItemUseContext context = getBlockItemUseContext(fakePlayer, posVec, groundPos);
        BlockState currentBlockState = serverWorld.getBlockState(groundPos);
        boolean flag = currentBlockState.getMaterial().isReplaceable() ||
                currentBlockState.matchesBlock(Blocks.WATER) ||
                currentBlockState.matchesBlock(voidVeinBlock);
        BlockState state = voidVeinBlock.getStateForPlacement(context);
        if (state != null && flag) {
            serverWorld.setBlockState(groundPos, state, 3); // Flags=3 for client update
        }
    }

}
