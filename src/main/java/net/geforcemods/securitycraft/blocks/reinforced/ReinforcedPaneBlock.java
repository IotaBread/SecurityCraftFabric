package net.geforcemods.securitycraft.blocks.reinforced;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class ReinforcedPaneBlock extends BaseReinforcedBlock implements FluidDrainable, FluidFillable
{
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] field_196410_A;
	protected final VoxelShape[] field_196412_B;

	public ReinforcedPaneBlock(Settings settings, Block vB)
	{
		super(settings, vB);
		field_196410_A = func_196408_a(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		field_196412_B = func_196408_a(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
		setDefaultState(stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
	}

	protected VoxelShape[] func_196408_a(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_)
	{
		float f = 8.0F - p_196408_1_;
		float f1 = 8.0F + p_196408_1_;
		float f2 = 8.0F - p_196408_2_;
		float f3 = 8.0F + p_196408_2_;
		VoxelShape voxelshape = Block.createCuboidShape(f, 0.0D, f, f1, p_196408_3_, f1);
		VoxelShape voxelshape1 = Block.createCuboidShape(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
		VoxelShape voxelshape2 = Block.createCuboidShape(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
		VoxelShape voxelshape3 = Block.createCuboidShape(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
		VoxelShape voxelshape4 = Block.createCuboidShape(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
		VoxelShape voxelshape5 = VoxelShapes.union(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.union(voxelshape2, voxelshape3);
		VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.union(voxelshape2, voxelshape1), VoxelShapes.union(voxelshape3, voxelshape1), VoxelShapes.union(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.union(voxelshape2, voxelshape4), VoxelShapes.union(voxelshape3, voxelshape4), VoxelShapes.union(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.union(voxelshape2, voxelshape5), VoxelShapes.union(voxelshape3, voxelshape5), VoxelShapes.union(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			avoxelshape[i] = VoxelShapes.union(voxelshape, avoxelshape[i]);
		}

		return avoxelshape;
	}

	@Override
	public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		if(!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isClient())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, true), 3);
				world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return field_196412_B[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return field_196410_A[getIndex(state)];
	}

	private static int getMask(Direction facing)
	{
		return 1 << facing.getHorizontal();
	}

	protected int getIndex(BlockState p_196406_1_)
	{
		int i = 0;

		if(p_196406_1_.get(NORTH))
			i |= getMask(Direction.NORTH);

		if(p_196406_1_.get(EAST))
			i |= getMask(Direction.EAST);

		if(p_196406_1_.get(SOUTH))
			i |= getMask(Direction.SOUTH);

		if(p_196406_1_.get(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView worldIn, BlockPos pos, NavigationType type)
	{
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		switch(rot) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		switch(mirror)
		{
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getPlacementState(context.getWorld(), context.getBlockPos());
	}

	public BlockState getPlacementState(BlockView world, BlockPos pos)
	{
		FluidState fluidState = world.getFluidState(pos);
		BlockPos northPos = pos.north();
		BlockPos southPos = pos.south();
		BlockPos westPos = pos.west();
		BlockPos eastPos = pos.east();
		BlockState northState = world.getBlockState(northPos);
		BlockState southState = world.getBlockState(southPos);
		BlockState westState = world.getBlockState(westPos);
		BlockState eastState = world.getBlockState(eastPos);
		return getDefaultState().with(NORTH, canAttachTo(northState, northState.isSideSolidFullSquare(world, northPos, Direction.SOUTH))).with(SOUTH, canAttachTo(southState, southState.isSideSolidFullSquare(world, southPos, Direction.NORTH))).with(WEST, canAttachTo(westState, westState.isSideSolidFullSquare(world, westPos, Direction.EAST))).with(EAST, canAttachTo(eastState, eastState.isSideSolidFullSquare(world, eastPos, Direction.WEST))).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(FACING_TO_PROPERTY_MAP.get(facing), canAttachTo(facingState, facingState.isSideSolidFullSquare(world, facingPos, facing.getOpposite()))) : super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		if(adjacentBlockState.getBlock() == this)
		{
			if(!side.getAxis().isHorizontal())
				return true;

			if(state.get(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.get(FACING_TO_PROPERTY_MAP.get(side.getOpposite())))
				return true;
		}

		return super.isSideInvisible(state, adjacentBlockState, side);
	}

	public final boolean canAttachTo(BlockState p_220112_1_, boolean p_220112_2_) {
		Block block = p_220112_1_.getBlock();
		return !cannotConnect(block) && p_220112_2_ || block instanceof ReinforcedPaneBlock;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(NORTH, vanillaState.get(PaneBlock.NORTH)).with(EAST, vanillaState.get(PaneBlock.EAST)).with(WEST, vanillaState.get(PaneBlock.WEST)).with(SOUTH, vanillaState.get(PaneBlock.SOUTH)).with(WATERLOGGED, vanillaState.get(PaneBlock.WATERLOGGED));
	}
}