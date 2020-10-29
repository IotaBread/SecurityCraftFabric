package net.geforcemods.securitycraft.models;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * IMSBomb - Geforce
 * Created using Tabula 4.1.1
 */
@Environment(EnvType.CLIENT)
public class IMSBombModel extends EntityModel<IMSBombEntity> {
	public ModelPart shape1;

	public IMSBombModel() {
		textureWidth = 24;
		textureHeight = 24;
		shape1 = new ModelPart(this, 0, 0);
		shape1.setPivot(0.0F, 0.0F, 0.0F);
		shape1.addCuboid(0.0F, 0.0F, 0.0F, 3, 4, 3);
	}

	@Override
	public void render(MatrixStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		shape1.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(IMSBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
