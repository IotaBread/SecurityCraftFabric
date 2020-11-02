package net.geforcemods.securitycraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * SecurityCamera - Geforce
 * Created using Tabula 4.1.1
 */
@Environment(EnvType.CLIENT)
public class SecurityCameraModel extends EntityModel<SecurityCameraEntity> {
	public ModelPart shape1;
	public ModelPart shape2;
	public ModelPart cameraRotationPoint;
	public ModelPart shape3;
	public ModelPart cameraBody;
	public ModelPart cameraLensRight;
	public ModelPart cameraLensLeft;
	public ModelPart cameraLensTop;

	public SecurityCameraModel() {
		textureWidth = 128;
		textureHeight = 64;
		cameraRotationPoint = new ModelPart(this, 0, 25);
		cameraRotationPoint.setPivot(0.0F, 14.0F, 3.0F);
		cameraRotationPoint.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 1);
		setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
		cameraLensRight = new ModelPart(this, 10, 40);
		cameraLensRight.setPivot(3.0F, 0.0F, -3.0F);
		cameraLensRight.addCuboid(-2.0F, 0.0F, 0.0F, 1, 3, 1);
		shape3 = new ModelPart(this, 1, 12);
		shape3.setPivot(0.0F, 1.0F, 0.0F);
		shape3.addCuboid(0.0F, 0.0F, 0.0F, 2, 1, 7);
		cameraLensLeft = new ModelPart(this, 0, 40);
		cameraLensLeft.setPivot(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addCuboid(0.0F, 0.0F, 0.0F, 1, 3, 1);
		cameraBody = new ModelPart(this, 0, 25);
		cameraBody.setPivot(0.0F, 0.0F, -5.0F);
		cameraBody.addCuboid(-2.0F, 0.0F, -2.0F, 4, 3, 8);
		setRotateAngle(cameraBody, 0.2617993877991494F, 0.0F, 0.0F);
		shape1 = new ModelPart(this, 0, 0);
		shape1.setPivot(-3.0F, 13.0F, 7.0F);
		shape1.addCuboid(0.0F, 0.0F, 0.0F, 6, 6, 1);
		cameraLensTop = new ModelPart(this, 20, 40);
		cameraLensTop.setPivot(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addCuboid(0.0F, 0.0F, 0.0F, 2, 1, 1);
		shape2 = new ModelPart(this, 2, 12);
		shape2.setPivot(-1.0F, 13.75F, 2.25F);
		shape2.addCuboid(0.0F, 0.0F, 0.0F, 2, 1, 6);
		setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
		cameraBody.addChild(cameraLensRight);
		shape2.addChild(shape3);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint.addChild(cameraBody);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void render(MatrixStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		cameraRotationPoint.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape1.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape2.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.pitch = x;
		modelRenderer.yaw = y;
		modelRenderer.roll = z;
	}

	@Override
	public void setAngles(SecurityCameraEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
