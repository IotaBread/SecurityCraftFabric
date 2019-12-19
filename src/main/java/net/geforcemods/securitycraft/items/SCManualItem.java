package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SCManualItem extends Item {

	public SCManualItem(){
		super(new Item.Properties().maxStackSize(1).group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(world.isRemote)
			SecurityCraft.proxy.displaySCManualGui();

		return ActionResult.func_226250_c_(player.getHeldItem(hand)); //pass
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(par1ItemStack.getTag() == null){
			ListNBT bookPages = new ListNBT();

			par1ItemStack.setTagInfo("pages", bookPages);
			par1ItemStack.setTagInfo("author", StringNBT.func_229705_a_("Geforce")); //create
			par1ItemStack.setTagInfo("title", StringNBT.func_229705_a_("SecurityCraft"));
		}
	}

}
