package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class BriefcaseContainer extends ScreenHandler {

	public BriefcaseContainer(int windowId, PlayerInventory playerInventory, BriefcaseInventory briefcaseInventory) {
		super(SCContent.cTypeBriefcaseInventory, windowId);

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 4; j++)
				addSlot(new ItemRestrictedSlot(briefcaseInventory, j + (i * 4), 53 + (j * 18), 17 + (i * 18), SCContent.BRIEFCASE));

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

			if(index < BriefcaseInventory.SIZE) {
				if(!insertItem(slotStack, BriefcaseInventory.SIZE, 48, true))
					return ItemStack.EMPTY;

				slot.onStackChanged(slotStack, slotStackCopy);
			}
			else if(index >= BriefcaseInventory.SIZE)
				if(!insertItem(slotStack, 0, BriefcaseInventory.SIZE, false))
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
	public ItemStack onSlotClick(int slot, int dragType, SlotActionType clickType, PlayerEntity player) {
		if(slot >= 0 && getSlot(slot) != null && ((!player.getMainHandStack().isEmpty() && getSlot(slot).getStack() == player.getMainHandStack()) || (!player.getOffHandStack().isEmpty() && getSlot(slot).getStack() == player.getOffHandStack())))
			return ItemStack.EMPTY;

		return super.onSlotClick(slot, dragType, clickType, player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

}
