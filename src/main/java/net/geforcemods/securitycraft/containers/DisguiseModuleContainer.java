package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class DisguiseModuleContainer extends ScreenHandler {

	private ModuleItemInventory inventory;

	public DisguiseModuleContainer(int windowId, PlayerInventory playerInventory, ModuleItemInventory moduleInventory) {
		super(SCContent.cTypeDisguiseModule, windowId);
		inventory = moduleInventory;
		addSlot(new AddonSlot(inventory, 0, 79, 20));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if(index < inventory.SIZE) {
				if(!insertItem(slotStack, inventory.SIZE, 37, true))
					return ItemStack.EMPTY;

				slot.onStackChanged(slotStack, slotStackCopy);
			}
			else if(index >= inventory.SIZE)
				if(!insertItem(slotStack, 0, inventory.SIZE, false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();

			if(slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTakeItem(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack onSlotClick(int slot, int dragType, SlotActionType clickType, PlayerEntity player)
	{
		if(slot >= 0 && getSlot(slot) != null && ((!player.getMainHandStack().isEmpty() && getSlot(slot).getStack() == player.getMainHandStack()) || (!player.getOffHandStack().isEmpty() && getSlot(slot).getStack() == player.getOffHandStack())))
			return ItemStack.EMPTY;

		return super.onSlotClick(slot, dragType, clickType, player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}


	public static class AddonSlot extends Slot {

		private ModuleItemInventory inventory;

		public AddonSlot(ModuleItemInventory inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
			this.inventory = inventory;
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			int numberOfItems = 0;
			int numberOfBlocks = 0;
			boolean isStackBlock = itemStack.getItem() instanceof BlockItem;

			for(ItemStack stack : inventory.moduleInventory)
				if(!stack.isEmpty() && stack.getItem() != null)
					if(stack.getItem() instanceof BlockItem)
						numberOfBlocks++;
					else
						numberOfItems++;

			return (isStackBlock && numberOfBlocks < inventory.maxNumberOfBlocks) || (!isStackBlock && numberOfItems < inventory.maxNumberOfItems);
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}
	}

}
