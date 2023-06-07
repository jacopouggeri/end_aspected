package net.jayugg.end_aspected.particle;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class VoidChargePopParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteSet;
    private final float friction;

    VoidChargePopParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IAnimatedSprite spriteSet) {
        super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.friction = 0.96F;
        this.spriteSet = spriteSet;
        this.particleScale *= 1.0F;
        this.canCollide = false;
        this.selectSpriteWithAge(spriteSet);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.selectSpriteWithAge(this.spriteSet);
        // Apply friction to the particle motion
        this.motionX *= this.friction;
        this.motionY *= this.friction;
        this.motionZ *= this.friction;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VoidChargePopParticle particle = new VoidChargePopParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setAlphaF(1.0F);
            particle.setMaxAge(worldIn.rand.nextInt(4) + 6);
            return particle;
        }
    }

}
