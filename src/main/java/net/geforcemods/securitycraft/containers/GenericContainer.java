package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class GenericContainer extends ScreenHandler {

	public GenericContainer(ScreenHandlerType<GenericContainer> type, int windowId)
	{
		super(type, windowId);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
