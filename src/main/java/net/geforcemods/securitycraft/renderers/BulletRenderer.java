package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class BulletRenderer extends EntityRenderer<BulletEntity>
{
	private static final Identifier TEXTURE = new Identifier(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final BulletModel MODEL = new BulletModel();

	public BulletRenderer(EntityRenderDispatcher renderManager)
	{
		super(renderManager);
	}

	@Override
	public void render(BulletEntity entity, float p_225623_2_, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int p_225623_6_)
	{
		matrix.multiply(new Quaternion(Vector3f.POSITIVE_Y, entity.yaw, true)); //YP
		MODEL.render(matrix, buffer.getBuffer(RenderLayer.getEntitySolid(getTexture(entity))), p_225623_6_, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public Identifier getTexture(BulletEntity entity)
	{
		return TEXTURE;
	}
}
