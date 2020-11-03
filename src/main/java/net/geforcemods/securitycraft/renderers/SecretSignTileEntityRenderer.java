package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer.SignModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.OrderedText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SecretSignTileEntityRenderer extends BlockEntityRenderer<SecretSignTileEntity>
{
	private final SignModel model = new SignModel();

	public SecretSignTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(SecretSignTileEntity te, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay)
	{
		BlockState state = te.getCachedState();
		SpriteIdentifier material = SignBlockEntityRenderer.getModelTexture(state.getBlock());
		TextRenderer font = dispatcher.getTextRenderer();
		VertexConsumer builder;

		matrix.push();

		if(state.getBlock() instanceof SecretStandingSignBlock)
		{
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-(state.get(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.foot.visible = true;
		}
		else
		{
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-state.get(SecretWallSignBlock.FACING).asRotation()));
			matrix.translate(0.0D, -0.3125D, -0.4375D);
			model.foot.visible = false;
		}

		matrix.push();
		matrix.scale(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.getVertexConsumer(buffer, model::getLayer);
		model.field.render(matrix, builder, combinedLight, combinedOverlay);
		model.foot.render(matrix, builder, combinedLight, combinedOverlay);
		matrix.pop();
		matrix.translate(0.0D, 0.33333334F, 0.046666667F);
		matrix.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.isPlayerAllowedToSeeText(MinecraftClient.getInstance().player))
		{
			int textColor = te.getTextColor().getSignColor();
			int r = (int)(NativeImage.getRed(textColor) * 0.4D);
			int g = (int)(NativeImage.getGreen(textColor) * 0.4D);
			int b = (int)(NativeImage.getBlue(textColor) * 0.4D);
			int argb = NativeImage.getAbgrColor(0, b, g, r);

			for(int line = 0; line < 4; ++line)
			{
				OrderedText rp = te.getTextBeingEditedOnRow(line, (p_243502_1_) -> {
					List<OrderedText> list = font.wrapLines(p_243502_1_, 90);
					return list.isEmpty() ? OrderedText.EMPTY : list.get(0);
				});

				if(rp != null)
					font.draw(rp, -font.getWidth(rp) / 2, line * 10 - 20, argb, false, matrix.peek().getModel(), buffer, false, 0, combinedLight);
			}
		}

		matrix.pop();
	}
}