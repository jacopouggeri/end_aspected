package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.EndAspected;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class AspectedArrowRenderer extends ArrowRenderer<AspectedArrowEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(EndAspected.MOD_ID, "textures/entity/aspected_arrow.png");

    public AspectedArrowRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    public @Nonnull ResourceLocation getTextureLocation(@Nonnull AspectedArrowEntity arrow) {
        return TEXTURE;
    }
}