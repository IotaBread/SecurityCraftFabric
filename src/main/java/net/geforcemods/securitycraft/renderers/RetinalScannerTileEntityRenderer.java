package net.geforcemods.securitycraft.renderers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;

import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class RetinalScannerTileEntityRenderer extends BlockEntityRenderer<RetinalScannerTileEntity>
{
	private static final float CORRECT_FACTOR = 1 / 550F;

	public RetinalScannerTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(RetinalScannerTileEntity te, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay)
	{
		Direction direction = te.getCachedState().get(RetinalScannerBlock.FACING);

		if(!te.hasModule(ModuleType.DISGUISE) && direction != null)
		{
			matrix.push();

			switch(direction)
			{
				case NORTH:
					matrix.translate(0.25F, 1.0F / 16.0F, 0.0F);
					break;
				case SOUTH:
					matrix.translate(0.75F, 1.0F / 16.0F, 1.0F);
					matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
					break;
				case WEST:
					matrix.translate(0.0F, 1.0F / 16.0F, 0.75F);
					matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
					break;
				case EAST:
					matrix.translate(1.0F, 1.0F / 16.0F, 0.25F);
					matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
					break;
				default:
					break;
			}

			matrix.scale(-1.0F, -1.0F, 1.0F);

			VertexConsumer vertexBuilder = buffer.getBuffer(RenderLayer.getEntityCutout(getSkinTexture(te.getPlayerProfile())));
			Matrix4f positionMatrix = matrix.peek().getModel();
			Matrix3f normalMatrix = matrix.peek().getNormal();
			Vec3i normalVector = direction.getVector();
			BlockPos offsetPos = te.getPos().offset(direction);

			combinedLight = LightmapTextureManager.pack(te.getWorld().getLightLevel(LightType.BLOCK, offsetPos), te.getWorld().getLightLevel(LightType.SKY, offsetPos));

			// face
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).texture(0.125F, 0.25F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).texture(0.125F, 0.125F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).texture(0.25F, 0.125F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).texture(0.25F, 0.25F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();

			// helmet
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).texture(0.625F, 0.25F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).texture(0.625F, 0.125F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).texture(0.75F, 0.125F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).texture(0.75F, 0.25F).overlay(OverlayTexture.DEFAULT_UV).light(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();

			matrix.pop();
		}
	}

	private static Identifier getSkinTexture(@Nullable GameProfile profile)
	{
		if(ConfigHandler.CONFIG.retinalScannerFace && profile != null)
		{
			MinecraftClient minecraft = MinecraftClient.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinProvider().getTextures(profile);
			return map.containsKey(Type.SKIN) ? minecraft.getSkinProvider().loadSkin(map.get(Type.SKIN), Type.SKIN) : DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(profile));
		}
		else
			return DefaultSkinHelper.getTexture();
	}
}