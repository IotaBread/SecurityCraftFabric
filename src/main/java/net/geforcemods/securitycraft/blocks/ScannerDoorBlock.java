package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
//import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
//import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ScannerDoorBlock extends DoorBlock
{
	public ScannerDoorBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);

		super.onPlaced(world, pos, state, placer, stack);
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(World world, BlockPos pos, BlockPos neighbor)
	{
		BlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();

		if(state.get(HALF) == DoubleBlockHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.breakBlock(pos, false);
			else if (neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockBelow = pos.up();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
			{
				world.breakBlock(pos, false);
				drop = true;
			}

			if(!world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), Direction.UP))
			{
				world.breakBlock(pos, false);
				drop = true;

				if(stateBelow.getBlock() == this)
					world.breakBlock(blockBelow, false);
			}

			if(drop)
			{
				if(!world.isClient)
				{
					world.breakBlock(pos, false);
					Block.dropStack(world, pos, new ItemStack(SCContent.SCANNER_DOOR_ITEM));
				}
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onStateReplaced(state, world, pos, newState, isMoving);

		if(state.getBlock() != newState.getBlock())
			world.removeBlockEntity(pos);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int id, int param)
	{
		super.onSyncedBlockEvent(state, world, pos, id, param);

		BlockEntity tileentity = world.getBlockEntity(pos);

		return tileentity == null ? false : tileentity.onSyncedBlockEvent(id, param);
	}

//	@Override // Forge method
//	public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos, PlayerEntity player)
//	{
//		return new ItemStack(SCContent.SCANNER_DOOR_ITEM);
//	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state)
	{
		return PistonBehavior.BLOCK;
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new ScannerDoorTileEntity().linkable().activatedByView();
//	}
}
