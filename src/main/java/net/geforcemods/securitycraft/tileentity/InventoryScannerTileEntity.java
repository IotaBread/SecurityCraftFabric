package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.wrapper.EmptyHandler;

public class InventoryScannerTileEntity extends DisguisableTileEntity implements Inventory, NamedScreenHandlerFactory{

	private BooleanOption horizontal = new BooleanOption("horizontal", false);
//	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler()); // TODO
	private DefaultedList<ItemStack> inventoryContents = DefaultedList.<ItemStack>ofSize(37, ItemStack.EMPTY);
	private boolean isProvidingPower;
	private int cooldown;

	public InventoryScannerTileEntity()
	{
		super(SCContent.teTypeInventoryScanner);
	}

	@Override
	public void tick(){
		if(cooldown > 0)
			cooldown--;
		else if(isProvidingPower){
			isProvidingPower = false;
			BlockUtils.updateAndNotify(getWorld(), pos, getWorld().getBlockState(pos).getBlock(), 1, true);
		}
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag){
		super.fromTag(state, tag);

		ListTag list = tag.getList("Items", 10);
		inventoryContents = DefaultedList.<ItemStack>ofSize(size(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i)
		{
			CompoundTag stackTag = list.getCompound(i);
			int slot = stackTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < inventoryContents.size())
				inventoryContents.set(slot, ItemStack.fromTag(stackTag));
		}

		if(tag.contains("cooldown"))
			cooldown = tag.getInt("cooldown");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag){
		super.toTag(tag);

		ListTag list = new ListTag();

		for (int i = 0; i < inventoryContents.size(); ++i)
			if (!inventoryContents.get(i).isEmpty())
			{
				CompoundTag stackTag = new CompoundTag();
				stackTag.putByte("Slot", (byte)i);
				inventoryContents.get(i).toTag(stackTag);
				list.add(stackTag);
			}

		tag.put("Items", list);
		tag.putInt("cooldown", cooldown);
		return tag;
	}

	@Override
	public int size() {
		return 37;
	}

	@Override
	public ItemStack removeStack(int index, int count)
	{
		if (!inventoryContents.get(index).isEmpty())
		{
			ItemStack stack;

			if (inventoryContents.get(index).getCount() <= count)
			{
				stack = inventoryContents.get(index);
				inventoryContents.set(index, ItemStack.EMPTY);
				markDirty();
				return stack;
			}
			else
			{
				stack = inventoryContents.get(index).split(count);

				if (inventoryContents.get(index).getCount() == 0)
					inventoryContents.set(index, ItemStack.EMPTY);

				markDirty();
				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getStack(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : inventoryContents.get(slot);
	}

	/**
	 * Copy of getStackInSlot which doesn't get overrided by CustomizableSCTE.
	 */

	public ItemStack getStackInSlotCopy(int index) {
		return inventoryContents.get(index);
	}

	@Override
	public void setStack(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());

		markDirty();
	}

	/**
	 * Adds the given stack to the inventory. Will void any excess.
	 * @param stack The stack to add
	 */
	public void addItemToStorage(ItemStack stack)
	{
		ItemStack remainder = stack;

		for(int i = 10; i < getContents().size(); i++)
		{
			remainder = insertItem(i, remainder);

			if(remainder.isEmpty())
				break;
		}
	}

	public ItemStack insertItem(int slot, ItemStack stackToInsert)
	{
		if(stackToInsert.isEmpty() || slot < 0 || slot >= getContents().size())
			return stackToInsert;

		ItemStack slotStack = getStack(slot);
		int limit = stackToInsert.getItem().getMaxCount();

		if(slotStack.isEmpty())
		{
			setStack(slot, stackToInsert);
			return ItemStack.EMPTY;
		}
		else if(InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit)
		{
			if(limit - slotStack.getCount() >= stackToInsert.getCount())
			{
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				return ItemStack.EMPTY;
			}
			else
			{
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.split((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				return toReturn;
			}
		}

		return stackToInsert;
	}

//	@Override // Forge method
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
//	{
//		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
//			return EMPTY_INVENTORY.cast();
//		else return super.getCapability(cap, side);
//	}

	@Override
	public boolean hasCustomSCName() {
		return true;
	}

	@Override
	public int getMaxCountPerStack() {
		return 64;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onOpen(PlayerEntity player) {}

	@Override
	public void onClose(PlayerEntity player) {}

	@Override
	public boolean isValid(int var1, ItemStack var2) {
		return true;
	}

	public boolean shouldProvidePower() {
		return hasModule(ModuleType.REDSTONE) && isProvidingPower;
	}

	public void setShouldProvidePower(boolean isProvidingPower) {
		this.isProvidingPower = isProvidingPower;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public DefaultedList<ItemStack> getContents(){
		return inventoryContents;
	}

	public void setContents(DefaultedList<ItemStack> contents){
		inventoryContents = contents;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner != null && !connectedScanner.hasModule(module))
			connectedScanner.insertModule(stack);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner != null && connectedScanner.hasModule(module))
			connectedScanner.removeModule(module);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST, ModuleType.SMART, ModuleType.STORAGE, ModuleType.DISGUISE, ModuleType.REDSTONE};
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(!option.getName().equals("horizontal"))
			return;

		BooleanOption bo = (BooleanOption)option;

		InventoryScannerTileEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

		if(connectedScanner != null)
		{
			Direction facing = getCachedState().get(InventoryScannerBlock.FACING);

			for(int i = 0; i <= ConfigHandler.CONFIG.inventoryScannerRange; i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				BlockState state = world.getBlockState(offsetPos);
				Block block = state.getBlock();

				if(block == SCContent.INVENTORY_SCANNER_FIELD)
					world.setBlockState(offsetPos, state.with(InventoryScannerFieldBlock.HORIZONTAL, bo.get()));
				else if(!state.isAir() && block != SCContent.INVENTORY_SCANNER_FIELD && block != SCContent.INVENTORY_SCANNER)
					break;
				else if(block == SCContent.INVENTORY_SCANNER && state.get(InventoryScannerBlock.FACING) == facing.getOpposite())
					break;
			}

			connectedScanner.setHorizontal(bo.get());
		}

		world.setBlockState(pos, getCachedState().with(InventoryScannerBlock.HORIZONTAL, bo.get()));
	}

	public void setHorizontal(boolean isHorizontal)
	{
		horizontal.setValue(isHorizontal);
		world.setBlockState(pos, getCachedState().with(InventoryScannerBlock.HORIZONTAL, isHorizontal));
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[] {horizontal};
	}

	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new InventoryScannerContainer(windowId, world, pos, inv);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(SCContent.INVENTORY_SCANNER.getTranslationKey());
	}

	@Override
	public void clear()
	{
		inventoryContents.clear();
	}

	@Override
	public boolean isEmpty()
	{
		return inventoryContents.isEmpty();
	}

	@Override
	public ItemStack removeStack(int index)
	{
		return inventoryContents.remove(index);
	}
}
