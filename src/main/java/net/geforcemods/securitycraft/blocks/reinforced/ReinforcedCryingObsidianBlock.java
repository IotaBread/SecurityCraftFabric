package net.geforcemods.securitycraft.blocks.reinforced;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class ReinforcedCryingObsidianBlock extends BaseReinforcedBlock
{
	public ReinforcedCryingObsidianBlock(Settings properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(5) == 0)
		{
			Direction direction = Direction.random(rand);

			if(direction != Direction.UP)
			{
				BlockPos offsetPos = pos.offset(direction);
				BlockState offsetState = world.getBlockState(offsetPos);

				if(!state.isOpaque() || !offsetState.isSideSolidFullSquare(world, offsetPos, direction.getOpposite()))
				{
					double xOffset = direction.getOffsetX() == 0 ? rand.nextDouble() : 0.5D + direction.getOffsetX() * 0.6D;
					double yOffset = direction.getOffsetY() == 0 ? rand.nextDouble() : 0.5D + direction.getOffsetY() * 0.6D;
					double zOffset = direction.getOffsetZ() == 0 ? rand.nextDouble() : 0.5D + direction.getOffsetZ() * 0.6D;

					world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
}
