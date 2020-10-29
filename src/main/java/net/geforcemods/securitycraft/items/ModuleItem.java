package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IModuleInventory;
//import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
//import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class ModuleItem extends Item{

	public static final Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean nbtCanBeModified;
	private boolean canBeCustomized;
	private int numberOfItemAddons;
	private int numberOfBlockAddons;

	public ModuleItem(Settings properties, ModuleType module, boolean nbtCanBeModified){
		this(properties, module, nbtCanBeModified, false, 0, 0);
	}

	public ModuleItem(Settings properties, ModuleType module, boolean nbtCanBeModified, boolean canBeCustomized){
		this(properties, module, nbtCanBeModified, canBeCustomized, 0, 0);
	}

	public ModuleItem(Settings properties, ModuleType module, boolean nbtCanBeModified, boolean canBeCustomized, int itemAddons, int blockAddons){
		super(properties);
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		this.canBeCustomized = canBeCustomized;
		numberOfItemAddons = itemAddons;
		numberOfBlockAddons = blockAddons;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		BlockEntity te = ctx.getWorld().getBlockEntity(ctx.getBlockPos());
		ItemStack stack = ctx.getStack();

		if(te instanceof IModuleInventory)
		{
			IModuleInventory inv = (IModuleInventory)te;
			ModuleType type = ((ModuleItem)stack.getItem()).getModuleType();

			if(inv.getAcceptedModules().contains(type) && !inv.hasModule(type))
			{
				inv.insertModule(stack);
				inv.onModuleInserted(stack, type);

				if(!ctx.getPlayer().isCreative())
					stack.decrement(1);

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		try
		{
			//noinspection StatementWithEmptyBody
			if(canBeCustomized()) // TODO
			{
//				if(world.isClient && (module == ModuleType.WHITELIST || module == ModuleType.BLACKLIST))
//					SecurityCraft.proxy.displayEditModuleGui(stack);
//				else if(!world.isClient && module == ModuleType.DISGUISE)
//				{
//					NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() {
//						@Override
//						public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//						{
//							return new DisguiseModuleContainer(windowId, inv, new ModuleItemInventory(player.getStackInHand(hand)));
//						}
//
//						@Override
//						public Text getDisplayName()
//						{
//							return new TranslatableText(getTranslationKey());
//						}
//					});
//				}
			}
		}
		catch(NoSuchMethodError e) {/*:^)*/}

		return TypedActionResult.pass(stack);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext flag) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(new TranslatableText("tooltip.securitycraft:module.modifiable").setStyle(GRAY_STYLE));
		else
			list.add(new TranslatableText("tooltip.securitycraft:module.notModifiable").setStyle(GRAY_STYLE));

		if(nbtCanBeModified && stack.getTag() != null && !stack.getTag().isEmpty()) {
			list.add(LiteralText.EMPTY);
			list.add(new TranslatableText("tooltip.securitycraft:module.playerCustomization.players").setStyle(GRAY_STYLE));

			for(int i = 1; i <= MAX_PLAYERS; i++)
				if(!stack.getTag().getString("Player" + i).isEmpty())
					list.add(new LiteralText(stack.getTag().getString("Player" + i)).setStyle(GRAY_STYLE));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems", numberOfBlockAddons, numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.items", numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks", numberOfBlockAddons).setStyle(GRAY_STYLE));

			if(getNumberOfAddons() > 0 && !getAddons(stack.getTag()).isEmpty()) {
				list.add(LiteralText.EMPTY);

				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.added").setStyle(GRAY_STYLE));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new LiteralText("- ").append(ClientUtils.localize(addon.getTranslationKey())).setStyle(GRAY_STYLE));
			}
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public int getNumberOfAddons(){
		return numberOfItemAddons + numberOfBlockAddons;
	}

	public int getNumberOfItemAddons(){
		return numberOfItemAddons;
	}

	public int getNumberOfBlockAddons(){
		return numberOfBlockAddons;
	}

	public ArrayList<Block> getBlockAddons(CompoundTag tag){
		ArrayList<Block> list = new ArrayList<>();

		if(tag == null) return list;

		ListTag items = tag.getList("ItemInventory", NbtType.COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons) {
				ItemStack stack;

				if((stack = ItemStack.fromTag(item)).getItem() instanceof BlockItem)
					list.add(Block.getBlockFromItem(stack.getItem()));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(CompoundTag tag){
		ArrayList<ItemStack> list = new ArrayList<>();

		if(tag == null) return list;

		ListTag items = tag.getList("ItemInventory", NbtType.COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons)
				list.add(ItemStack.fromTag(item));
		}

		return list;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
