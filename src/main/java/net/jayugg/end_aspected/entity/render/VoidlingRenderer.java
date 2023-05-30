package net.jayugg.end_aspected.entity.render;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.VoidlingEntity;
import net.jayugg.end_aspected.entity.model.VoidlingModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

public class VoidlingRenderer extends MobRenderer<VoidlingEntity, VoidlingModel<VoidlingEntity>> {
    protected static final ResourceLocation TEXTURE = EndAspected.prefix("textures/entity/voidling.png");
    public VoidlingRenderer(EntityRendererManager manager) {
        super(manager, new VoidlingModel<>(), 0.3F);
        this.addLayer(new VoidlingEyesLayer<>(this));
    }

    protected float getDeathMaxRotation(@Nonnull VoidlingEntity entityLivingBaseIn) {
        return 180.0F;
    }

    @Override
    public @Nonnull ResourceLocation getEntityTexture(@Nonnull VoidlingEntity entity) {
        return TEXTURE;
    }
}
