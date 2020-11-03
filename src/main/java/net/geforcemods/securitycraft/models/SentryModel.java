package net.geforcemods.securitycraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Sentry - bl4ckscor3
 * Created using Tabula 7.0.0
 */
@Environment(EnvType.CLIENT)
public class SentryModel extends EntityModel<SentryEntity>
{
	public ModelPart base;
	public ModelPart body;
	public ModelPart neck;
	public ModelPart head;
	public ModelPart hair;
	public ModelPart rightEye;
	public ModelPart leftEye;
	public ModelPart nose;

	public SentryModel()
	{
		textureWidth = 64;
		textureHeight = 64;
		base = new ModelPart(this, 0, 0);
		base.setPivot(-7.5F, 9.0F, -7.5F);
		base.addCuboid(0.0F, 0.0F, 0.0F, 15, 15, 15);
		head = new ModelPart(this, 24, 30);
		head.setPivot(-4.0F, -4.0F, -3.0F);
		head.addCuboid(0.0F, 0.0F, 0.0F, 8, 5, 6);
		neck = new ModelPart(this, 45, 0);
		neck.setPivot(-2.0F, 1.0F, -2.0F);
		neck.addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4);
		rightEye = new ModelPart(this, 0, 0);
		rightEye.setPivot(-2.7F, -3.0F, -3.3F);
		rightEye.addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 1);
		body = new ModelPart(this, 0, 30);
		body.setPivot(-3.0F, 5.0F, -3.0F);
		body.addCuboid(0.0F, 0.0F, 0.0F, 6, 4, 6);
		nose = new ModelPart(this, 0, 3);
		nose.setPivot(-0.5F, -1.0F, -6.9F);
		nose.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 4);
		leftEye = new ModelPart(this, 6, 0);
		leftEye.setPivot(0.7F, -3.0F, -3.3F);
		leftEye.addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 1);
		hair = new ModelPart(this, 0, 40);
		hair.setPivot(-3.0F, -5.0F, -3.0F);
		hair.addCuboid(0.0F, 0.0F, 0.0F, 6, 1, 6);
	}

	public void renderBase(MatrixStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		base.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void render(MatrixStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		head.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		neck.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		rightEye.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		nose.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		leftEye.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		hair.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(SentryEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
