package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.MotionActivatedLightTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MotionActivatedLightBlock extends OwnableBlock {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = Properties.LIT;
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(Block.createCuboidShape(6, 3, 13, 10, 4, 14), VoxelShapes.union(Block.createCuboidShape(6, 6, 13, 10, 9, 14), VoxelShapes.combine(Block.createCuboidShape(7, 3, 14, 9, 8, 16), Block.createCuboidShape(7, 4, 15, 9, 7, 14), BooleanBiFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_EAST = VoxelShapes.union(Block.createCuboidShape(3, 3, 6, 2, 4, 10), VoxelShapes.union(Block.createCuboidShape(3, 6, 6, 2, 9, 10), VoxelShapes.combine(Block.createCuboidShape(2, 3, 7, 0, 8, 9), Block.createCuboidShape(1, 4, 7, 2, 7, 9), BooleanBiFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(Block.createCuboidShape(6, 3, 2, 10, 4, 3), VoxelShapes.union(Block.createCuboidShape(6, 6, 2, 10, 9, 3), VoxelShapes.combine(Block.createCuboidShape(7, 3, 0, 9, 8, 2), Block.createCuboidShape(7, 4, 1, 9, 7, 2), BooleanBiFunction.ONLY_FIRST)));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.union(Block.createCuboidShape(13, 3, 6, 14, 4, 10), VoxelShapes.union(Block.createCuboidShape(13, 6, 6, 14, 9, 10), VoxelShapes.combine(Block.createCuboidShape(14, 3, 7, 16, 8, 9), Block.createCuboidShape(15, 4, 7, 14, 7, 9), BooleanBiFunction.ONLY_FIRST)));

	public MotionActivatedLightBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx){
		switch(state.get(FACING))
		{
			case NORTH: return SHAPE_NORTH;
			case EAST: return SHAPE_EAST;
			case SOUTH: return SHAPE_SOUTH;
			case WEST: return SHAPE_WEST;
			default: return VoxelShapes.fullCube();
		}
	}

	public static void toggleLight(World world, BlockPos pos, Owner owner, boolean isLit) {
		if(!world.isClient)
		{
			if(isLit)
			{
				BlockUtils.setBlockProperty(world, pos, LIT, true);

				if(((IOwnable) world.getBlockEntity(pos)) != null)
					((IOwnable) world.getBlockEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.MOTION_ACTIVATED_LIGHT, 1, false);
			}
			else
			{
				BlockUtils.setBlockProperty(world, pos, LIT, false);

				if(((IOwnable) world.getBlockEntity(pos)) != null)
					((IOwnable) world.getBlockEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.MOTION_ACTIVATED_LIGHT, 1, false);
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		Direction side = state.get(FACING);

		return side != Direction.UP && side != Direction.DOWN && BlockUtils.isSideSolid(world, pos.offset(side.getOpposite()), side);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return facing != Direction.UP && facing != Direction.DOWN && BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing) ? getDefaultState().with(FACING, facing) : null;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canPlaceAt(state, world, pos))
			world.breakBlock(pos, true);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(LIT);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new MotionActivatedLightTileEntity().attacks(LivingEntity.class, ConfigHandler.CONFIG.motionActivatedLightSearchRadius.get(), 1);
//	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		Direction facing = state.get(FACING);

		switch(mirror)
		{
			case LEFT_RIGHT:
				if(facing.getAxis() == Axis.Z)
					return state.with(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if(facing.getAxis() == Axis.X)
					return state.with(FACING, facing.getOpposite());
				break;
			case NONE: break;
		}

		return state;
	}
}
