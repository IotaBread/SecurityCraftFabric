package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IMSBombRenderer extends EntityRenderer<IMSBombEntity> {

	private static final Identifier TEXTURE = new Identifier("securitycraft:textures/entity/ims_bomb.png");
	/** instance of ModelIMSBomb for rendering */
	protected static final IMSBombModel modelBomb = new IMSBombModel();

	public IMSBombRenderer(EntityRenderDispatcher renderManager){
		super(renderManager);
	}

	@Override
	public void render(IMSBombEntity imsBomb, float p_225623_2_, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int p_225623_6_)
	{
		matrix.translate(-0.1D, 0, 0.1D);
		matrix.scale(1.4F, 1.4F, 1.4F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(imsBomb));
		modelBomb.render(matrix, buffer.getBuffer(RenderLayer.getEntitySolid(getTexture(imsBomb))), p_225623_6_, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public Identifier getTexture(IMSBombEntity imsBomb) {
		return TEXTURE;
	}
}
