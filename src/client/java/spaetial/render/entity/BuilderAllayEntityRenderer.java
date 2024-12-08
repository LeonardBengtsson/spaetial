package spaetial.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import spaetial.Spaetial;
import spaetial.SpaetialClient;
import spaetial.entity.BuilderAllayEntity;

@Environment(EnvType.CLIENT)
public class BuilderAllayEntityRenderer extends MobEntityRenderer<BuilderAllayEntity, BuilderAllayEntityModel> {
    private static final Identifier TEXTURE = Spaetial.id("textures/entity/builder_allay/builder_allay.png");

    public BuilderAllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BuilderAllayEntityModel(context.getPart(SpaetialClient.MODEL_BUILDER_ALLAY_LAYER)), .5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(BuilderAllayEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(BuilderAllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
