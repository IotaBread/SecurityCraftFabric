package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlockReinforcerContainer extends ScreenHandler
{
	private final ItemStack blockReinforcer;
	private final SimpleInventory itemInventory = new SimpleInventory(2);
	public final SlotBlockReinforcer reinforcingSlot;
	public final SlotBlockReinforcer unreinforcingSlot;
	public final boolean isLvl1;

	public BlockReinforcerContainer(int windowId, PlayerInventory inventory, boolean isLvl1)
	{
		super(SCContent.cTypeBlockReinforcer, windowId);

		blockReinforcer = inventory.getMainHandStack();
		this.isLvl1 = isLvl1;
		addSlot(reinforcingSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20, true));

		if(!isLvl1)
			addSlot(unreinforcingSlot = new SlotBlockReinforcer(itemInventory, 1, 26, 45, false));
		else
			unreinforcingSlot = null;

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return true;
	}

	@Override
	public void close(PlayerEntity player)
	{
		if(!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected())
		{
			for(int slot = 0; slot < itemInventory.size(); ++slot)
			{
				player.dropItem(itemInventory.removeStack(slot), false);
			}

			return;
		}

		if(!itemInventory.getStack(0).isEmpty())
		{
			player.dropItem(reinforcingSlot.output, false);
			blockReinforcer.damage(reinforcingSlot.output.getCount(), player, p -> p.sendToolBreakStatus(p.getActiveHand()));
		}

		if(!isLvl1 && !itemInventory.getStack(1).isEmpty())
		{
			player.dropItem(unreinforcingSlot.output, false);
			blockReinforcer.damage(unreinforcingSlot.output.getCount(), player, p -> p.sendToolBreakStatus(p.getActiveHand()));
		}
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if(slot != null && slot.hasStack())
		{
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if(id <= fixSlot(1))
			{
				if(!insertItem(slotStack, fixSlot(1), fixSlot(38), true))
					return ItemStack.EMPTY;
				slot.onStackChanged(slotStack, slotStackCopy);
			}
			else if(id > 1)
				if(!insertItem(slotStack, 0, fixSlot(2), false))
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

	private int fixSlot(int slot)
	{
		return isLvl1 ? slot - 1 : slot;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean merged = false;
		int currentIndex = startIndex;

		if(useEndIndex)
			currentIndex = endIndex - 1;

		Slot slot;
		ItemStack slotStack;

		if(stack.isStackable())
			while(stack.getCount() > 0 && (!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex))
			{
				slot = slots.get(currentIndex);
				slotStack = slot.getStack();

				if(!slotStack.isEmpty() && canStacksCombine(stack, slotStack) && slot.canInsert(stack))
				{
					int combinedCount = slotStack.getCount() + stack.getCount();

					if(combinedCount <= stack.getMaxCount())
					{
						stack.setCount(0);
						slotStack.setCount(combinedCount);
						slot.markDirty();
						merged = true;
					}
					else if(slotStack.getCount() < stack.getMaxCount())
					{
						stack.decrement(stack.getMaxCount() - slotStack.getCount());
						slotStack.setCount(stack.getMaxCount());
						slot.markDirty();
						merged = true;
					}
				}

				if(useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}

		if(stack.getCount() > 0)
		{
			if(useEndIndex)
				currentIndex = endIndex - 1;
			else
				currentIndex = startIndex;

			while(!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex)
			{
				slot = slots.get(currentIndex);
				slotStack = slot.getStack();

				if(slotStack.isEmpty() && slot.canInsert(stack))
				{
					slot.setStack(stack.copy());
					slot.markDirty();
					stack.setCount(0);
					merged = true;
					break;
				}

				if(useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}
		}

		return merged;
	}

	public class SlotBlockReinforcer extends Slot
	{
		private final boolean reinforce;
		private ItemStack output = ItemStack.EMPTY;

		public SlotBlockReinforcer(Inventory inventory, int index, int x, int y, boolean reinforce)
		{
			super(inventory, index, x, y);

			this.reinforce = reinforce;
		}

		@Override
		public boolean canInsert(ItemStack stack)
		{
			//can only reinforce OR unreinforce at once
			if(!itemInventory.getStack((id + 1) % 2).isEmpty())
				return false;

			return (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).containsKey(Block.getBlockFromItem(stack.getItem())) &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getDamage() >= stack.getCount() + (hasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}

		@Override
		public void markDirty()
		{
			ItemStack stack = itemInventory.getStack(id);

			if(!stack.isEmpty())
			{
				Block block = (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).get(Block.getBlockFromItem(stack.getItem()));

				if(block != null)
				{
					output = new ItemStack(block);
					output.setCount(stack.getCount());
				}
			}
		}

		public ItemStack getOutput()
		{
			return output;
		}
	}
}
