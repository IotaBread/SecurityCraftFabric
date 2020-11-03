package net.geforcemods.securitycraft.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Calendar;

@Environment(EnvType.CLIENT)
public class KeypadChestTileEntityRenderer extends ChestBlockEntityRenderer<KeypadChestTileEntity> {
	private static final SpriteIdentifier ACTIVE = createMaterial("active");
	private static final SpriteIdentifier INACTIVE = createMaterial("inactive");
	private static final SpriteIdentifier LEFT_ACTIVE = createMaterial("left_active");
	private static final SpriteIdentifier LEFT_INACTIVE = createMaterial("left_inactive");
	private static final SpriteIdentifier RIGHT_ACTIVE = createMaterial("right_active");
	private static final SpriteIdentifier RIGHT_INACTIVE = createMaterial("right_inactive");
	private static final SpriteIdentifier CHRISTMAS = createMaterial("christmas");
	private static final SpriteIdentifier CHRISTMAS_LEFT = createMaterial("christmas_left");
	private static final SpriteIdentifier CHRISTMAS_RIGHT = createMaterial("christmas_right");
	protected boolean isChristmas;

	public KeypadChestTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);

		Calendar calendar = Calendar.getInstance();

		if(calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
			isChristmas = true;
	}

//	@Override // Forge method
//	protected SpriteIdentifier getMaterial(KeypadChestTileEntity te, ChestType type)
//	{
//		if(isChristmas)
//			return getMaterialForType(type, CHRISTMAS_LEFT, CHRISTMAS_RIGHT, CHRISTMAS);
//		else if(te.getAnimationProgress(0.0F) >= 0.9F)
//			return getMaterialForType(type, LEFT_ACTIVE, RIGHT_ACTIVE, ACTIVE);
//		else
//			return getMaterialForType(type, LEFT_INACTIVE, RIGHT_INACTIVE, INACTIVE);
//	}

	private SpriteIdentifier getMaterialForType(ChestType type, SpriteIdentifier left, SpriteIdentifier right, SpriteIdentifier single)
	{
		switch(type)
		{
			case LEFT:
				return left;
			case RIGHT:
				return right;
			case SINGLE: default:
				return single;
		}
	}

	private static SpriteIdentifier createMaterial(String name)
	{
		return new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier("securitycraft", "entity/chest/" + name));
	}
}