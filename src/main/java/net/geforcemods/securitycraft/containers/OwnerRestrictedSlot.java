package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.compat.fabric.FabricSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class OwnerRestrictedSlot extends FabricSlot {

	private final IOwnable tileEntity;
	private final boolean isGhostSlot;

	public OwnerRestrictedSlot(Inventory inventory, IOwnable tileEntity, int index, int xPos, int yPos, boolean ghostSlot) {
		super(inventory, index, xPos, yPos);
		this.tileEntity = tileEntity;
		isGhostSlot = ghostSlot;
	}

	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	@Override
	public boolean canTakeItems(PlayerEntity player){
		return tileEntity.getOwner().isOwner(player) && !isGhostSlot; //the !isGhostSlot check helps to prevent double clicking a stack to pull all items towards the stack
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return !isGhostSlot; //prevents shift clicking into ghost slot
	}

	@Override
	public void setStack(ItemStack stack)
	{
		if(canInsert(stack))
		{
			super.setStack(stack);
		}
	}

	@Override
	public int getMaxItemCount(){
		return 1;
	}

	public boolean isGhostSlot()
	{
		return isGhostSlot;
	}
}
