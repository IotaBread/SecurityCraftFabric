package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemKeypadChestRenderer extends BuiltinModelItemRenderer
{
	private static final KeypadChestTileEntity DUMMY_TE = new KeypadChestTileEntity();
	private static KeypadChestTileEntityRenderer dummyRenderer = null;

	@Override
	public void render(ItemStack stack, Mode transformType, MatrixStack matrix, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay)
	{
		if(dummyRenderer == null)
			dummyRenderer = new KeypadChestTileEntityRenderer(BlockEntityRenderDispatcher.INSTANCE);

		dummyRenderer.render(DUMMY_TE, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
