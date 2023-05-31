package net.jayugg.end_aspected.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.VoidBatEntity;
import net.jayugg.end_aspected.entity.model.VoidBatModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class VoidBatRenderer extends MobRenderer<VoidBatEntity, VoidBatModel<VoidBatEntity>> {
    private static final ResourceLocation BAT_TEXTURES = EndAspected.prefix("textures/entity/voidbat.png");

    public VoidBatRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VoidBatModel<>(), 0.25F);
        this.addLayer(new VoidBatGlowLayer<>(this));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(VoidBatEntity entity) {
        return BAT_TEXTURES;
    }

    protected void preRenderCallback(VoidBatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.35F, 0.35F, 0.35F);
    }

    protected void applyRotations(VoidBatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entityLiving.getIsBatHanging()) {
            matrixStackIn.translate(0.0D, -0.1F, 0.0D);
        } else {
            matrixStackIn.translate(0.0D, MathHelper.cos(ageInTicks * 0.3F) * 0.1F, 0.0D);
        }

        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }
}