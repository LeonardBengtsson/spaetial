package spaetial.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import spaetial.Spaetial;
import spaetial.SpaetialClient;
import spaetial.entity.BuilderAllayEntity;

@Environment(EnvType.CLIENT)
public class BuilderAllayEntityRenderer extends MobEntityRenderer<BuilderAllayEntity, BuilderAllayEntityRenderState, BuilderAllayEntityModel> {
    private static final Identifier TEXTURE = Spaetial.id("textures/entity/builder_allay/builder_allay.png");

    public BuilderAllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BuilderAllayEntityModel(context.getPart(SpaetialClient.MODEL_BUILDER_ALLAY_LAYER)), .5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(BuilderAllayEntityRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public BuilderAllayEntityRenderState createRenderState() { return new BuilderAllayEntityRenderState(); }

    @Override
    public void updateRenderState(BuilderAllayEntity entity, BuilderAllayEntityRenderState renderState, float f) {
        super.updateRenderState(entity, renderState, f);
        ArmedEntityRenderState.updateRenderState(entity, renderState, this.itemModelResolver);
        renderState.itemHoldAnimationTicks = entity.getItemHoldAnimationTicks(f);
    }

    @Override
    protected int getBlockLight(BuilderAllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
