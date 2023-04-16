package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.EndAspected;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class AspectedArrowRenderer extends ArrowRenderer<AspectedArrowEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(EndAspected.MOD_ID, "textures/entity/aspected_arrow.png");

    public AspectedArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public @Nonnull ResourceLocation getEntityTexture(@Nonnull AspectedArrowEntity arrow) {
        return TEXTURE;
    }
}