package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class ClaymoreBlock extends OwnableBlock implements IExplosive {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.of("deactivated");
	private static final VoxelShape NORTH_OFF = VoxelShapes.union(Block.createCuboidShape(4, 0, 5, 12, 4, 7), VoxelShapes.union(Block.createCuboidShape(4, 4, 5, 12, 5, 6), VoxelShapes.union(Block.createCuboidShape(5, 4, 4, 6, 5, 5), VoxelShapes.union(Block.createCuboidShape(10, 4, 4, 11, 5, 5), VoxelShapes.union(Block.createCuboidShape(4, 4, 3, 5, 5, 4), Block.createCuboidShape(11, 4, 3, 12, 5, 4))))));
	private static final VoxelShape NORTH_ON = VoxelShapes.union(NORTH_OFF, VoxelShapes.union(Block.createCuboidShape(3, 4, 2, 4, 5, 3), Block.createCuboidShape(12, 4, 2, 13, 5, 3)));
	private static final VoxelShape EAST_OFF = VoxelShapes.union(Block.createCuboidShape(9, 0, 4, 11, 4, 12), VoxelShapes.union(Block.createCuboidShape(10, 4, 4, 11, 5, 12), VoxelShapes.union(Block.createCuboidShape(11, 4, 5, 12, 5, 6), VoxelShapes.union(Block.createCuboidShape(11, 4, 10, 12, 5, 11), VoxelShapes.union(Block.createCuboidShape(12, 4, 4, 13, 5, 5), Block.createCuboidShape(12, 4, 11, 13, 5, 12))))));
	private static final VoxelShape EAST_ON = VoxelShapes.union(EAST_OFF, VoxelShapes.union(Block.createCuboidShape(13, 4, 3, 14, 5, 4), Block.createCuboidShape(13, 4, 12, 14, 5, 13)));
	private static final VoxelShape SOUTH_OFF = VoxelShapes.union(Block.createCuboidShape(4, 0, 9, 12, 4, 11), VoxelShapes.union(Block.createCuboidShape(4, 4, 10, 12, 5, 11), VoxelShapes.union(Block.createCuboidShape(5, 4, 11, 6, 5, 12), VoxelShapes.union(Block.createCuboidShape(10, 4, 11, 11, 5, 12), VoxelShapes.union(Block.createCuboidShape(4, 4, 12, 5, 5, 13), Block.createCuboidShape(11, 4, 12, 12, 5, 13))))));
	private static final VoxelShape SOUTH_ON = VoxelShapes.union(SOUTH_OFF, VoxelShapes.union(Block.createCuboidShape(3, 4, 13, 4, 5, 14), Block.createCuboidShape(12, 4, 13, 13, 5, 14)));
	private static final VoxelShape WEST_OFF = VoxelShapes.union(Block.createCuboidShape(7, 0, 4, 5, 4, 12), VoxelShapes.union(Block.createCuboidShape(6, 4, 4, 5, 5, 12), VoxelShapes.union(Block.createCuboidShape(5, 4, 5, 4, 5, 6), VoxelShapes.union(Block.createCuboidShape(5, 4, 10, 4, 5, 11), VoxelShapes.union(Block.createCuboidShape(4, 4, 4, 3, 5, 5), Block.createCuboidShape(4, 4, 11, 3, 5, 12))))));
	private static final VoxelShape WEST_ON = VoxelShapes.union(WEST_OFF, VoxelShapes.union(Block.createCuboidShape(3, 4, 3, 2, 5, 4), Block.createCuboidShape(3, 4, 12, 2, 5, 13)));

	public ClaymoreBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(DEACTIVATED, false));
	}

	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines ? -1F : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			world.breakBlock(pos, true);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return BlockUtils.isSideSolid(world, pos.down(), Direction.UP);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient)
			if(!player.inventory.getMainHandStack().isEmpty() && player.inventory.getMainHandStack().getItem() == SCContent.WIRE_CUTTERS){
				world.setBlockState(pos, SCContent.CLAYMORE.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, true));
				return ActionResult.SUCCESS;
			}else if(!player.inventory.getMainHandStack().isEmpty() && player.inventory.getMainHandStack().getItem() == Items.FLINT_AND_STEEL){
				world.setBlockState(pos, SCContent.CLAYMORE.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, false));
				return ActionResult.SUCCESS;
			}

		return ActionResult.PASS;
	}

//	@Override // Forge method
//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
//		if (!player.isCreative() && !world.isClient && !world.getBlockState(pos).get(ClaymoreBlock.DEACTIVATED))
//		{
//			world.breakBlock(pos, false);
//
//			if(!EntityUtils.doesPlayerOwn(player, world, pos))
//				world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
//		}
//
//		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//	}
//
//	@Override
//	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
//	{
//		if (!world.isClient && world.getBlockState(pos).contains(ClaymoreBlock.DEACTIVATED) && !world.getBlockState(pos).get(ClaymoreBlock.DEACTIVATED))
//		{
//			if(pos.equals(new BlockPos(explosion.getPosition()))) // Forge method, TODO
//				return;
//
//			world.breakBlock(pos, false);
//			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
//		}
//	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing()).with(DEACTIVATED, false);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isClient)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isClient)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isClient){
			world.breakBlock(pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.5F, true, DestructionType.BREAK);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		switch(state.get(FACING))
		{
			case NORTH:
				if(state.get(DEACTIVATED))
					return NORTH_OFF;
				else
					return NORTH_ON;
			case EAST:
				if(state.get(DEACTIVATED))
					return EAST_OFF;
				else
					return EAST_ON;
			case SOUTH:
				if(state.get(DEACTIVATED))
					return SOUTH_OFF;
				else
					return SOUTH_ON;
			case WEST:
				if(state.get(DEACTIVATED))
					return WEST_OFF;
				else
					return WEST_ON;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new ClaymoreTileEntity();
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
