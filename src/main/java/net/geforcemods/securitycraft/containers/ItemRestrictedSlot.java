package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.compat.fabric.FabricSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRestrictedSlot extends FabricSlot {

	private final Inventory inventory;
	private final Item[] prohibitedItems;

	public ItemRestrictedSlot(Inventory inventory, int index, int xPos, int yPos, Item... prohibitedItems) {
		super(inventory, index, xPos, yPos);
		this.inventory = inventory;
		this.prohibitedItems = prohibitedItems;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		if(stack.getItem() == null) return false;

		// Only allows items not in prohibitedItems[] to be placed in the slot.
		for(Item prohibitedItem : prohibitedItems)
			if(stack.getItem() == prohibitedItem)
				return false;

		return true;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.inventory.setStack(getSlotIndex(), stack);
		this.markDirty();
	}
}
