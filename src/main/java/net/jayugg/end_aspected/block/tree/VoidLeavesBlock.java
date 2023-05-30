package net.jayugg.end_aspected.block.tree;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Random;

public class VoidLeavesBlock extends LeavesBlock {
    public VoidLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (worldIn.rand.nextFloat() > 0.4f) {
            worldIn.addParticle(ParticleTypes.WARPED_SPORE, pos.getX() + 0.5f, pos.getY() + 0.1f, pos.getZ() + 0.5f, 0.0D, 0.0D, 0.0D);
        }
    }
}
