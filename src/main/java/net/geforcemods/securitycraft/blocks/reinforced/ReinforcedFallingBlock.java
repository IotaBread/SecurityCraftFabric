package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class ReinforcedFallingBlock extends BaseReinforcedBlock
{
	public ReinforcedFallingBlock(Settings settings, Block vB)
	{
		super(settings, vB);
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
			checkFallable(world, pos);
	}

	private void checkFallable(World world, BlockPos pos)
	{
		if(canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0)
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
				BlockState state = getDefaultState();

				if(world.getBlockState(pos).getBlock() == this)
				{
					state = world.getBlockState(pos);
					world.breakBlock(pos, false);
				}

				BlockPos blockpos;

				for(blockpos = pos.down(); canFallThrough(world.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

				if(blockpos.getY() > 0)
					world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	public static boolean canFallThrough(BlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
	}
}