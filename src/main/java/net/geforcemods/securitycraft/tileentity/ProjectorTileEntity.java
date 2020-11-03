package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
//import net.minecraft.util.math.Box;

public class ProjectorTileEntity extends DisguisableTileEntity implements Inventory, NamedScreenHandlerFactory {

	public static final int MIN_WIDTH = 1;
	public static final int MAX_WIDTH = 10;
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;

	public static final int RENDER_DISTANCE = 100;

	private int projectionWidth = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;
	public boolean activatedByRedstone = false;
	public boolean active = false;

	private ItemStack projectedBlock = ItemStack.EMPTY;

	public ProjectorTileEntity()
	{
		super(SCContent.teTypeProjector);
	}

//	@Override // Forge method
//	public Box getRenderBoundingBox() {
//		return new Box(getPos()).expand(RENDER_DISTANCE);
//	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		tag.putInt("width", projectionWidth);
		tag.putInt("range", projectionRange);
		tag.putInt("offset", projectionOffset);
		activatedByRedstone = hasModule(ModuleType.REDSTONE);
		tag.putBoolean("active", active);

		if(!isEmpty())
		{
			CompoundTag itemTag = new CompoundTag();
			projectedBlock.toTag(itemTag);
			tag.put("storedItem", itemTag);
		}

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if(tag.contains("width"))
			projectionWidth = tag.getInt("width");

		if(tag.contains("range"))
			projectionRange = tag.getInt("range");

		if(tag.contains("offset"))
			projectionOffset = tag.getInt("offset");

		activatedByRedstone = hasModule(ModuleType.REDSTONE);

		if(tag.contains("active"))
			active = tag.getBoolean("active");

		if(tag.contains("storedItem"))
			projectedBlock = ItemStack.fromTag(tag.getCompound("storedItem"));
	}

	public int getProjectionWidth()
	{
		return projectionWidth;
	}

	public void setProjectionWidth(int width)
	{
		projectionWidth = width;
	}

	public int getProjectionRange()
	{
		return projectionRange;
	}

	public void setProjectionRange(int range)
	{
		projectionRange = range;
	}

	public int getProjectionOffset()
	{
		return projectionOffset;
	}

	public void setProjectionOffset(int offset)
	{
		projectionOffset = offset;
	}

	public boolean isActivatedByRedstone()
	{
		return activatedByRedstone;
	}

	public void setActivatedByRedstone(boolean redstone)
	{
		activatedByRedstone = redstone;
	}

	public boolean isActive()
	{
		return activatedByRedstone ? active : true;
	}

	public void setActive(boolean isOn)
	{
		active = isOn;
	}

	public Block getProjectedBlock() {
		return Block.getBlockFromItem(projectedBlock.getItem());
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(module == ModuleType.REDSTONE)
			setActivatedByRedstone(true);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.REDSTONE)
			setActivatedByRedstone(false);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE, ModuleType.REDSTONE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new ProjectorContainer(windowId, world, pos, inv);
	}

	@Override
	public Text getDisplayName()
	{
		// return new TranslationTextComponent(SCContent.PROJECTOR.get().getTranslationKey());
		return new TranslatableText("Projector");
	}

	@Override
	public void clear()
	{
		projectedBlock = ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int index, int count)
	{
		ItemStack stack = projectedBlock;

		if(count >= 1)
			projectedBlock = ItemStack.EMPTY;

		return stack;
	}

	@Override
	public int size()
	{
		return ProjectorContainer.SIZE;
	}

	@Override
	public int getMaxCountPerStack()
	{
		return 1;
	}

	@Override
	public ItemStack getStack(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? projectedBlock : ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty()
	{
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity arg0)
	{
		return true;
	}

	@Override
	public ItemStack removeStack(int index)
	{
		ItemStack stack = projectedBlock;
		projectedBlock = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setStack(int index, ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getCount() > getMaxCountPerStack())
			stack = new ItemStack(stack.getItem(), getMaxCountPerStack());

		projectedBlock = stack;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}
}
