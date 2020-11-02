package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryScannerContainer extends ScreenHandler {

	private final int numRows;
	public final InventoryScannerTileEntity te;

	public InventoryScannerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory){
		super(SCContent.cTypeInventoryScanner, windowId);
		te = (InventoryScannerTileEntity)world.getBlockEntity(pos);
		numRows = te.size() / 9;

		for(int i = 0; i < 10; i++)
			addSlot(new OwnerRestrictedSlot(te, te, i, (4 + (i * 17)), 16, true));

		if(te.getOwner().isOwner(inventory.player) && te.hasModule(ModuleType.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlot(new Slot(te, 10 + ((i * 3) + j), 177 + (j * 18), 17 + i * 18));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferSlot(PlayerEntity player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasStack())
		{
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < numRows * 9)
			{
				if (!insertItem(slotStack, numRows * 9, slots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!insertItem(slotStack, 0, numRows * 9, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
		}

		return slotStackCopy;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void close(PlayerEntity player)
	{
		super.close(player);

		Utils.setISinTEAppropriately(player.world, te.getPos(), ((InventoryScannerTileEntity) player.world.getBlockEntity(te.getPos())).getContents());
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack onSlotClick(int slotId, int dragType, SlotActionType clickType, PlayerEntity player)
	{
		if(slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot && ((OwnerRestrictedSlot)getSlot(slotId)).isGhostSlot())
		{
			if(te.getOwner().isOwner(player))
			{
				ItemStack pickedUpStack = player.inventory.getCursorStack().copy();

				pickedUpStack.setCount(1);
				te.getContents().set(slotId, pickedUpStack);
			}

			return ItemStack.EMPTY;
		}
		else return super.onSlotClick(slotId, dragType, clickType, player);
	}
}
