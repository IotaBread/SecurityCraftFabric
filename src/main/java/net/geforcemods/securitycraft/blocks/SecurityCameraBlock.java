package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityCameraBlock extends OwnableBlock{

	public static final DirectionProperty FACING = DirectionProperty.of("facing", facing -> facing != Direction.UP);
	public static final BooleanProperty POWERED = Properties.POWERED;
	private static final VoxelShape SHAPE_SOUTH = VoxelShapes.cuboid(new Box(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F));
	private static final VoxelShape SHAPE_NORTH = VoxelShapes.cuboid(new Box(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F));
	private static final VoxelShape SHAPE_WEST = VoxelShapes.cuboid(new Box(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE = VoxelShapes.cuboid(new Box(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F));
	private static final VoxelShape SHAPE_DOWN = VoxelShapes.union(Block.createCuboidShape(7, 15, 5, 9, 16, 11), VoxelShapes.union(Block.createCuboidShape(6, 15, 6, 7, 16, 10), VoxelShapes.union(Block.createCuboidShape(5, 15, 7, 6, 16, 9), VoxelShapes.union(Block.createCuboidShape(9, 15, 6, 10, 16, 10), VoxelShapes.union(Block.createCuboidShape(10, 15, 7, 11, 16, 9), Block.createCuboidShape(7, 14, 7, 9, 15, 9))))));

	public SecurityCameraBlock(Settings settings) {
		super(settings);
		stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView access, BlockPos pos, ShapeContext ctx){
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return state.get(FACING) == Direction.DOWN ? BlockRenderType.MODEL : BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onStateReplaced(state, world, pos, newState, isMoving);

		world.updateNeighborsAlways(pos.north(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAlways(pos.south(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAlways(pos.east(), world.getBlockState(pos).getBlock());
		world.updateNeighborsAlways(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		Direction dir = state.get(FACING);

		if(dir == Direction.SOUTH)
			return SHAPE_SOUTH;
		else if(dir == Direction.NORTH)
			return SHAPE_NORTH;
		else if(dir == Direction.WEST)
			return SHAPE_WEST;
		else if(dir == Direction.DOWN)
			return SHAPE_DOWN;
		else
			return SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return ctx.getSide() != Direction.UP ? getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer()) : null;
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		BlockState state = getDefaultState().with(POWERED, false);

		if(BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing))
			return state.with(FACING, facing).with(POWERED, false);
		else{
			Iterator<?> iterator = Direction.Type.HORIZONTAL.iterator();
			Direction iFacing;

			do{
				if(!iterator.hasNext())
					return state;

				iFacing = (Direction)iterator.next();
			}while (!BlockUtils.isSideSolid(world, pos.offset(iFacing.getOpposite()), iFacing));

			return state.with(FACING, facing).with(POWERED, false);
		}
	}

//	public void mountCamera(World world, int x, int y, int z, int id, PlayerEntity player){
//		if(world.isClient && player.getVehicle() == null)
//		{
//			GameOptions settings = MinecraftClient.getInstance().options;
//
//			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SECURITY_CAMERA.getTranslationKey()), ClientUtils.localize("messages.securitycraft:securityCamera.mounted",
//					settings.keyForward.getBoundKeyLocalizedText(),
//					settings.keyLeft.getBoundKeyLocalizedText(),
//					settings.keyBack.getBoundKeyLocalizedText(),
//					settings.keyRight.getBoundKeyLocalizedText(),
//					KeyBindings.cameraZoomIn.getBoundKeyLocalizedText(),
//					KeyBindings.cameraZoomOut.getBoundKeyLocalizedText()), Formatting.GREEN);
//		}
//
//		if(player.getVehicle() instanceof SecurityCameraEntity){
//			SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, (SecurityCameraEntity) player.getVehicle());
//			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
//			player.startRiding(dummyEntity);
//			return;
//		}
//
//		SecurityCameraEntity dummyEntity = new SecurityCameraEntity(world, x, y, z, id, player);
//		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
//		player.startRiding(dummyEntity);
//
//		if(world instanceof ServerWorld)
//		{
//			ServerWorld serverWorld = (ServerWorld)world;
//			List<Entity> loadedEntityList = serverWorld.getEntitiesByType().collect(Collectors.toList()); // TODO
//
//			for(Entity e : loadedEntityList)
//			{
//				if(e instanceof MobEntity)
//				{
//					if(((MobEntity)e).getTarget() == player)
//						((MobEntity)e).setTarget(null);
//				}
//			}
//		}
//	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		Direction facing = state.get(FACING);

		return BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state){
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView world, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView world, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canPlaceAt(world.getBlockState(pos), world, pos) && !canPlaceAt(state, world, pos))
			world.breakBlock(pos, true);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new SecurityCameraTileEntity().nameable();
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
