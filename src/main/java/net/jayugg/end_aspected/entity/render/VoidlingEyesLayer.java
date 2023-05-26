package net.jayugg.end_aspected.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.jayugg.end_aspected.entity.model.VoidlingModel;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class VoidlingEyesLayer<T extends Entity, M extends VoidlingModel<T>> extends AbstractEyesLayer<T, M> {
    private static final RenderType RENDER_TYPE = RenderType.makeType(
            "voidling_eyes",
            DefaultVertexFormats.ENTITY,
            GL11.GL_QUADS, 256,
            RenderType.State.getBuilder()
                    .texture(new RenderState.TextureState(new ResourceLocation(MOD_ID, "textures/entity/voidling_eyes.png"), false, false))
                    .transparency(new RenderState.TransparencyState("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultAlphaFunc();
                    }))
                    .build(false)
    );


    public VoidlingEyesLayer(IEntityRenderer<T, M> rendererIn) {
        super(rendererIn);
    }

    public @Nonnull RenderType getRenderType() {
        return RENDER_TYPE;
    }
}
