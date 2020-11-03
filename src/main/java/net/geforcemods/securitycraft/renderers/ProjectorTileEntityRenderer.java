package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public class ProjectorTileEntityRenderer extends BlockEntityRenderer<ProjectorTileEntity> {

	public ProjectorTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(ProjectorTileEntity te, float partialTicks, MatrixStack stack, VertexConsumerProvider buffer, int packedLight, int arg5)
	{
		if(!te.isActive())
			return;

		if(!te.isEmpty())
		{
			for(int i = 0; i < te.getProjectionWidth(); i++) {
				for(int j = 0; j < te.getProjectionWidth(); j++) {
					stack.push();

					BlockPos pos = translateProjection(te, stack, te.getCachedState().get(ProjectorBlock.FACING), i, j, te.getProjectionRange(), te.getProjectionOffset());

					if(pos != null && !te.getWorld().isAir(pos))
					{
						stack.pop();
						continue;
					}

					RenderSystem.disableCull();
					MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(te.getProjectedBlock().getDefaultState(), stack, buffer, LightmapTextureManager.pack(te.getWorld().getLightLevel(LightType.BLOCK, pos), te.getWorld().getLightLevel(LightType.SKY, pos)), OverlayTexture.DEFAULT_UV);
					RenderSystem.enableCull();

					stack.pop();
				}
			}
		}
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 *
	 * @return The BlockPos of the fake block to be drawn
	 */
	private BlockPos translateProjection(ProjectorTileEntity te, MatrixStack stack, Direction direction, int x, int y, double distance, double offset)
	{
		BlockPos pos;

		if(direction == Direction.NORTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + distance);
			stack.translate(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == Direction.SOUTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + -distance);
			stack.translate(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == Direction.WEST) {
			pos = new BlockPos(te.getPos().getX() + distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			stack.translate(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == Direction.EAST) {
			pos = new BlockPos(te.getPos().getX() + -distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			stack.translate(-distance, 0.0D + y, 0.0D + x + offset);
		}
		else {
			return te.getPos();
		}

		return pos;
	}

	@Override
	public boolean rendersOutsideBoundingBox(ProjectorTileEntity te)
	{
		return true;
	}
}
