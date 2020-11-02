package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
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

public class FrameBlock extends OwnableBlock {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(2, 2, 0, 14, 14, 1);
	private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(15, 2, 2, 16, 14, 14);
	private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(2, 2, 15, 14, 14, 16);
	private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0, 2, 2, 1, 14, 14);

	public FrameBlock(Settings settings){
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		VoxelShape shape = null;

		switch(state.get(FACING))
		{
			case NORTH: shape = SHAPE_NORTH; break;
			case EAST: shape = SHAPE_EAST; break;
			case SOUTH: shape = SHAPE_SOUTH; break;
			case WEST: shape = SHAPE_WEST; break;
			default: shape = VoxelShapes.empty();
		}

		return VoxelShapes.combine(VoxelShapes.fullCube(), shape, BooleanBiFunction.ONLY_FIRST); //subtract
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		return player.getStackInHand(hand).getItem() == SCContent.KEY_PANEL ? ActionResult.SUCCESS : ActionResult.PASS;
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
