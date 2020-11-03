package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBettyEntity> {

	public BouncingBettyRenderer(EntityRenderDispatcher renderManager)
	{
		super(renderManager);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(BouncingBettyEntity entity, float entityYaw, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int packedLight)
	{
		matrix.push();
		matrix.translate(0.0D, 0.5D, 0.0D);

		if(entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			matrix.scale(scale, scale, scale);
		}

		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
		matrix.translate(-0.5D, -0.5D, 0.5D);
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(SCContent.BOUNCING_BETTY.getDefaultState(), matrix, buffer, packedLight, OverlayTexture.DEFAULT_UV);
		matrix.pop();
		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public Identifier getTexture(BouncingBettyEntity entity)
	{
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}