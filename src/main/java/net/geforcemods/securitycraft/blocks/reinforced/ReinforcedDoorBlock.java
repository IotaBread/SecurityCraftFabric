package net.geforcemods.securitycraft.blocks.reinforced;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import org.jetbrains.annotations.Nullable;

public class ReinforcedDoorBlock extends OwnableBlock implements BlockEntityProvider {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.createCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.createCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ReinforcedDoorBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHinge.LEFT).with(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		boolean flag = !state.get(OPEN);
		boolean flag1 = state.get(HINGE) == DoorHinge.RIGHT;
		switch(direction) {
			case EAST:
			default:
				return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
				return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf ? stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE, facingState.get(HINGE)) : Blocks.AIR.getDefaultState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canPlaceAt(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		}
	}

	@Override
	public void afterBreak(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		super.afterBreak(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}

	@Override
	public void onBreak(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleblockhalf = state.get(HALF);
		BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf) {
			worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
			worldIn.syncWorldEvent(player, 2001, blockpos, Block.getRawIdFromState(blockstate));
			ItemStack itemstack = player.getMainHandStack();
			if (!worldIn.isClient && !player.isCreative()) {
				Block.dropStacks(state, worldIn, pos, (BlockEntity)null, player, itemstack);
				Block.dropStacks(blockstate, worldIn, blockpos, (BlockEntity)null, player, itemstack);
			}
		}

		super.onBreak(worldIn, pos, state, player);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView worldIn, BlockPos pos, NavigationType type) {
		switch(type) {
			case LAND:
				return state.get(OPEN);
			case WATER:
				return false;
			case AIR:
				return state.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos blockpos = context.getBlockPos();
		if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).canReplace(context)) {
			World world = context.getWorld();
			boolean flag = world.isReceivingRedstonePower(blockpos) || world.isReceivingRedstonePower(blockpos.up());
			return this.getDefaultState().with(FACING, context.getPlayerFacing()).with(HINGE, this.getHingeSide(context)).with(OPEN, flag).with(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		onNeighborChanged(world, pos, fromPos);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		super.onPlaced(world, pos, state, placer, stack);
		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHinge getHingeSide(ItemPlacementContext p_208073_1_) {
		BlockView iblockreader = p_208073_1_.getWorld();
		BlockPos blockpos = p_208073_1_.getBlockPos();
		Direction direction = p_208073_1_.getPlayerFacing();
		BlockPos blockpos1 = blockpos.up();
		Direction direction1 = direction.rotateYCounterclockwise();
		BlockPos blockpos2 = blockpos.offset(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		BlockPos blockpos3 = blockpos1.offset(direction1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
		Direction direction2 = direction.rotateYClockwise();
		BlockPos blockpos4 = blockpos.offset(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		BlockPos blockpos5 = blockpos1.offset(direction2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
		int i = (blockstate.isFullCube(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isFullCube(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isFullCube(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isFullCube(iblockreader, blockpos5) ? 1 : 0);
		boolean flag = blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
		boolean flag1 = blockstate2.getBlock() == this && blockstate2.get(HALF) == DoubleBlockHalf.LOWER;
		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = p_208073_1_.getHitPos();
				double d0 = vec3d.x - blockpos.getX();
				double d1 = vec3d.z - blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
			} else {
				return DoorHinge.LEFT;
			}
		} else {
			return DoorHinge.RIGHT;
		}
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
		Owner previousOwner = null;

		if(world.getBlockEntity(pos) instanceof OwnableTileEntity)
			previousOwner = ((OwnableTileEntity)world.getBlockEntity(pos)).getOwner();

		if(state.get(HALF) == DoubleBlockHalf.UPPER)
		{
			BlockPos blockBelow = pos.down();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if(stateBelow.getBlock() != this)
				world.breakBlock(pos, false);
			else if(neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else
		{
			boolean drop = false;
			BlockPos blockAbove = pos.up();
			BlockState stateAbove = world.getBlockState(blockAbove);

			if(stateAbove.getBlock() != this)
			{
				world.breakBlock(pos, false);
				drop = true;
			}

			if(!BlockUtils.isSideSolid(world, pos.down(), Direction.UP))
			{
				world.breakBlock(pos, false);
				drop = true;

				if(stateAbove.getBlock() == this)
					world.breakBlock(pos, false);
			}

			if(drop)
			{
				if(!world.isClient)
				{
					world.breakBlock(pos, false);
					Block.dropStack(world, pos, new ItemStack(SCContent.REINFORCED_DOOR_ITEM));
				}
			}
			else
			{
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.up());

				if(neighborBlock != this && hasActiveSCBlock != stateAbove.get(OPEN))
				{
					if(hasActiveSCBlock != state.get(OPEN))
					{
						world.setBlockState(pos, state.with(OPEN, hasActiveSCBlock), 2);

						BlockState secondDoorState;

						if(state.get(FACING) == Direction.WEST)
						{
							secondDoorState = world.getBlockState(pos.north());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
								world.setBlockState(pos.north(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.south());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
									world.setBlockState(pos.south(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.get(FACING) == Direction.NORTH)
						{
							secondDoorState = world.getBlockState(pos.east());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
								world.setBlockState(pos.east(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.west());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
									world.setBlockState(pos.west(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.get(FACING) == Direction.EAST)
						{
							secondDoorState = world.getBlockState(pos.south());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
								world.setBlockState(pos.south(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.north());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
									world.setBlockState(pos.north(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if(state.get(FACING) == Direction.SOUTH)
						{
							secondDoorState = world.getBlockState(pos.west());

							if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
								world.setBlockState(pos.west(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							else
							{
								secondDoorState = world.getBlockState(pos.east());

								if(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR && secondDoorState.get(OPEN) != hasActiveSCBlock)
									world.setBlockState(pos.east(), secondDoorState.with(OPEN, hasActiveSCBlock), 2);
							}
						}

						world.syncWorldEvent((PlayerEntity)null, hasActiveSCBlock ? 1005 : 1011, pos, 0);
					}
				}
			}
		}

		if(previousOwner != null && world.getBlockEntity(pos) instanceof OwnableTileEntity && world.getBlockEntity(pos.up()) instanceof OwnableTileEntity)
		{
			((OwnableTileEntity)world.getBlockEntity(pos)).getOwner().set(previousOwner);
			((OwnableTileEntity)world.getBlockEntity(pos.up())).getOwner().set(previousOwner);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (state.get(HALF) == DoubleBlockHalf.LOWER) {
			return blockstate.isSideSolidFullSquare(worldIn, blockpos, Direction.UP);
		} else {
			return blockstate.getBlock() == this;
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
		return mirrorIn == BlockMirror.NONE ? state : state.rotate(mirrorIn.getRotation(state.get(FACING))).cycle(HINGE); //cycle
	}

	@Override
	@Environment(EnvType.CLIENT)
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
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

	@Override
	public ItemStack getPickStack(BlockView worldIn, BlockPos pos, BlockState state){
		return new ItemStack(SCContent.REINFORCED_DOOR_ITEM);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new OwnableTileEntity();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(HALF, FACING, OPEN, HINGE);
	}
}