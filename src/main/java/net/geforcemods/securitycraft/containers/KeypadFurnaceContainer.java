package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadFurnaceContainer extends AbstractFurnaceScreenHandler{

	public KeypadFurnaceTileEntity te;

	public KeypadFurnaceContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, world, pos, inventory, (KeypadFurnaceTileEntity)world.getBlockEntity(pos), ((KeypadFurnaceTileEntity)world.getBlockEntity(pos)).getFurnaceData());
	}

	public KeypadFurnaceContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory, Inventory furnaceInv, PropertyDelegate furnaceData) {
		super(SCContent.cTypeKeypadFurnace, RecipeType.SMELTING, RecipeBookCategory.FURNACE, windowId, inventory, furnaceInv, furnaceData);
		this.te = (KeypadFurnaceTileEntity)world.getBlockEntity(pos);
	}

	@Override
	public boolean canUse(PlayerEntity player){
		return true;
	}

	@Override
	public void close(PlayerEntity player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getCachedState().with(KeypadFurnaceBlock.OPEN, false));
	}
}