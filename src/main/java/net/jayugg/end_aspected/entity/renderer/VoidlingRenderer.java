package net.jayugg.end_aspected.entity.renderer;

import net.jayugg.end_aspected.entity.VoidlingEntity;
import net.jayugg.end_aspected.entity.model.VoidlingModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import static net.jayugg.end_aspected.EndAspected.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class VoidlingRenderer extends MobRenderer<VoidlingEntity, VoidlingModel<VoidlingEntity>> {
    private static final ResourceLocation VOIDLING_LOCATION = new ResourceLocation(MOD_ID,"textures/entity/voidling.png");

    public VoidlingRenderer(EntityRendererProvider.Context p_173994_) {
        super(p_173994_, new VoidlingModel<>(p_173994_.bakeLayer(ModelLayers.ENDERMITE)), 0.3F);
        this.addLayer(new VoidlingEyesLayer<>(this));
    }

    protected float getFlipDegrees(VoidlingEntity pLivingEntity) {
        return 180.0F;
    }

    /**
     * Returns the location of an entity's texture.
     */
    @Override
    public @Nonnull ResourceLocation getTextureLocation(@Nonnull VoidlingEntity pEntity) {
        return VOIDLING_LOCATION;
    }
}
