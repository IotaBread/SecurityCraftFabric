package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
//import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class IronFenceBlock extends OwnableBlock implements IIntersectable {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey().getAxis().isHorizontal()).collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public IronFenceBlock(Settings settings)
	{
		super(settings);

		setDefaultState(stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		renderShapes = func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = func_196408_a(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = func_196408_a(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos)
	{
		return renderShapes[getIndex(state)];
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
		VoxelShape[] returnValue = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.union(voxelshape2, voxelshape1), VoxelShapes.union(voxelshape3, voxelshape1), VoxelShapes.union(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.union(voxelshape2, voxelshape4), VoxelShapes.union(voxelshape3, voxelshape4), VoxelShapes.union(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.union(voxelshape2, voxelshape5), VoxelShapes.union(voxelshape3, voxelshape5), VoxelShapes.union(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			returnValue[i] = VoxelShapes.union(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return collisionShapes[getIndex(state)];
	}

	public boolean func_220111_a(BlockState state, boolean p_220111_2_, Direction direction)
	{
		Block block = state.getBlock();
		boolean flag = block.isIn(BlockTags.FENCES) && state.getMaterial() == material;
		boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, direction);

		return !cannotConnect(block) && p_220111_2_ || flag || flag1;
	}

	private static int getMask(Direction facing)
	{
		return 1 << facing.getHorizontal();
	}

	protected int getIndex(BlockState state)
	{
		int i = 0;

		if (state.get(NORTH))
			i |= getMask(Direction.NORTH);

		if (state.get(EAST))
			i |= getMask(Direction.EAST);

		if (state.get(SOUTH))
			i |= getMask(Direction.SOUTH);

		if (state.get(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		switch(rot)
		{
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
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockView iblockreader = ctx.getWorld();
		BlockPos blockpos = ctx.getBlockPos();
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate = iblockreader.getBlockState(blockpos1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
		return super.getPlacementState(ctx).with(NORTH, func_220111_a(blockstate, blockstate.isSideSolidFullSquare(iblockreader, blockpos1, Direction.SOUTH), Direction.SOUTH)).with(EAST, func_220111_a(blockstate1, blockstate1.isSideSolidFullSquare(iblockreader, blockpos2, Direction.WEST), Direction.WEST)).with(SOUTH, func_220111_a(blockstate2, blockstate2.isSideSolidFullSquare(iblockreader, blockpos3, Direction.NORTH), Direction.NORTH)).with(WEST, func_220111_a(blockstate3, blockstate3.isSideSolidFullSquare(iblockreader, blockpos4, Direction.EAST), Direction.EAST));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getAxis().getType() == Direction.Type.HORIZONTAL ? state.with(FACING_TO_PROPERTY_MAP.get(facing), func_220111_a(facingState, facingState.isSideSolidFullSquare(world, facingPos, facing.getOpposite()), facing.getOpposite())) : super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		return ActionResult.FAIL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof ItemEntity)
			return;
		//owner check
		else if(entity instanceof PlayerEntity)
		{
			if(((OwnableTileEntity) world.getBlockEntity(pos)).getOwner().isOwner((PlayerEntity)entity))
				return;
		}
		else if(!world.isClient && entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningEntity lightning = WorldUtils.createLightning(world, Vec3d.ofBottomCenter(pos), true);

			creeper.onStruckByLightning((ServerWorld)world, lightning);
			creeper.extinguish();
			return;
		}

		entity.damage(CustomDamageSources.ELECTRICITY, 6.0F); //3 hearts per attack
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int eventID, int eventParam)
	{
		super.onSyncedBlockEvent(state, world, pos, eventID, eventParam);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.onSyncedBlockEvent(eventID, eventParam);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new SecurityCraftTileEntity().intersectsEntities();
//	}
}