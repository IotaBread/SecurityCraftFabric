package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public class KeypadBlock extends DisguisableBlock implements IPasswordConvertible, BlockEntityProvider {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public KeypadBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(state.get(POWERED))
			return ActionResult.PASS;
		else if(!world.isClient)
		{
			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.BLACKLIST))
				return ActionResult.FAIL;

			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.WHITELIST))
				activate(world, pos, ((KeypadTileEntity)world.getBlockEntity(pos)).getSignalLength());
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER) && !PlayerUtils.isHoldingItem(player, SCContent.KEY_PANEL))
				((IPasswordProtected) world.getBlockEntity(pos)).openPasswordGUI(player);
		}

		return ActionResult.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, int signalLength){
		BlockUtils.setBlockProperty(world, pos, POWERED, true);
		world.updateNeighborsAlways(pos, SCContent.KEYPAD);
		world.getBlockTickScheduler().schedule(pos, SCContent.KEYPAD, signalLength);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		BlockUtils.setBlockProperty(world, pos, POWERED, false);
		world.updateNeighborsAlways(pos, SCContent.KEYPAD);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state){
		return true;
	}

//	@Override // Forge method
//	public boolean shouldCheckWeakPower(BlockState state, WorldView world, BlockPos pos, Direction side)
//	{
//		return false;
//	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(POWERED, false);
	}

	@Override
	public ItemStack getPickStack(BlockView worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new KeypadTileEntity();
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.FRAME;
	}

	@Override
	public boolean convert(PlayerEntity player, World world, BlockPos pos)
	{
		world.setBlockState(pos, SCContent.KEYPAD.getDefaultState().with(KeypadBlock.FACING, world.getBlockState(pos).get(FrameBlock.FACING)).with(KeypadBlock.POWERED, false));
		((IOwnable) world.getBlockEntity(pos)).setOwner(player.getUuid().toString(), player.getName().getString());
		return true;
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
