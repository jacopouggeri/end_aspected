package net.jayugg.end_aspected.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.entity.model.VoidBeastModel;
import net.jayugg.end_aspected.entity.model.VoidShadeModel;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class VoidBeastEyesLayer<T extends Entity> extends AbstractEyesLayer<T, VoidBeastModel<T>> {
    private static final RenderType RENDER_TYPE = RenderType.makeType(
            "void_beast_eyes",
            DefaultVertexFormats.ENTITY,
            GL11.GL_QUADS, 256,
            RenderType.State.getBuilder()
                    .texture(new RenderState.TextureState(EndAspected.prefix("textures/entity/void_beast_eyes.png"), false, false))
                    .transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultAlphaFunc();
                    }))
                    .build(false)
    );


    public VoidBeastEyesLayer(IEntityRenderer<T, VoidBeastModel<T>> model) {
        super(model);
    }

    @Nonnull
    public RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
