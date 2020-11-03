package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.models.SentryModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SentryRenderer extends EntityRenderer<SentryEntity>
{
	private static final Identifier TEXTURE = new Identifier(SecurityCraft.MODID + ":textures/entity/sentry.png");
	private static final SentryModel MODEL = new SentryModel();

	public SentryRenderer(EntityRenderDispatcher renderManager)
	{
		super(renderManager);
	}

	@Override
	public void render(SentryEntity entity, float partialTicks, float p_225623_3_, MatrixStack stack, VertexConsumerProvider buffer, int p_225623_6_)
	{
		VertexConsumer builder = buffer.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));

		stack.translate(0.0D, 1.5D, 0.0D);
		stack.scale(-1, -1, 1); //rotate model rightside up
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entity));
		MODEL.renderBase(stack, builder, p_225623_6_, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(entity.getDataTracker().get(SentryEntity.HEAD_ROTATION)));
		stack.translate(0.0F, entity.getHeadYTranslation(), 0.0F);
		MODEL.render(stack, builder, p_225623_6_, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public Identifier getTexture(SentryEntity entity)
	{
		return TEXTURE;
	}
}
