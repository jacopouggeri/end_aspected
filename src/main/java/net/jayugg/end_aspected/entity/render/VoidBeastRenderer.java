package net.jayugg.end_aspected.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.VoidBeastEntity;
import net.jayugg.end_aspected.entity.model.VoidBeastModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class VoidBeastRenderer extends MobRenderer<VoidBeastEntity, VoidBeastModel<VoidBeastEntity>> {
    private static final ResourceLocation VOID_BEAST_LOCATION = EndAspected.prefix("textures/entity/void_beast.png");

    public VoidBeastRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VoidBeastModel<>(), 0.75F);
        this.addLayer(new VoidBeastEyesLayer<>(this));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(VoidBeastEntity entity) {
        return VOID_BEAST_LOCATION;
    }

    protected void preRenderCallback(VoidBeastEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        int i = entitylivingbaseIn.getPhantomSize();
        float f = 1.0F + 0.15F * (float)i;
        matrixStackIn.scale(f, f, f);
        matrixStackIn.translate(0.0D, 1.3125D, 0.1875D);
    }

    protected void applyRotations(VoidBeastEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityLiving.rotationPitch));
    }
}