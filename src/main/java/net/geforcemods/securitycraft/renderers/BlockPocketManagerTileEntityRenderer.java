package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class BlockPocketManagerTileEntityRenderer extends BlockEntityRenderer<BlockPocketManagerTileEntity>
{
	public BlockPocketManagerTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(BlockPocketManagerTileEntity te, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay)
	{
		// The code below draws the outline border of a block pocket (centered at the manager).

		if(!te.showOutline)
			return;

		Matrix4f positionMatrix = matrix.peek().getModel();
		Direction facing = te.getCachedState().get(BlockPocketManagerBlock.FACING);
		VertexConsumer builder = buffer.getBuffer(RenderLayer.getLines());
		int size = te.size;
		int half = (size - 1) / 2;
		int leftX = -half;
		int rightX = half + 1;
		int frontZ = facing == Direction.NORTH || facing == Direction.WEST ? 0 : 1;
		int backZ = facing == Direction.NORTH || facing == Direction.WEST ? size : 1-size;

		if(facing == Direction.EAST || facing == Direction.WEST) //x- and z-values get switched when the manager's direction is west or east
		{
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half;
			backZ = half + 1;
		}

		//bottom lines
		builder.vertex(positionMatrix, leftX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, 0.0F, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, 0.0F, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, backZ).color(0, 0, 255, 255).next();
		//top lines
		builder.vertex(positionMatrix, leftX, size, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, size, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, size, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, size, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, backZ).color(0, 0, 255, 255).next();
		//corner edge lines
		builder.vertex(positionMatrix, leftX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, size, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, 0.0F, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, leftX, size, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, backZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, 0.0F, frontZ).color(0, 0, 255, 255).next();
		builder.vertex(positionMatrix, rightX, size, frontZ).color(0, 0, 255, 255).next();
	}

	@Override
	public boolean rendersOutsideBoundingBox(BlockPocketManagerTileEntity te)
	{
		return te.showOutline;
	}
}
