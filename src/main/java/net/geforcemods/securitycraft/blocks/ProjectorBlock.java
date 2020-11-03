package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;
import java.util.stream.Stream;

public class ProjectorBlock extends DisguisableBlock {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape NORTH = Stream.of(Block.createCuboidShape(3, 5, 0.9, 6, 8, 1.9), Block.createCuboidShape(0, 3, 1, 16, 10, 16), Block.createCuboidShape(2, 8, 0.5, 7, 9, 1), Block.createCuboidShape(2, 4, 0.5, 7, 5, 1), Block.createCuboidShape(6, 5, 0.5, 7, 8, 1), Block.createCuboidShape(2, 5, 0.5, 3, 8, 1), Block.createCuboidShape(0, 0, 1, 2, 3, 3), Block.createCuboidShape(14, 0, 1, 16, 3, 3), Block.createCuboidShape(14, 0, 14, 16, 3, 16), Block.createCuboidShape(0, 0, 14, 2, 3, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape SOUTH = Stream.of(Block.createCuboidShape(0, 3, 0, 16, 10, 15), Block.createCuboidShape(10, 5, 14.1, 13, 8, 15.100000000000001), Block.createCuboidShape(9, 8, 15, 14, 9, 15.5), Block.createCuboidShape(9, 4, 15, 14, 5, 15.5), Block.createCuboidShape(9, 5, 15, 10, 8, 15.5), Block.createCuboidShape(13, 5, 15, 14, 8, 15.5), Block.createCuboidShape(14, 0, 13, 16, 3, 15), Block.createCuboidShape(0, 0, 13, 2, 3, 15), Block.createCuboidShape(0, 0, 0, 2, 3, 2), Block.createCuboidShape(14, 0, 0, 16, 3, 2)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape WEST = Stream.of(Block.createCuboidShape(0.5, 5, 13, 1, 8, 14), Block.createCuboidShape(0.5, 5, 9, 1, 8, 10), Block.createCuboidShape(0.5, 4, 9, 1, 5, 14), Block.createCuboidShape(0.5, 8, 9, 1, 9, 14), Block.createCuboidShape(0.75, 5, 10, 1.75, 8, 13), Block.createCuboidShape(1, 0, 14, 3, 3, 16), Block.createCuboidShape(14, 0, 14, 16, 3, 16), Block.createCuboidShape(14, 0, 0, 16, 3, 2), Block.createCuboidShape(1, 0, 0, 3, 3, 2), Block.createCuboidShape(1, 3, 0, 16, 10, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).orElse(VoxelShapes.fullCube());
	private static final VoxelShape EAST = Stream.of(Block.createCuboidShape(15, 5, 2, 15.5, 8, 3), Block.createCuboidShape(15, 5, 6, 15.5, 8, 7), Block.createCuboidShape(15, 4, 2, 15.5, 5, 7), Block.createCuboidShape(15, 8, 2, 15.5, 9, 7), Block.createCuboidShape(14.25, 5, 3, 15.25, 8, 6), Block.createCuboidShape(13, 0, 0, 15, 3, 2), Block.createCuboidShape(0, 0, 0, 2, 3, 2), Block.createCuboidShape(0, 0, 14, 2, 3, 16), Block.createCuboidShape(13, 0, 14, 15, 3, 16), Block.createCuboidShape(0, 3, 0, 15, 10, 16)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).orElse(VoxelShapes.fullCube());

	public ProjectorBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getOutlineShape(world, pos, ctx);
		else
		{
			switch(state.get(FACING))
			{
				case NORTH:
					return SOUTH;
				case EAST:
					return WEST;
				case SOUTH:
					return NORTH;
				case WEST:
					return EAST;
				default: return VoxelShapes.fullCube();
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		BlockEntity te = world.getBlockEntity(pos);

		if(!(te instanceof ProjectorTileEntity))
			return ActionResult.FAIL;

		boolean isOwner = ((IOwnable)te).getOwner().isOwner(player);

//		if(!world.isClient && isOwner) // TODO
//			NetworkHooks.openGui((ServerPlayerEntity)player, (NamedScreenHandlerFactory) te, pos);

		return isOwner ? ActionResult.SUCCESS : ActionResult.FAIL;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		BlockEntity tileentity = world.getBlockEntity(pos);

		if (tileentity instanceof ProjectorTileEntity)
		{
			// Drop the block being projected
			ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorTileEntity) world.getBlockEntity(pos)).getStack(9));
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(item));
		}

		super.onStateReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		if(!world.isClient)
		{
			if(world.getBlockEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) world.getBlockEntity(pos)).isActivatedByRedstone())
			{
				((ProjectorTileEntity) world.getBlockEntity(pos)).setActive(world.isReceivingRedstonePower(pos));
				((ProjectorTileEntity) world.getBlockEntity(pos)).sync();
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if (!world.isReceivingRedstonePower(pos) && world.getBlockEntity(pos) instanceof ProjectorTileEntity && ((ProjectorTileEntity) world.getBlockEntity(pos)).isActivatedByRedstone())
		{
			((ProjectorTileEntity) world.getBlockEntity(pos)).setActive(false);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new ProjectorTileEntity();
//	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
