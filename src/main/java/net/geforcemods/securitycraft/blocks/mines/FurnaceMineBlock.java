package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class FurnaceMineBlock extends ExplosiveBlock implements IOverlayDisplay, IBlockMine {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public FurnaceMineBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

//	@Override // TODO
//	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
//		if (!world.isClient)
//		{
//			if(pos.equals(new BlockPos(explosion.getPosition())))
//				return;
//
//			explode(world, pos);
//		}
//	}

//	@Override // TODO
//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
//		if(!world.isClient)
//			if(player != null && player.isCreative() && !ConfigHandler.CONFIG.mineExplodesWhenInCreative)
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
//				explode(world, pos);
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			}
//
//		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(world.isClient)
			return ActionResult.PASS;
		else if(player.inventory.getMainHandStack().getItem() != SCContent.REMOTE_ACCESS_MINE && !EntityUtils.doesPlayerOwn(player, world, pos)){
			explode(world, pos);
			return ActionResult.SUCCESS;
		}
		else
			return ActionResult.FAIL;
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
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public void explode(World world, BlockPos pos) {
		world.breakBlock(pos, false);

		if(ConfigHandler.CONFIG.smallerMineExplosion)
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY(), pos.getZ(), 5.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);

	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return new ItemStack(Blocks.FURNACE);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return false;
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
