package net.geforcemods.securitycraft.tileentity;

import net.fabricmc.fabric.api.util.NbtType;
import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
//import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
//import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
//import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.util.Constants;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.fml.network.NetworkHooks;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.wrapper.EmptyHandler;

public class KeypadFurnaceTileEntity extends AbstractFurnaceBlockEntity implements IPasswordProtected, NamedScreenHandlerFactory, IOwnable, INameable, IModuleInventory, ICustomizable
{
//	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler()); // TODO
//	private LazyOptional<IItemHandler> insertOnlyHandler;
	private Owner owner = new Owner();
	private String passcode;
	private Text furnaceCustomName;
	private DefaultedList<ItemStack> modules = DefaultedList.<ItemStack>ofSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadFurnaceTileEntity()
	{
		super(SCContent.teTypeKeypadFurnace, RecipeType.SMELTING);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if(tag.contains("CustomName", NbtType.STRING))
			furnaceCustomName = new LiteralText(tag.getString("CustomName"));

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if(tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if(tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));

		if(tag.contains("passcode"))
			passcode = tag.getString("passcode");

		if(hasCustomSCName())
			tag.putString("CustomName", furnaceCustomName.getString());
	}

	@Override
	public CompoundTag toInitialChunkDataTag()
	{
		return toTag(new CompoundTag());
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket()
	{
		return new BlockEntityUpdateS2CPacket(pos, 1, toInitialChunkDataTag());
	}

//	@Override // Forge method
//	public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket packet)
//	{
//		fromTag(getCachedState(), packet.getCompoundTag());
//	}

	@Override
	public Owner getOwner()
	{
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name)
	{
		owner.set(uuid, name);
	}

//	@Override // Forge method
//	public void onLoad()
//	{
//		if(world.isClient)
//			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(pos));
//	}
//
//	@Override // Forge method
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
//	{
//		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
//		{
//			BlockPos offsetPos = pos.offset(side);
//			BlockState offsetState = world.getBlockState(offsetPos);
//
//			for(IExtractionBlock extractionBlock : SecurityCraft.getRegisteredExtractionBlocks())
//			{
//				if(offsetState.getBlock() == extractionBlock.getBlock())
//				{
//					if(!extractionBlock.canExtract(this, world, offsetPos, offsetState))
//						return EMPTY_INVENTORY.cast();
//					else return super.getCapability(cap, side);
//				}
//			}
//
//			return getInsertOnlyHandler().cast();
//		}
//		else return super.getCapability(cap, side);
//	}
//
//	private LazyOptional<IItemHandler> getInsertOnlyHandler()
//	{
//		if(insertOnlyHandler == null)
//			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadFurnaceTileEntity.this));
//
//		return insertOnlyHandler;
//	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getStack(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : inventory.get(slot);
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isClient && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeypadFurnaceBlock)
			KeypadFurnaceBlock.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayerEntity)
			{
//				NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//					@Override
//					public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//					{
//						return new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, world, pos);
//					}
//
//					@Override
//					public Text getDisplayName()
//					{
//						return new TranslatableText(SCContent.KEYPAD_FURNACE.getTranslationKey());
//					}
//				}, pos);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayerEntity)
				{
//					NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//						@Override
//						public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//						{
//							return new GenericTEContainer(SCContent.cTypeSetPassword, windowId, world, pos);
//						}
//
//						@Override
//						public Text getDisplayName()
//						{
//							return new TranslatableText(SCContent.KEYPAD_FURNACE.getTranslationKey());
//						}
//					}, pos);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new LiteralText("SecurityCraft"), ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), Formatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_FURNACE.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), Formatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	public PropertyDelegate getFurnaceData()
	{
		return propertyDelegate;
	}

	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new KeypadFurnaceContainer(windowId, world, pos, inv, this, propertyDelegate);
	}

	@Override
	protected ScreenHandler createScreenHandler(int windowId, PlayerInventory inv)
	{
		return createMenu(windowId, inv, inv.player);
	}

	@Override
	public Text getDisplayName()
	{
		return hasCustomSCName() ? getCustomSCName() : getContainerName();
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText(SCContent.KEYPAD_FURNACE.getTranslationKey());
	}

	@Override
	public Text getCustomSCName()
	{
		return furnaceCustomName;
	}

	@Override
	public void setCustomSCName(Text customName)
	{
		furnaceCustomName = customName;
	}

	@Override
	public boolean hasCustomSCName()
	{
		return furnaceCustomName != null && furnaceCustomName.getString() != null && !furnaceCustomName.getString().isEmpty();
	}

	@Override
	public boolean canBeNamed()
	{
		return true;
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	@Override
	public DefaultedList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.WHITELIST, ModuleType.BLACKLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{sendMessage};
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}
}
