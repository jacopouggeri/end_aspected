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
public class VoidChargeParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteSet;
    private final float friction;

    protected VoidChargeParticle(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IAnimatedSprite spriteSet) {
        super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.friction = 0.96F;
        this.spriteSet = spriteSet;
        this.particleScale *= 1.5F;
        this.maxAge = 12 + this.rand.nextInt(8);
        this.canCollide = false;
        this.selectSpriteWithAge(spriteSet);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
    }

    public void tick() {
        super.tick();
        this.selectSpriteWithAge(this.spriteSet);
        this.motionX *= this.friction;
        this.motionY *= this.friction;
        this.motionZ *= this.friction;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VoidChargeParticle particle = new VoidChargeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
            particle.setAlphaF(1.0F);
            return particle;
        }
    }
}
