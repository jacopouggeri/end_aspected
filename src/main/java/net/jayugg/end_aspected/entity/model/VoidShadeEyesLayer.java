package net.jayugg.end_aspected.entity.model;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidShadeEyesLayer<T extends Entity> extends AbstractEyesLayer<T, VoidShadeModel<T>> {
    private static final RenderType RENDER_TYPE = RenderType.getEyes(EndAspected.prefix("textures/entity/void:shade_eyes.png"));

    public VoidShadeEyesLayer(IEntityRenderer<T, VoidShadeModel<T>> model) {
        super(model);
    }

    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
