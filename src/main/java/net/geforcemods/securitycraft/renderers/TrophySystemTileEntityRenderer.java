package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class TrophySystemTileEntityRenderer extends BlockEntityRenderer<TrophySystemTileEntity> {

	public TrophySystemTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(TrophySystemTileEntity te, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int p_225616_5_, int p_225616_6_) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		VertexConsumer builder = buffer.getBuffer(RenderLayer.getLines());
		Matrix4f positionMatrix = matrix.peek().getModel();
		BlockPos pos = te.getPos();

		//pos, color
		builder.vertex(positionMatrix, 0.5F, 0.75F, 0.5F).color(255, 0, 0, 255).next();
		builder.vertex(positionMatrix, (float)(te.entityBeingTargeted.getX() - pos.getX()), (float)(te.entityBeingTargeted.getY() - pos.getY()), (float)(te.entityBeingTargeted.getZ() - pos.getZ())).color(255, 0, 0, 255).next();
	}

	@Override
	public boolean rendersOutsideBoundingBox(TrophySystemTileEntity te)
	{
		return true;
	}

}
