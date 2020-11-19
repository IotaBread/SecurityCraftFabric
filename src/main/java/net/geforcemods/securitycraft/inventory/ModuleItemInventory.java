package net.geforcemods.securitycraft.inventory;

import net.fabricmc.fabric.api.util.NbtType;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ModuleItem;
//import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;
//import net.minecraftforge.common.util.Constants;
//import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.fml.common.thread.EffectiveSide;

public class ModuleItemInventory implements Inventory {

	public int SIZE = 0;
	private final ItemStack module;

	public DefaultedList<ItemStack> moduleInventory;
	public int maxNumberOfItems;
	public int maxNumberOfBlocks;

	public ModuleItemInventory(ItemStack moduleItem) {
		module = moduleItem;

		if(!(moduleItem.getItem() instanceof ModuleItem)) return;

		SIZE = ((ModuleItem) moduleItem.getItem()).getNumberOfAddons();
		maxNumberOfItems = ((ModuleItem) moduleItem.getItem()).getNumberOfItemAddons();
		maxNumberOfBlocks = ((ModuleItem) moduleItem.getItem()).getNumberOfBlockAddons();
		moduleInventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

		if (!module.hasTag())
			module.setTag(new CompoundTag());

		readFromNBT(module.getTag());
	}

	@Override
	public int size() {
		return SIZE;
	}

	@Override
	public ItemStack getStack(int index) {
		return moduleInventory.get(index);
	}

	public void readFromNBT(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", NbtType.COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < size())
				moduleInventory.set(slot, ItemStack.fromTag(item));
		}
	}

	public void writeToNBT(CompoundTag tag) {
		ListTag items = new ListTag();

		for(int i = 0; i < size(); i++)
			if(!getStack(i).isEmpty()) {
				CompoundTag item = new CompoundTag();
				item.putInt("Slot", i);
				getStack(i).toTag(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);

//		if(EffectiveSide.get() == LogicalSide.CLIENT) // TODO
//			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));
	}

	@Override
	public ItemStack removeStack(int index, int size) {
		ItemStack stack = getStack(index);

		if(!stack.isEmpty())
			if(stack.getCount() > size) {
				stack = stack.split(size);
				markDirty();
			}
			else
				setStack(index, ItemStack.EMPTY);

		return stack;
	}

	@Override
	public ItemStack removeStack(int index) {
		ItemStack stack = getStack(index);
		setStack(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setStack(int index, ItemStack stack) {
		moduleInventory.set(index, stack);

		if(!stack.isEmpty() && stack.getCount() > getMaxCountPerStack())
			stack.setCount(getMaxCountPerStack());

		markDirty();
	}

	@Override
	public int getMaxCountPerStack() {
		return 64;
	}

	@Override
	public void markDirty() {
		for(int i = 0; i < size(); i++)
			if(!getStack(i).isEmpty() && getStack(i).getCount() == 0)
				moduleInventory.set(i, ItemStack.EMPTY);

		writeToNBT(module.getTag());
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
	public boolean isValid(int index, ItemStack stack) {
		return true;
	}

	@Override
	public void clear() {}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : moduleInventory)
			if(!stack.isEmpty())
				return false;

		return true;
	}
}
