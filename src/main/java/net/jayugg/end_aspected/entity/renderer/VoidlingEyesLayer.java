package net.jayugg.end_aspected.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jayugg.end_aspected.entity.model.VoidlingModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class VoidlingEyesLayer<T extends Entity, M extends VoidlingModel<T>> extends EyesLayer<T, M> {
    private static final RenderType VOIDLING_EYES = RenderType.entityCutoutNoCull(new ResourceLocation(MOD_ID, "textures/entity/voidling_eyes.png"));

    public VoidlingEyesLayer(RenderLayerParent<T, M> p_117507_) {
        super(p_117507_);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderSystem.setShaderTexture(0, new ResourceLocation(MOD_ID, "textures/entity/voidling_eyes.png"));
        RenderSystem.setupDefaultState(0, 0, 0, 255);
        super.render(matrixStackIn, bufferIn, 15728640, entityIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    public @Nonnull RenderType renderType() {
        return VOIDLING_EYES;
    }
}