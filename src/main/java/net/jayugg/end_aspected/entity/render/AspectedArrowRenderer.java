package net.jayugg.end_aspected.entity.render;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class AspectedArrowRenderer extends ArrowRenderer<AspectedArrowEntity> {
    public static final ResourceLocation TEXTURE = EndAspected.prefix("textures/entity/aspected_arrow.png");

    public AspectedArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public @Nonnull ResourceLocation getEntityTexture(@Nonnull AspectedArrowEntity arrow) {
        return TEXTURE;
    }
}