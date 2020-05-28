package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class CustomizeBlockContainer extends Container{

	public IModuleInventory moduleInv;
	private final int maxSlots;

	public CustomizeBlockContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeCustomizeBlock, windowId);
		this.moduleInv = (IModuleInventory)world.getTileEntity(pos);

		int slotId = 0;

		if(moduleInv.enableHack())
			slotId = 100;

		if(moduleInv.getMaxNumberOfModules() == 1)
			addSlot(new SlotItemHandler(moduleInv, slotId, 79, 20));
		else if(moduleInv.getMaxNumberOfModules() == 2){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 88, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 3){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 61, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 79, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 97, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 4){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 106, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 5){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 34, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 106, 20));
		}

		maxSlots = 36 + moduleInv.getMaxNumberOfModules();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			boolean isModule = slotStack.getItem() instanceof ModuleItem;

			copy = slotStack.copy();

			if(index >= 36 && index <= maxSlots) //module slots
			{
				if(!mergeItemStack(slotStack, 0, 36, false)) //main inventory + hotbar
				{
					moduleInv.onModuleRemoved(slotStack, ((ModuleItem)slotStack.getItem()).getModule());

					if(moduleInv instanceof CustomizableTileEntity)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, slotStack, (CustomizableTileEntity)moduleInv);

					return ItemStack.EMPTY;
				}
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
				{
					moduleInv.onModuleInserted(slotStack, ((ModuleItem)slotStack.getItem()).getModule());

					if(moduleInv instanceof CustomizableTileEntity)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, slotStack, (CustomizableTileEntity)moduleInv);

					return ItemStack.EMPTY;
				}
				else if(!mergeItemStack(slotStack, 0, 27, false)) //main inventory
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
				{
					moduleInv.onModuleInserted(slotStack, ((ModuleItem)slotStack.getItem()).getModule());

					if(moduleInv instanceof CustomizableTileEntity)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, slotStack, (CustomizableTileEntity)moduleInv);

					return ItemStack.EMPTY;
				}
				else if(!mergeItemStack(slotStack, 27, 36, false)) //hotbar
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
}
