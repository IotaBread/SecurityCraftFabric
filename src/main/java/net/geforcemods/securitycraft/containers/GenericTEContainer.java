package net.geforcemods.securitycraft.containers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenericTEContainer extends ScreenHandler {
	public BlockEntity te;

	public GenericTEContainer(ScreenHandlerType<GenericTEContainer> type, int windowId, World world, BlockPos pos)
	{
		super(type, windowId);

		te = world.getBlockEntity(pos);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
}
