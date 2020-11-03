package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends AbstractButtonBlock {
	private static final VoxelShape FLOOR_NS_POWERED = Block.createCuboidShape(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.createCuboidShape(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.createCuboidShape(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.createCuboidShape(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.createCuboidShape(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.createCuboidShape(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.createCuboidShape(3, 5, 1, 13, 11, 0);
	private static final VoxelShape WALL_S_UNPOWERED = Block.createCuboidShape(3, 5, 2, 13, 11, 0);
	private static final VoxelShape WALL_E_POWERED = Block.createCuboidShape(1, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.createCuboidShape(2, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.createCuboidShape(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.createCuboidShape(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.createCuboidShape(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.createCuboidShape(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.createCuboidShape(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.createCuboidShape(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock(boolean isWooden, Settings settings) {
		super(isWooden, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		boolean newPowered = !state.get(POWERED);

		BlockUtils.setBlockProperty(world, pos, POWERED, newPowered, true);
		playClickSound(player, world, pos, newPowered);

		if(state.get(FACE) == WallMountLocation.WALL)
			notifyNeighbors(world, pos, state.get(FACING));
		else if(state.get(FACE) == WallMountLocation.CEILING)
			notifyNeighbors(world, pos, Direction.DOWN);
		else if(state.get(FACE) == WallMountLocation.FLOOR)
			notifyNeighbors(world, pos, Direction.UP);

		return ActionResult.SUCCESS;
	}

	private void notifyNeighbors(World world, BlockPos pos, Direction facing)
	{
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(facing.getOpposite()), this);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onStateReplaced(state, world, pos, newState, isMoving);
		world.removeBlockEntity(pos);
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int id, int param){
		super.onSyncedBlockEvent(state, world, pos, id, param);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.onSyncedBlockEvent(id, param);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		switch(state.get(FACE))
		{
			case FLOOR:
				switch(state.get(FACING))
				{
					case NORTH: case SOUTH:
						if(state.get(POWERED))
							return FLOOR_NS_POWERED;
						else
							return FLOOR_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.get(POWERED))
							return FLOOR_EW_POWERED;
						else
							return FLOOR_EW_UNPOWERED;
					default: break;
				}
				break;
			case WALL:
				switch(state.get(FACING))
				{
					case NORTH:
						if(state.get(POWERED))
							return WALL_N_POWERED;
						else
							return WALL_N_UNPOWERED;
					case SOUTH:
						if(state.get(POWERED))
							return WALL_S_POWERED;
						else
							return WALL_S_UNPOWERED;
					case EAST:
						if(state.get(POWERED))
							return WALL_E_POWERED;
						else
							return WALL_E_UNPOWERED;
					case WEST:
						if(state.get(POWERED))
							return WALL_W_POWERED;
						else
							return WALL_W_UNPOWERED;
					default: break;
				}
				break;
			case CEILING:
				switch(state.get(FACING))
				{
					case NORTH: case SOUTH:
						if(state.get(POWERED))
							return CEILING_NS_POWERED;
						else
							return CEILING_NS_UNPOWERED;
					case EAST: case WEST:
						if(state.get(POWERED))
							return CEILING_EW_POWERED;
						else
							return CEILING_EW_UNPOWERED;
					default: break;
				}
		}

		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return VoxelShapes.empty();
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new OwnableTileEntity();
//	}

	@Override
	protected SoundEvent getClickSound(boolean turningOn)
	{
		return turningOn ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
	}
}
