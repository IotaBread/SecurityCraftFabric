package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
//import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraft.world.WorldView;
//import net.minecraftforge.fml.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock implements BlockEntityProvider {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.of("horizontal");

	public InventoryScannerBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HORIZONTAL, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient)
		{
			if(isFacingAnotherScanner(world, pos) && player instanceof ServerPlayerEntity)
			{
				BlockEntity te = world.getBlockEntity(pos);

//				if(te instanceof NamedScreenHandlerFactory) // TODO
//					NetworkHooks.openGui((ServerPlayerEntity)player, (NamedScreenHandlerFactory)te, pos);
			}
			else if(hand == Hand.MAIN_HAND)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.INVENTORY_SCANNER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:invScan.notConnected"), Formatting.RED);
		}

		return ActionResult.SUCCESS;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onPlaced(world, pos, state, entity, stack);

		if(world.isClient)
			return;

		checkAndPlaceAppropriately(world, pos);
	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		InventoryScannerTileEntity connectedScanner = getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null || !connectedScanner.getOwner().equals(((InventoryScannerTileEntity)world.getBlockEntity(pos)).getOwner()))
			return;

		boolean horizontal = false;

		if(connectedScanner.getCachedState().get(HORIZONTAL))
			horizontal = true;

		((InventoryScannerTileEntity)world.getBlockEntity(pos)).setHorizontal(horizontal);

		Direction facing = world.getBlockState(pos).get(FACING);
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.offset(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD)
				return;
		}

		for(int i = 1; i < loopBoundary; i++)
		{
			world.setBlockState(pos.offset(facing, i), SCContent.INVENTORY_SCANNER_FIELD.getDefaultState().with(FACING, facing).with(HORIZONTAL, horizontal));
		}

		CustomizableTileEntity.link((CustomizableTileEntity)world.getBlockEntity(pos), connectedScanner);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isClient || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerTileEntity connectedScanner = null;

		for(Direction facing : Direction.Type.HORIZONTAL)
		{
			for(int i = 1; i <= ConfigHandler.CONFIG.inventoryScannerRange; i++)
			{
				BlockPos offsetIPos = pos.offset(facing, i);

				if(BlockUtils.getBlock(world, offsetIPos) == SCContent.INVENTORY_SCANNER)
				{
					for(int j = 1; j < i; j++)
					{
						BlockPos offsetJPos = pos.offset(facing, j);
						BlockState field = world.getBlockState(offsetJPos);

						//checking if the field is oriented correctly
						if(field.getBlock() == SCContent.INVENTORY_SCANNER_FIELD)
						{
							if(facing == Direction.WEST || facing == Direction.EAST)
							{
								if(field.get(InventoryScannerFieldBlock.FACING) == Direction.WEST || field.get(InventoryScannerFieldBlock.FACING) == Direction.EAST)
									world.breakBlock(offsetJPos, false);
							}
							else if(facing == Direction.NORTH || facing == Direction.SOUTH)
							{
								if(field.get(InventoryScannerFieldBlock.FACING) == Direction.NORTH || field.get(InventoryScannerFieldBlock.FACING) == Direction.SOUTH)
									world.breakBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (InventoryScannerTileEntity)world.getBlockEntity(offsetIPos);
					break;
				}
			}
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().size(); i++)
			{
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}
		}

		super.onStateReplaced(state, world, pos, newState, isMoving);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static InventoryScannerTileEntity getConnectedInventoryScanner(World world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).get(FACING);

		for(int i = 0; i <= ConfigHandler.CONFIG.inventoryScannerRange; i++)
		{
			BlockPos offsetPos = pos.offset(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.isAir() && block != SCContent.INVENTORY_SCANNER_FIELD && block != SCContent.INVENTORY_SCANNER)
				return null;

			if(block == SCContent.INVENTORY_SCANNER && state.get(FACING) == facing.getOpposite())
				return (InventoryScannerTileEntity)world.getBlockEntity(offsetPos);
		}

		return null;
	}

//	@Override // Forge method
//	public void onNeighborChange(BlockState state, WorldView world, BlockPos pos, BlockPos neighbor) {
//		checkAndPlaceAppropriately((World)world, pos);
//	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
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
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side)
	{
		if(!(blockAccess.getBlockEntity(pos) instanceof InventoryScannerTileEntity)){
			return 0;
		}

		return (((InventoryScannerTileEntity) blockAccess.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE) && ((InventoryScannerTileEntity) blockAccess.getBlockEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side)
	{
		return getWeakRedstonePower(blockState, blockAccess, pos, side);
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
		builder.add(FACING, HORIZONTAL);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InventoryScannerTileEntity();
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
