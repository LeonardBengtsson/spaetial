package spaetial.render.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BuilderAllayEntityModel extends EntityModel<BuilderAllayEntityRenderState> implements ModelWithArms {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public BuilderAllayEntityModel(ModelPart root) {
        super(root.getChild("root"), RenderLayer::getEntityTranslucent);
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.5F, 0.0F));
        modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.99F, 0.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 10).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F)).uv(0, 16).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, -4.0F, 0.0F));
        modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(23, 0).cuboid(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)), ModelTransform.pivot(-1.75F, 0.5F, 0.0F));
        modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(23, 6).cuboid(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)), ModelTransform.pivot(1.75F, 0.5F, 0.0F));
        modelPartData3.addChild("right_wing", ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 0.0F, 0.6F));
        modelPartData3.addChild("left_wing", ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, 0.6F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(BuilderAllayEntityRenderState renderState) {
        super.setAngles(renderState);
        float f = renderState.limbAmplitudeMultiplier;
        float g = renderState.limbFrequency;
        float h = renderState.age * 20.0F * 0.017453292F + g;
        float i = MathHelper.cos(h) * 3.1415927F * 0.15F + f;
        float j = renderState.age * 9.0F * 0.017453292F;
        float k = Math.min(f / 0.3F, 1.0F);
        float l = 1.0F - k;
        float m = renderState.itemHoldAnimationTicks;
        float n;
        float o;
        float p;

        this.head.pitch = renderState.pitch * 0.017453292F;
        this.head.yaw = renderState.yawDegrees * 0.017453292F;

        this.rightWing.pitch = 0.43633232F * (1.0F - k);
        this.rightWing.yaw = -0.7853982F + i;
        this.leftWing.pitch = 0.43633232F * (1.0F - k);
        this.leftWing.yaw = 0.7853982F - i;
        this.body.pitch = k * 0.7853982F;
        n = m * MathHelper.lerp(k, -1.0471976F, -1.134464F);
        this.root.pivotY += (float) Math.cos(j) * 0.25F * l;
        this.rightArm.pitch = n;
        this.leftArm.pitch = n;
        o = l * (1.0F - m);
        p = 0.43633232F - MathHelper.cos(j + 4.712389F) * 3.1415927F * 0.075F * o;
        this.leftArm.roll = -p;
        this.rightArm.roll = p;
        this.rightArm.yaw = 0.27925268F * m;
        this.leftArm.yaw = -0.27925268F * m;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        float f = 1.0F;
        float g = 3.0F;
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        matrices.translate(0.0F, 0.0625F, 0.1875F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(this.rightArm.pitch));
        matrices.scale(0.7F, 0.7F, 0.7F);
        matrices.translate(0.0625F, 0.0F, 0.0F);
    }
}
