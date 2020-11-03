package net.geforcemods.securitycraft.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BulletModel extends EntityModel<BulletEntity>
{
	public ModelPart bullet;

	public BulletModel()
	{
		textureWidth = 8;
		textureHeight = 4;
		bullet = new ModelPart(this, 0, 0);
		bullet.setPivot(0.0F, 0.0F, 0.0F);
		bullet.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 2);
	}

	@Override
	public void render(MatrixStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		bullet.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(BulletEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
