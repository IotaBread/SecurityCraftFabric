package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
//import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;

public class SecretSignTileEntity extends SignBlockEntity implements IOwnable, IModuleInventory, ICustomizable
{
	private Owner owner = new Owner();
	private BooleanOption isSecret = new BooleanOption("isSecret", true);
	private DefaultedList<ItemStack> modules = DefaultedList.<ItemStack>ofSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	@Override
	public BlockEntityType<?> getType()
	{
		return SCContent.teTypeSecretSign;
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

		if (tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	@Override
	public DefaultedList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ isSecret };
	}

	public boolean isSecret() {
		return isSecret.get();
	}

	public boolean isPlayerAllowedToSeeText(PlayerEntity player) {
		return !isSecret() || getOwner().isOwner(player) || ModuleUtils.checkForModule(getWorld(), getPos(), player, ModuleType.WHITELIST);
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		world.updateListeners(pos, getCachedState(), getCachedState(), 2);
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

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

//	@Override // Forge method
//	public void onLoad()
//	{
//		if(world.isClient)
//			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getPos()));
//	}
}
