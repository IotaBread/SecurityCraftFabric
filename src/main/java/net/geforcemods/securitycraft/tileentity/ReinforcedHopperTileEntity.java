package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
//import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.ItemHandlerHelper;
//import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

//import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//fuck vanilla for not making the hopper te extensible
public class ReinforcedHopperTileEntity extends LootableContainerBlockEntity implements Hopper, Tickable, IOwnable
{
	private Owner owner = new Owner();
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;

	public ReinforcedHopperTileEntity()
	{
		super(SCContent.teTypeReinforcedHopper);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);

		if(!deserializeLootTable(tag))
			Inventories.fromTag(tag, inventory);

		if(tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if(tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));

		transferCooldown = tag.getInt("TransferCooldown");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		if(!serializeLootTable(tag))
			Inventories.toTag(tag, inventory);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		tag.putInt("TransferCooldown", transferCooldown);
		return tag;
	}

	@Override
	public int size()
	{
		return inventory.size();
	}

	@Override
	public ItemStack removeStack(int index, int count)
	{
		checkLootInteraction(null);
		return Inventories.splitStack(getInvStackList(), index, count);
	}

	@Override
	public void setStack(int index, ItemStack stack)
	{
		checkLootInteraction(null);
		getInvStackList().set(index, stack);

		if(stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText("container.hopper");
	}

	@Override
	public void tick()
	{
		if(world != null && !world.isClient)
		{
			--transferCooldown;
			tickedGameTime = world.getTime();

			if(!isOnTransferCooldown())
			{
//				setTransferCooldown(0); // TODO
//				updateHopper(() -> pullItems(this));
			}
		}
	}

	private boolean updateHopper(Supplier<Boolean> idk)
	{
		if(world != null && !world.isClient)
		{
			if (!isOnTransferCooldown() && getCachedState().get(HopperBlock.ENABLED))
			{
				boolean hasChanged = false;

				if(!isInventoryEmpty())
					hasChanged = transferItemsOut();

				if(!isFull())
					hasChanged |= idk.get();

				if(hasChanged)
				{
					setTransferCooldown(8);
					markDirty();
					return true;
				}
			}

			return false;
		}
		else return false;
	}

	private boolean isInventoryEmpty()
	{
		for(ItemStack stack : inventory)
		{
			if(!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return isInventoryEmpty();
	}

	private boolean isFull()
	{
		for(ItemStack stack : inventory)
		{
			if(stack.isEmpty() || stack.getCount() != stack.getMaxCount())
				return false;
		}

		return true;
	}

	private boolean transferItemsOut()
	{
//		if(insertHook())
//			return true;

		Inventory inv = getInventoryForHopperTransfer();

		if(inv != null)
		{
			Direction direction = getCachedState().get(HopperBlock.FACING).getOpposite();

			if(isInventoryFull(inv, direction))
				return false;
			else
			{
				for(int i = 0; i < size(); ++i)
				{
					if(!getStack(i).isEmpty())
					{
						ItemStack copy = getStack(i).copy();
						ItemStack remainder = putStackInInventoryAllSlots(this, inv, removeStack(i, 1), direction);

						if(remainder.isEmpty())
						{
							inv.markDirty();
							return true;
						}

						setStack(i, copy);
					}
				}
			}
		}

		return false;
	}

	private static IntStream getSlotStreamForSide(Inventory inv, Direction dir)
	{
		return inv instanceof SidedInventory ? IntStream.of(((SidedInventory)inv).getAvailableSlots(dir)) : IntStream.range(0, inv.size());
	}

	private boolean isInventoryFull(Inventory inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> {
			ItemStack stack = inventory.getStack(slot);
			return stack.getCount() >= stack.getMaxCount();
		});
	}

	private static boolean isInventoryEmpty(Inventory inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> inventory.getStack(slot).isEmpty());
	}

//	public static boolean pullItems(Hopper hopper) // TODO
//	{
//		Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);
//
//		if(ret != null)
//			return ret;
//
//		Inventory inv = getSourceInventory(hopper);
//
//		if(inv != null)
//		{
//			Direction direction = Direction.DOWN;
//			return isInventoryEmpty(inv, direction) ? false : getSlotStreamForSide(inv, direction).anyMatch(slot -> pullItemFromSlot(hopper, inv, slot, direction));
//		}
//		else
//		{
//			for(ItemEntity entity : getCaptureItems(hopper))
//			{
//				if (captureItem(hopper, entity))
//					return true;
//			}
//
//			return false;
//		}
//	}

	private static boolean pullItemFromSlot(Hopper hopper, Inventory inventory, int index, Direction direction)
	{
		ItemStack stack = inventory.getStack(index);

		if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction))
		{
			ItemStack copy = stack.copy();
			ItemStack remainder = putStackInInventoryAllSlots(inventory, hopper, inventory.removeStack(index, 1), null);

			if(remainder.isEmpty())
			{
				inventory.markDirty();
				return true;
			}

			inventory.setStack(index, copy);
		}

		return false;
	}

	public static boolean captureItem(Inventory inv, ItemEntity entity)
	{
		boolean capturedEverything = false;
		ItemStack copy = entity.getStack().copy();
		ItemStack remainder = putStackInInventoryAllSlots(null, inv, copy, null);

		if(remainder.isEmpty())
		{
			capturedEverything = true;
			entity.remove();
		}
		else
			entity.setStack(remainder);

		return capturedEverything;
	}

	public static ItemStack putStackInInventoryAllSlots(Inventory source, Inventory destination, ItemStack stack, Direction direction)
	{
		if(destination instanceof SidedInventory && direction != null)
		{
			SidedInventory inv = (SidedInventory)destination;
			int[] slots = inv.getAvailableSlots(direction);

			for(int k = 0; k < slots.length && !stack.isEmpty(); ++k)
			{
				stack = insertStack(source, destination, stack, slots[k], direction);
			}
		}
		else
		{
			int destSize = destination.size();

			for(int j = 0; j < destSize && !stack.isEmpty(); ++j)
			{
				stack = insertStack(source, destination, stack, j, direction);
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(Inventory inventory, ItemStack stack, int index, Direction side)
	{
		if(!inventory.isValid(index, stack))
			return false;
		else return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(index, stack, side);
	}

	private static boolean canExtractItemFromSlot(Inventory inventory, ItemStack stack, int index, Direction side)
	{
		return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canExtract(index, stack, side);
	}

	private static ItemStack insertStack(Inventory source, Inventory destination, ItemStack stack, int index, Direction direction)
	{
		ItemStack destStack = destination.getStack(index);

		if(canInsertItemInSlot(destination, stack, index, direction))
		{
			boolean hasChanged = false;
			boolean isDestEmpty = destination.isEmpty();

			if(destStack.isEmpty())
			{
				destination.setStack(index, stack);
				stack = ItemStack.EMPTY;
				hasChanged = true;
			}
			else if (canCombine(destStack, stack))
			{
				int sizeDifference = stack.getMaxCount() - destStack.getCount();
				int minSize = Math.min(stack.getCount(), sizeDifference);
				stack.decrement(minSize);
				destStack.increment(minSize);
				hasChanged = minSize > 0;
			}

			if(hasChanged)
			{
				if (isDestEmpty && destination instanceof ReinforcedHopperTileEntity)
				{
					ReinforcedHopperTileEntity te = (ReinforcedHopperTileEntity)destination;

					if(!te.mayTransfer())
					{
						int k = 0;

						if(source instanceof ReinforcedHopperTileEntity) {
							ReinforcedHopperTileEntity te2 = (ReinforcedHopperTileEntity)source;

							if (te.tickedGameTime >= te2.tickedGameTime)
								k = 1;
						}

						te.setTransferCooldown(8 - k);
					}
				}

				destination.markDirty();
			}
		}

		return stack;
	}

	@Nullable
	private Inventory getInventoryForHopperTransfer()
	{
		Direction direction = getCachedState().get(HopperBlock.FACING);
		return getInventoryAtPosition(getWorld(), pos.offset(direction));
	}

	@Nullable
	public static Inventory getSourceInventory(Hopper hopper)
	{
		return getInventoryAtPosition(hopper.getWorld(), hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
	}

	public static List<ItemEntity> getCaptureItems(Hopper hopper)
	{
		return hopper.getInputAreaShape().getBoundingBoxes().stream().flatMap((box) -> {
			return hopper.getWorld().getEntitiesByClass(ItemEntity.class, box.offset(hopper.getHopperX() - 0.5D, hopper.getHopperY() - 0.5D, hopper.getHopperZ() - 0.5D), EntityPredicates.VALID_ENTITY).stream();
		}).collect(Collectors.toList());
	}

	@Nullable
	public static Inventory getInventoryAtPosition(World world, BlockPos pos)
	{
		return getInventoryAtPosition(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Nullable
	public static Inventory getInventoryAtPosition(World world, double x, double y, double z)
	{
		Inventory inv = null;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof InventoryProvider)
			inv = ((InventoryProvider)block).getInventory(state, world, pos);
		else if(state.getBlock().hasBlockEntity())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof Inventory)
			{
				inv = (Inventory)te;

				if(inv instanceof ChestBlockEntity && block instanceof ChestBlock)
					inv = ChestBlock.getInventory((ChestBlock)block, state, world, pos, true);
			}
		}

		if(inv == null)
		{
			List<Entity> list = world.getOtherEntities(null, new Box(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.VALID_INVENTORIES);

			if(!list.isEmpty())
				inv = (Inventory)list.get(world.random.nextInt(list.size()));
		}

		return inv;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if(stack1.getItem() != stack2.getItem())
			return false;
		else if(stack1.getDamage() != stack2.getDamage())
			return false;
		else if(stack1.getCount() > stack1.getMaxCount())
			return false;
		else return ItemStack.areTagsEqual(stack1, stack2);
	}

	@Override
	public double getHopperX()
	{
		return pos.getX() + 0.5D;
	}

	@Override
	public double getHopperY()
	{
		return pos.getY() + 0.5D;
	}

	@Override
	public double getHopperZ() {
		return pos.getZ() + 0.5D;
	}

	public void setTransferCooldown(int ticks)
	{
		transferCooldown = ticks;
	}

	private boolean isOnTransferCooldown()
	{
		return transferCooldown > 0;
	}

	public boolean mayTransfer()
	{
		return transferCooldown > 8;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList()
	{
		return inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> items)
	{
		inventory = items;
	}

	public void onEntityCollision(Entity entity)
	{
		if(entity instanceof ItemEntity)
		{
			BlockPos pos = getPos();

			if(VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), getInputAreaShape(), BooleanBiFunction.AND))
				updateHopper(() -> captureItem(this, (ItemEntity)entity));
		}
	}

	@Override
	protected ScreenHandler createScreenHandler(int id, PlayerInventory player)
	{
		return new HopperScreenHandler(id, player, this);
	}

	public long getLastUpdateTime()
	{
		return tickedGameTime;
	}

	@Override
	public CompoundTag toInitialChunkDataTag()
	{
		return toTag(new CompoundTag());
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket()
	{
		return new BlockEntityUpdateS2CPacket(pos, 1, toInitialChunkDataTag());
	}

//	@Override // Forge method
//	public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket packet)
//	{
//		fromTag(getCachedState(), packet.getCompoundTag());
//	}

	@Override
	public Owner getOwner()
	{
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name)
	{
		owner.set(uuid, name);
	}

//	@Override // Forge method
//	public void onLoad()
//	{
//		if(world.isClient)
//			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getPos()));
//	}

	//code from Forge, as it is hardcoded to the vanilla hopper
//	private boolean insertHook()
//	{
//		Direction hopperFacing = getCachedState().get(HopperBlock.FACING);
//		return getItemHandler(this, hopperFacing)
//				.map(destinationResult -> {
//					IItemHandler itemHandler = destinationResult.getKey();
//					Object destination = destinationResult.getValue();
//					if (isFull())
//					{
//						return false;
//					}
//					else
//					{
//						for (int i = 0; i < size(); ++i)
//						{
//							if (!getStack(i).isEmpty())
//							{
//								ItemStack originalSlotContents = getStack(i).copy();
//								ItemStack insertStack = removeStack(i, 1);
//								ItemStack remainder = putStackInInventoryAllSlots(this, destination, itemHandler, insertStack);
//
//								if (remainder.isEmpty())
//								{
//									return true;
//								}
//
//								setStack(i, originalSlotContents);
//							}
//						}
//
//						return false;
//					}
//				})
//				.orElse(false);
//	}
//
//	//these are private in forge's code, so it's copied here
//	private Optional<Pair<IItemHandler, Object>> getItemHandler(Hopper hopper, Direction hopperFacing)
//	{
//		double x = hopper.getHopperX() + hopperFacing.getOffsetX();
//		double y = hopper.getHopperY() + hopperFacing.getOffsetY();
//		double z = hopper.getHopperZ() + hopperFacing.getOffsetZ();
//		return VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
//	}
//
//	private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
//	{
//		for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
//		{
//			stack = insertStack(source, destination, destInventory, stack, slot);
//		}
//		return stack;
//	}
//
//	private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
//	{
//		ItemStack itemstack = destInventory.getStackInSlot(slot);
//
//		if (destInventory.insertItem(slot, stack, true).isEmpty())
//		{
//			boolean insertedItem = false;
//			boolean inventoryWasEmpty = isEmpty(destInventory);
//
//			if (itemstack.isEmpty())
//			{
//				destInventory.insertItem(slot, stack, false);
//				stack = ItemStack.EMPTY;
//				insertedItem = true;
//			}
//			else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
//			{
//				int originalSize = stack.getCount();
//				stack = destInventory.insertItem(slot, stack, false);
//				insertedItem = originalSize < stack.getCount();
//			}
//
//			if (insertedItem)
//			{
//				if (inventoryWasEmpty && destination instanceof HopperBlockEntity)
//				{
//					HopperBlockEntity destinationHopper = (HopperBlockEntity)destination;
//
//					if (!destinationHopper.isDisabled())
//					{
//						destinationHopper.setCooldown(8);
//					}
//				}
//			}
//		}
//
//		return stack;
//	}
//
//	private static boolean isEmpty(IItemHandler itemHandler)
//	{
//		for (int slot = 0; slot < itemHandler.getSlots(); slot++)
//		{
//			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
//			if (stackInSlot.getCount() > 0)
//			{
//				return false;
//			}
//		}
//		return true;
//	}
}