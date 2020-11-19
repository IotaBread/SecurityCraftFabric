package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class ReinforcedStainedGlassPaneBlock extends ReinforcedPaneBlock implements Stainable
{
	private final DyeColor color;

	public ReinforcedStainedGlassPaneBlock(Settings settings, DyeColor color, Block vB)
	{
		super(settings, vB);
		this.color = color;
	}

	@Override
	public float[] getBeaconColorMultiplier(BlockState state, WorldView world, BlockPos pos, BlockPos beaconPos)
	{
		return color.getColorComponents();
	}

	@Override
	public DyeColor getColor()
	{
		return color;
	}
}
