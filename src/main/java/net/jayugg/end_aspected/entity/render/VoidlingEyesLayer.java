package net.jayugg.end_aspected.entity.render;

import net.jayugg.end_aspected.entity.model.VoidlingModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class VoidlingEyesLayer<T extends Entity, M extends VoidlingModel<T>> extends AbstractEyesLayer<T, M> {
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation("textures/entity/voidling_eyes.png"));

    public VoidlingEyesLayer(IEntityRenderer<T, M> rendererIn) {
        super(rendererIn);
    }

    public @Nonnull RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
