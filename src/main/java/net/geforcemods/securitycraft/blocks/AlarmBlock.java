package net.geforcemods.securitycraft.blocks;

//import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
//import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;

//import java.util.Random;

public class AlarmBlock extends OwnableBlock {

	public static final BooleanProperty LIT = Properties.LIT;
	public static final DirectionProperty FACING = Properties.FACING;
	private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.createCuboidShape(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.createCuboidShape(4, 8, 4, 12, 16, 12);

	public AlarmBlock(Settings settings) {
		super(settings);

		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.UP).with(LIT, false));
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		Direction facing = state.get(FACING);

		return facing == Direction.UP && BlockUtils.isSideSolid(world, pos.down(), Direction.UP) ? true : BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		if (!canPlaceAt(state, world, pos))
			world.breakBlock(pos, true);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing) ? getDefaultState().with(FACING, facing) : null;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag) {
		if(world.isClient)
			return;
		else
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 5);
	}

//	/**
//	 * Ticks the block if it's been scheduled
//	 */
//	@Override
//	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
//	{
//		if(!world.isClient){
//			playSoundAndUpdate(world, pos);
//
//			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 5);
//		}
//	}
//
//	@Override
//	public void onNeighborChange(BlockState state, WorldView w, BlockPos pos, BlockPos neighbor){
//		if(w.isClient() || !(w instanceof World))
//			return;
//
//		World world = (World)w;
//
//		playSoundAndUpdate((world), pos);
//
//		Direction facing = world.getBlockState(pos).get(FACING);
//
//		if (!BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing))
//			world.breakBlock(pos, true);
//	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		Direction facing = state.get(FACING);

		switch(facing){
			case EAST:
				return SHAPE_EAST;
			case WEST:
				return SHAPE_WEST;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case UP:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
		}

		return VoxelShapes.fullCube();
	}

//	private void playSoundAndUpdate(World world, BlockPos pos){
//		if(world.getBlockState(pos).getBlock() != SCContent.ALARM.get() || !(world.getBlockEntity(pos) instanceof AlarmTileEntity)) return;
//
//		if(world.getReceivedRedstonePower(pos) > 0){
//			boolean isPowered = ((AlarmTileEntity) world.getBlockEntity(pos)).isPowered();
//
//			if(!isPowered){
//				Owner owner = ((AlarmTileEntity) world.getBlockEntity(pos)).getOwner();
//				BlockUtils.setBlockProperty(world, pos, LIT, true);
//				((AlarmTileEntity) world.getBlockEntity(pos)).getOwner().set(owner);
//				((AlarmTileEntity) world.getBlockEntity(pos)).setPowered(true);
//			}
//
//		}else{
//			boolean isPowered = ((AlarmTileEntity) world.getBlockEntity(pos)).isPowered();
//
//			if(isPowered){
//				Owner owner = ((AlarmTileEntity) world.getBlockEntity(pos)).getOwner();
//				BlockUtils.setBlockProperty(world, pos, LIT, false);
//				((AlarmTileEntity) world.getBlockEntity(pos)).getOwner().set(owner);
//				((AlarmTileEntity) world.getBlockEntity(pos)).setPowered(false);
//			}
//		}
//	}
//
//	@Override
//	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
//	{
//		return new ItemStack(SCContent.ALARM.get().asItem());
//	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder){
		builder.add(FACING);
		builder.add(LIT);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView reader){
//		return new AlarmTileEntity();
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
