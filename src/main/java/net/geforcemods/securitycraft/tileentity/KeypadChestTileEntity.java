package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
//import net.geforcemods.securitycraft.containers.GenericTEContainer;
//import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
//import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.fml.network.NetworkHooks;
//import net.minecraftforge.items.CapabilityItemHandler;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.wrapper.EmptyHandler;

public class KeypadChestTileEntity extends ChestBlockEntity implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable {

//	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler()); // TODO
//	private LazyOptional<IItemHandler> insertOnlyHandler;
	private String passcode;
	private Owner owner = new Owner();
	private DefaultedList<ItemStack> modules = DefaultedList.<ItemStack>ofSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadChestTileEntity()
	{
		super(SCContent.teTypeKeypadChest);
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if(owner != null){
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if (tag.contains("passcode"))
			if(tag.getInt("passcode") != 0)
				passcode = String.valueOf(tag.getInt("passcode"));
			else
				passcode = tag.getString("passcode");

		if (tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		toTag(tag);
		return new BlockEntityUpdateS2CPacket(pos, 1, tag);
	}

//	@Override // Forge method
//	public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket packet) {
//		fromTag(getCachedState(), packet.getCompoundTag());
//	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public Text getName()
	{
		return new LiteralText("Protected chest");
	}

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
//			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadChestTileEntity.this));
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
		return slot >= 100 ? getModuleInSlot(slot) : super.getStack(slot);
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isClient && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeypadChestBlock && !isBlocked())
			KeypadChestBlock.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(isBlocked())
			return;

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
//						return new TranslatableText(SCContent.KEYPAD_CHEST.getTranslationKey());
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
//							return new TranslatableText(SCContent.KEYPAD_CHEST.getTranslationKey());
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
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_CHEST.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), Formatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleInserted(stack, module);

		addOrRemoveModuleFromAttached(stack, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleRemoved(stack, module);

		addOrRemoveModuleFromAttached(stack, true);
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(option instanceof BooleanOption)
		{
			KeypadChestTileEntity offsetTe = findOther();

			if(offsetTe != null)
				offsetTe.setSendsMessages(((BooleanOption)option).get());
		}
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove)
	{
		if(module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		KeypadChestTileEntity offsetTe = findOther();

		if(offsetTe != null)
		{
			if(remove)
				offsetTe.removeModule(((ModuleItem)module.getItem()).getModuleType());
			else
				offsetTe.insertModule(module);
		}
	}

	public KeypadChestTileEntity findOther()
	{
		BlockState state = getCachedState();
		ChestType type = state.get(KeypadChestBlock.CHEST_TYPE);

		if(type != ChestType.SINGLE)
		{
			BlockPos offsetPos = pos.offset(ChestBlock.getFacing(state));
			BlockState offsetState = world.getBlockState(offsetPos);

			if(state.getBlock() == offsetState.getBlock())
			{
				ChestType offsetType = offsetState.get(KeypadChestBlock.CHEST_TYPE);

				if(offsetType != ChestType.SINGLE && type != offsetType && state.get(KeypadChestBlock.FACING) == offsetState.get(KeypadChestBlock.FACING))
				{
					BlockEntity offsetTe = world.getBlockEntity(offsetPos);

					if(offsetTe instanceof KeypadChestTileEntity)
						return (KeypadChestTileEntity)offsetTe;
				}
			}
		}

		return null;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public boolean isBlocked()
	{
		Block east = BlockUtils.getBlock(getWorld(), getPos().east());
		Block south = BlockUtils.getBlock(getWorld(), getPos().south());
		Block west = BlockUtils.getBlock(getWorld(), getPos().west());
		Block north = BlockUtils.getBlock(getWorld(), getPos().north());

		if(east instanceof KeypadChestBlock && KeypadChestBlock.isChestBlocked(getWorld(), getPos().east()))
			return true;
		else if(south instanceof KeypadChestBlock && KeypadChestBlock.isChestBlocked(getWorld(), getPos().south()))
			return true;
		else if(west instanceof KeypadChestBlock && KeypadChestBlock.isChestBlocked(getWorld(), getPos().west()))
			return true;
		else if(north instanceof KeypadChestBlock && KeypadChestBlock.isChestBlocked(getWorld(), getPos().north()))
			return true;
		else return isSingleBlocked();
	}

	public boolean isSingleBlocked()
	{
		return KeypadChestBlock.isChestBlocked(getWorld(), getPos());
	}

//	@Override // Forge method
//	public void onLoad()
//	{
//		if(world.isClient)
//			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(pos));
//	}

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

	public void setSendsMessages(boolean value)
	{
		sendMessage.setValue(value);
		world.updateListeners(pos, getCachedState(), getCachedState(), 3); //sync option change to client
	}
}
