package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.Random;

public class ReinforcedIceBlock extends BaseReinforcedBlock
{
	public ReinforcedIceBlock(Settings properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean hasRandomTicks(BlockState state)
	{
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos))
		{
			if(world.getDimension().isUltrawarm())
				world.removeBlock(pos, false);
			else
			{
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
				world.updateNeighbor(pos, Blocks.WATER, pos);
			}
		}
	}
}
