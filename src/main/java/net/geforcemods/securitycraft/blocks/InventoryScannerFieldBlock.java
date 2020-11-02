package net.geforcemods.securitycraft.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
//import net.minecraftforge.common.util.Constants.NBT;

import java.util.function.BiFunction;

public class InventoryScannerFieldBlock extends OwnableBlock implements IIntersectable {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.of("horizontal");
	private static final VoxelShape SHAPE_EW = Block.createCuboidShape(0, 0, 6, 16, 16, 10);
	private static final VoxelShape SHAPE_NS = Block.createCuboidShape(6, 0, 0, 10, 16, 16);
	private static final VoxelShape HORIZONTAL_SHAPE = Block.createCuboidShape(0, 6, 0, 16, 10, 16);

	public InventoryScannerFieldBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HORIZONTAL, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		if(entity instanceof PlayerEntity && !EntityUtils.isInvisible((PlayerEntity)entity))
		{
			if(ModuleUtils.checkForModule(world, connectedScanner.getPos(), (PlayerEntity)entity, ModuleType.WHITELIST))
				return;

			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty())
					checkInventory((PlayerEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
		else if(entity instanceof ItemEntity)
		{
			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((ItemEntity)entity).getStack().isEmpty())
					checkItemEntity((ItemEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
	}

	public static void checkInventory(PlayerEntity player, InventoryScannerTileEntity te, ItemStack stack)
	{
		boolean hasSmartModule = te.hasModule(ModuleType.SMART);
		boolean hasStorageModule = te.hasModule(ModuleType.STORAGE);

		if(te.hasModule(ModuleType.REDSTONE))
		{
			redstoneLoop(player.inventory.main, stack, te, hasSmartModule, hasStorageModule);
			redstoneLoop(player.inventory.armor, stack, te, hasSmartModule, hasStorageModule);
			redstoneLoop(player.inventory.offHand, stack, te, hasSmartModule, hasStorageModule);
		}

		if(hasStorageModule && !te.getOwner().isOwner(player))
		{
			checkLoop(player.inventory.main, stack, te, hasSmartModule, hasStorageModule);
			checkLoop(player.inventory.armor, stack, te, hasSmartModule, hasStorageModule);
			checkLoop(player.inventory.offHand, stack, te, hasSmartModule, hasStorageModule);
		}
	}

	private static void redstoneLoop(DefaultedList<ItemStack> inventory, ItemStack stack, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule)
	{
		for(int i = 1; i <= inventory.size(); i++)
		{
			ItemStack itemStackChecking = inventory.get(i - 1);

			if(!itemStackChecking.isEmpty())
			{
				if((hasSmartModule && areItemStacksEqual(itemStackChecking, stack) && ItemStack.areTagsEqual(itemStackChecking, stack))
						|| (!hasSmartModule && itemStackChecking.getItem() == stack.getItem()) || checkForShulkerBox(itemStackChecking, stack, te, hasSmartModule, hasStorageModule))
				{
					updateInventoryScannerPower(te);
				}
			}
		}
	}

	private static void checkLoop(DefaultedList<ItemStack> inventory, ItemStack stack, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule)
	{
		for(int i = 1; i <= inventory.size(); i++)
		{
			ItemStack itemStackChecking = inventory.get(i - 1);

			if(!itemStackChecking.isEmpty())
			{
				checkForShulkerBox(itemStackChecking, stack, te, hasSmartModule, hasStorageModule);

				if((hasSmartModule && areItemStacksEqual(itemStackChecking, stack) && ItemStack.areTagsEqual(itemStackChecking, stack))
						|| (!hasSmartModule && itemStackChecking.getItem() == stack.getItem()))
				{
					if(hasStorageModule)
						te.addItemToStorage(inventory.get(i - 1));

					inventory.set(i - 1, ItemStack.EMPTY);
				}
			}
		}
	}

	public static void checkItemEntity(ItemEntity entity, InventoryScannerTileEntity te, ItemStack stack)
	{
		boolean hasSmartModule = te.hasModule(ModuleType.SMART);
		boolean hasStorageModule = te.hasModule(ModuleType.STORAGE);

		if(te.hasModule(ModuleType.REDSTONE))
		{
			if((hasSmartModule && areItemStacksEqual(entity.getStack(), stack) && ItemStack.areTagsEqual(entity.getStack(), stack))
					|| (!hasSmartModule && entity.getStack().getItem() == stack.getItem()) || checkForShulkerBox(entity.getStack(), stack, te, hasSmartModule, hasStorageModule))
			{
				updateInventoryScannerPower(te);
			}
		}

		if(hasStorageModule)
		{
			checkForShulkerBox(entity.getStack(), stack, te, hasSmartModule, hasStorageModule);

			if((hasSmartModule && areItemStacksEqual(entity.getStack(), stack) && ItemStack.areTagsEqual(entity.getStack(), stack))
					|| (!hasSmartModule && entity.getStack().getItem() == stack.getItem()))
			{
				if(hasStorageModule)
					te.addItemToStorage(entity.getStack());

				entity.remove();
			}
		}
	}

	private static boolean checkForShulkerBox(ItemStack item, ItemStack stackToCheck, InventoryScannerTileEntity te, boolean hasSmartModule, boolean hasStorageModule) {
		boolean deletedItem = false;

		if(item != null) {
			if(!item.isEmpty() && item.getTag() != null && Block.getBlockFromItem(item.getItem()) instanceof ShulkerBoxBlock) {
				ListTag list = item.getTag().getCompound("BlockEntityTag").getList("Items", NbtType.COMPOUND);

				for(int i = 0; i < list.size(); i++) {
					ItemStack itemInChest = ItemStack.fromTag(list.getCompound(i));
					if((hasSmartModule && areItemStacksEqual(itemInChest, stackToCheck) && ItemStack.areTagsEqual(itemInChest, stackToCheck)) || (!hasSmartModule && areItemStacksEqual(itemInChest, stackToCheck))) {
						list.remove(i);
						deletedItem = true;

						if(hasStorageModule)
							te.addItemToStorage(itemInChest);
					}
				}
			}
		}

		return deletedItem;
	}

	private static void updateInventoryScannerPower(InventoryScannerTileEntity te)
	{
		if(!te.shouldProvidePower())
			te.setShouldProvidePower(true);

		te.setCooldown(60);
		checkAndUpdateTEAppropriately(te);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()).getBlock(), 1, true);
	}

//	/**
//	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
//	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.areEqual(s1, s2);
	}

	private static void checkAndUpdateTEAppropriately(InventoryScannerTileEntity te)
	{
		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(te.getWorld(), te.getPos());

		if(connectedScanner == null)
			return;

		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getCachedState().getBlock(), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.getPos(), connectedScanner.getCachedState().getBlock(), 1, true);
	}

	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state)
	{
		if(!world.isClient())
		{
			Direction facing = state.get(FACING);

			if (facing == Direction.EAST || facing == Direction.WEST)
			{
				checkAndDestroyFields(world, pos, (p, i) -> p.west(i));
				checkAndDestroyFields(world, pos, (p, i) -> p.east(i));
			}
			else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			{
				checkAndDestroyFields(world, pos, (p, i) -> p.north(i));
				checkAndDestroyFields(world, pos, (p, i) -> p.south(i));
			}
		}
	}

	private void checkAndDestroyFields(WorldAccess world, BlockPos pos, BiFunction<BlockPos,Integer,BlockPos> posModifier)
	{
		for(int i = 0; i < ConfigHandler.CONFIG.inventoryScannerRange; i++)
		{
			BlockPos modifiedPos = posModifier.apply(pos, i);

			if(BlockUtils.getBlock(world, modifiedPos) == SCContent.INVENTORY_SCANNER)
			{
				for(int j = 1; j < i; j++)
				{
					world.breakBlock(posModifier.apply(pos, j), false);
				}

				break;
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		if(state.get(HORIZONTAL))
			return HORIZONTAL_SHAPE;

		Direction facing = state.get(FACING);

		if (facing == Direction.EAST || facing == Direction.WEST)
			return SHAPE_EW; //ew
		else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			return SHAPE_NS; //ns
		return VoxelShapes.fullCube();
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HORIZONTAL);
	}

//	@Override // Forge method
//	public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos, PlayerEntity player)
//	{
//		return ItemStack.EMPTY;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new SecurityCraftTileEntity().intersectsEntities();
//	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		if (side == Direction.UP || side == Direction.DOWN)
			if (state.getBlock() == adjacentBlockState.getBlock())
				return true;

		return super.isSideInvisible(state, adjacentBlockState, side);
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
