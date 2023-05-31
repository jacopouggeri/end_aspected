package net.jayugg.end_aspected.entity.render;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.VoidMiteEntity;
import net.jayugg.end_aspected.entity.model.VoidMiteModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class VoidMiteRenderer extends MobRenderer<VoidMiteEntity, VoidMiteModel<VoidMiteEntity>> {
    protected static final ResourceLocation TEXTURE = EndAspected.prefix("textures/entity/voidmite.png");
    public VoidMiteRenderer(EntityRendererManager manager) {
        super(manager, new VoidMiteModel<>(), 0.3F);
        this.addLayer(new VoidMiteEyesLayer<>(this));
    }

    protected float getDeathMaxRotation(@Nonnull VoidMiteEntity entityLivingBaseIn) {
        return 180.0F;
    }

    @Override
    public @Nonnull ResourceLocation getEntityTexture(@Nonnull VoidMiteEntity entity) {
        return TEXTURE;
    }
}
