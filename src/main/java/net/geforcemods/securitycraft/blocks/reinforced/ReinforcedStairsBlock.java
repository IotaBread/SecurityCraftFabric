package net.geforcemods.securitycraft.blocks.reinforced;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ReinforcedStairsBlock extends BaseReinforcedBlock implements Waterloggable
{
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape AABB_SLAB_TOP = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape AABB_SLAB_BOTTOM = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWD_CORNER = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape SWD_CORNER = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape NWU_CORNER = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
	protected static final VoxelShape SWU_CORNER = Block.createCuboidShape(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
	protected static final VoxelShape NED_CORNER = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape SED_CORNER = Block.createCuboidShape(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape NEU_CORNER = Block.createCuboidShape(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	protected static final VoxelShape SEU_CORNER = Block.createCuboidShape(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape[] SLAB_TOP_SHAPES = makeShapes(AABB_SLAB_TOP, NWD_CORNER, NED_CORNER, SWD_CORNER, SED_CORNER);
	protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = makeShapes(AABB_SLAB_BOTTOM, NWU_CORNER, NEU_CORNER, SWU_CORNER, SEU_CORNER);
	private static final int[] field_196522_K = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block modelBlock;
	private final BlockState modelState;

	public ReinforcedStairsBlock(Settings settings, Block vB)
	{
		this(settings, () -> vB);
	}

	public ReinforcedStairsBlock(Settings settings, Supplier<Block> vB)
	{
		super(settings, vB);

		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, BlockHalf.BOTTOM).with(SHAPE, StairShape.STRAIGHT).with(WATERLOGGED, false));
		modelBlock = getVanillaBlock();
		modelState = modelBlock.getDefaultState();
	}

	private static VoxelShape[] makeShapes(VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		return IntStream.range(0, 16).mapToObj(shape -> combineShapes(shape, slabShape, nwCorner, neCorner, swCorner, seCorner)).toArray(size -> new VoxelShape[size]);
	}

	private static VoxelShape combineShapes(int bitfield, VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner)
	{
		VoxelShape shape = slabShape;

		if((bitfield & 1) != 0)
			shape = VoxelShapes.union(slabShape, nwCorner);

		if((bitfield & 2) != 0)
			shape = VoxelShapes.union(shape, neCorner);

		if((bitfield & 4) != 0)
			shape = VoxelShapes.union(shape, swCorner);

		if((bitfield & 8) != 0)
			shape = VoxelShapes.union(shape, seCorner);

		return shape;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state)
	{
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
	{
		return (state.get(HALF) == BlockHalf.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[field_196522_K[func_196511_x(state)]];
	}

	private int func_196511_x(BlockState state)
	{
		return state.get(SHAPE).ordinal() * 4 + state.get(FACING).getHorizontal();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		modelBlock.randomDisplayTick(stateIn, worldIn, pos, rand);
	}

	@Override
	public void onBlockBreakStart(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
	{
		modelState.onBlockBreakStart(worldIn, pos, player);
	}

	@Override
	public void onBroken(WorldAccess worldIn, BlockPos pos, BlockState state)
	{
		modelBlock.onBroken(worldIn, pos, state);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if(state.getBlock() != oldState.getBlock())
		{
			modelState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
			modelBlock.onBlockAdded(modelState, world, pos, oldState, false);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
			modelState.onStateReplaced(world, pos, newState, isMoving);
	}

	@Override
	public void onSteppedOn(World worldIn, BlockPos pos, Entity entity)
	{
		modelBlock.onSteppedOn(worldIn, pos, entity);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		modelState.scheduledTick(world, pos, random);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		return modelState.onUse(world, player, hand, hit);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		modelBlock.onDestroyedByExplosion(world, pos, explosion);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		Direction dir = ctx.getSide();
		BlockPos pos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(pos);
		BlockState state = this.getDefaultState().with(FACING, ctx.getPlayerFacing()).with(HALF, dir != Direction.DOWN && (dir == Direction.UP || !(ctx.getHitPos().y - pos.getY() > 0.5D)) ? BlockHalf.BOTTOM : BlockHalf.TOP).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

		return state.with(SHAPE, getShapeProperty(state, ctx.getWorld(), pos));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return facing.getAxis().isHorizontal() ? state.with(SHAPE, getShapeProperty(state, world, currentPos)) : super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	private static StairShape getShapeProperty(BlockState state, BlockView world, BlockPos pos)
	{
		Direction dir = state.get(FACING);
		BlockState offsetState = world.getBlockState(pos.offset(dir));

		if(isBlockStairs(offsetState) && state.get(HALF) == offsetState.get(HALF))
		{
			Direction offsetDir = offsetState.get(FACING);

			if(offsetDir.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetDir.getOpposite()))
			{
				if(offsetDir == dir.rotateYCounterclockwise())
					return StairShape.OUTER_LEFT;
				else return StairShape.OUTER_RIGHT;
			}
		}

		BlockState offsetOppositeState = world.getBlockState(pos.offset(dir.getOpposite()));

		if (isBlockStairs(offsetOppositeState) && state.get(HALF) == offsetOppositeState.get(HALF))
		{
			Direction offsetOppositeDir = offsetOppositeState.get(FACING);

			if(offsetOppositeDir.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, pos, offsetOppositeDir))
			{
				if(offsetOppositeDir == dir.rotateYCounterclockwise())
					return StairShape.INNER_LEFT;
				else return StairShape.INNER_RIGHT;
			}
		}

		return StairShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(BlockState state, BlockView world, BlockPos pos, Direction face)
	{
		BlockState offsetState = world.getBlockState(pos.offset(face));

		return !isBlockStairs(offsetState) || offsetState.get(FACING) != state.get(FACING) || offsetState.get(HALF) != state.get(HALF);
	}

	public static boolean isBlockStairs(BlockState state)
	{
		return state.getBlock() instanceof ReinforcedStairsBlock || state.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		Direction direction = state.get(FACING);
		StairShape shape = state.get(SHAPE);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z)
				{
					switch(shape)
					{
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
						default:
							return state.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X)
				{
					switch(shape)
					{
						case INNER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
						case INNER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
						case OUTER_LEFT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
						case STRAIGHT:
							return state.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			default:
				break;
		}

		return super.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(SHAPE, vanillaState.get(SHAPE)).with(FACING, vanillaState.get(FACING)).with(HALF, vanillaState.get(HALF)).with(WATERLOGGED, vanillaState.get(WATERLOGGED));
	}
}