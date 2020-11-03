package net.geforcemods.securitycraft.blocks.mines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class FallingBlockMineBlock extends BaseFullMineBlock
{
	public FallingBlockMineBlock(Settings settings, Block disguisedBlock)
	{
		super(settings, disguisedBlock);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag)
	{
		world.getBlockTickScheduler().schedule(pos, this, 2);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		world.getBlockTickScheduler().schedule(currentPos, this, 2);
		return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(!world.isClient)
		{
			if((world.isAir(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
			{
				if(world.isRegionLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
				{
					BlockEntity te = world.getBlockEntity(pos);

					if(!world.isClient && te instanceof IOwnable)
					{
						FallingBlockEntity entity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));

						entity.blockEntityData = te.toTag(new CompoundTag());
						world.spawnEntity(entity);
					}
				}
				else
				{
					BlockPos blockpos;

					world.breakBlock(pos, false);

					for(blockpos = pos.down(); (world.isAir(blockpos) || canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

					if(blockpos.getY() > 0)
						world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
				}
			}
		}
	}

	public static boolean canFallThrough(BlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	/**
	 * Called periodically clientside on blocks near the player to show effects (like furnace fire ParticleTypes). Note that
	 * this method is unrelated to randomTick and #needsRandomTick, and will always be called regardless
	 * of whether the block can receive random update ticks
	 */
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(16) == 0)
		{
			if(canFallThrough(world.getBlockState(pos.down())))
			{
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() - 0.05D;
				double particleZ = pos.getZ() + rand.nextFloat();

				world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
