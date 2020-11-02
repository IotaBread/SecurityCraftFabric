package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class SecurityCameraTileEntityRenderer extends BlockEntityRenderer<SecurityCameraTileEntity> {

	private static final Quaternion POSITIVE_Y_180 = Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.NEGATIVE_Y.getDegreesQuaternion(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F);

	public SecurityCameraTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final Identifier cameraTexture = new Identifier("securitycraft:textures/block/security_camera1.png");

	@Override
	public void render(SecurityCameraTileEntity te, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int p_225616_5_, int p_225616_6_)
	{
		if(te.down || (MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON && PlayerUtils.isPlayerMountedOnCamera(MinecraftClient.getInstance().player) && MinecraftClient.getInstance().player.getVehicle().getBlockPos().equals(te.getPos())))
			return;

		matrix.translate(0.5D, 1.5D, 0.5D);

		if(te.hasWorld() && BlockUtils.getBlock(te.getWorld(), te.getPos()) == SCContent.SECURITY_CAMERA)
		{
			Direction side = te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING);

			if(side == Direction.NORTH)
				matrix.multiply(POSITIVE_Y_180);
			else if(side == Direction.EAST)
				matrix.multiply(POSITIVE_Y_90);
			else if(side == Direction.WEST)
				matrix.multiply(NEGATIVE_Y_90);
		}

		matrix.multiply(POSITIVE_X_180);
		modelSecurityCamera.cameraRotationPoint.yaw = (float)te.cameraRotation;
		modelSecurityCamera.render(matrix, buffer.getBuffer(RenderLayer.getEntitySolid(cameraTexture)), p_225616_5_, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
