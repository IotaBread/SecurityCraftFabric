package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
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
//import net.minecraftforge.common.MinecraftForge;

public class KeypadChestBlock extends ChestBlock implements IPasswordConvertible {

	public KeypadChestBlock(Settings settings){
		super(settings, () -> SCContent.teTypeKeypadChest);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient && world.getBlockEntity(pos) instanceof KeypadChestTileEntity && !isChestBlocked(world, pos)) {
			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.BLACKLIST))
				return ActionResult.FAIL;
			else if(ModuleUtils.checkForModule(world, pos, player, ModuleType.WHITELIST))
				activate(world, pos, player);
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER))
				((KeypadChestTileEntity) world.getBlockEntity(pos)).openPasswordGUI(player);
		}

		return ActionResult.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		if(!world.isClient) {
			BlockState state = world.getBlockState(pos);
			ChestBlock block = (ChestBlock)state.getBlock();
			NamedScreenHandlerFactory inamedcontainerprovider = block.createScreenHandlerFactory(state, world, pos);
			if (inamedcontainerprovider != null) {
				player.openHandledScreen(inamedcontainerprovider);
				player.incrementStat(Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST));
			}
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onPlaced(world, pos, state, entity, stack);

		boolean isPlayer = entity instanceof PlayerEntity;

		if(isPlayer)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) entity);

		if(world.getBlockEntity(pos.east()) instanceof KeypadChestTileEntity && isPlayer && ((KeypadChestTileEntity) world.getBlockEntity(pos.east())).getOwner().isOwner((PlayerEntity) entity))
			((KeypadChestTileEntity)(world.getBlockEntity(pos))).setPassword(((KeypadChestTileEntity) world.getBlockEntity(pos.east())).getPassword());
		else if(world.getBlockEntity(pos.west()) instanceof KeypadChestTileEntity && isPlayer && ((KeypadChestTileEntity) world.getBlockEntity(pos.west())).getOwner().isOwner((PlayerEntity) entity))
			((KeypadChestTileEntity)(world.getBlockEntity(pos))).setPassword(((KeypadChestTileEntity) world.getBlockEntity(pos.west())).getPassword());
		else if(world.getBlockEntity(pos.south()) instanceof KeypadChestTileEntity && isPlayer && ((KeypadChestTileEntity) world.getBlockEntity(pos.south())).getOwner().isOwner((PlayerEntity) entity))
			((KeypadChestTileEntity)(world.getBlockEntity(pos))).setPassword(((KeypadChestTileEntity) world.getBlockEntity(pos.south())).getPassword());
		else if(world.getBlockEntity(pos.north()) instanceof KeypadChestTileEntity && isPlayer && ((KeypadChestTileEntity) world.getBlockEntity(pos.north())).getOwner().isOwner((PlayerEntity) entity))
			((KeypadChestTileEntity)(world.getBlockEntity(pos))).setPassword(((KeypadChestTileEntity) world.getBlockEntity(pos.north())).getPassword());
	}

//	@Override // Forge method
//	public void onNeighborChange(BlockState state, WorldView world, BlockPos pos, BlockPos neighbor){
//		super.onNeighborChange(state, world, pos, neighbor);
//		KeypadChestTileEntity ChestTileEntity = (KeypadChestTileEntity)world.getBlockEntity(pos);
//
//		if (ChestTileEntity != null)
//			ChestTileEntity.resetBlock();
//
//	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public BlockEntity createBlockEntity(BlockView reader)
	{
		return new KeypadChestTileEntity();
	}

	public static boolean isChestBlocked(World world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).isSolidBlock(world, pos.up());
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.CHEST;
	}

	@Override
	public boolean convert(PlayerEntity player, World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		Direction facing = state.get(FACING);
		ChestType type = state.get(CHEST_TYPE);

		convertChest(player, world, pos, facing, type);

		if(type != ChestType.SINGLE)
		{
			BlockPos newPos = pos.offset(getFacing(state));
			BlockState newState = world.getBlockState(newPos);
			Direction newFacing = newState.get(FACING);
			ChestType newType = newState.get(CHEST_TYPE);

			convertChest(player, world, newPos, newFacing, newType);
		}

		return true;
	}

	private void convertChest(PlayerEntity player, World world, BlockPos pos, Direction facing, ChestType type)
	{
		ChestBlockEntity chest = (ChestBlockEntity)world.getBlockEntity(pos);
		CompoundTag tag = chest.toTag(new CompoundTag());

		chest.clear();
		world.setBlockState(pos, SCContent.KEYPAD_CHEST.getDefaultState().with(FACING, facing).with(CHEST_TYPE, type));
		((IOwnable) world.getBlockEntity(pos)).getOwner().set(player.getUuid().toString(), player.getName().getString());
		((ChestBlockEntity)world.getBlockEntity(pos)).fromTag(world.getBlockState(pos), tag);
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
