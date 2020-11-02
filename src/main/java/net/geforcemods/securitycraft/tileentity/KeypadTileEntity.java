package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
//import net.minecraftforge.fml.network.NetworkHooks;

public class KeypadTileEntity extends DisguisableTileEntity implements IPasswordProtected {

	private String passcode;

	private BooleanOption isAlwaysActive = new BooleanOption("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			BlockUtils.setBlockProperty(world, pos, KeypadBlock.POWERED, get());
			world.updateNeighborsAlways(pos, SCContent.KEYPAD);
		}
	};
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this, "signalLength", 60, 5, 400, 5, true); //20 seconds max

	public KeypadTileEntity()
	{
		super(SCContent.teTypeKeypad);
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if (tag.contains("passcode"))
			if(tag.getInt("passcode") != 0)
				passcode = String.valueOf(tag.getInt("passcode"));
			else
				passcode = tag.getString("passcode");
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isClient && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeypadBlock)
			KeypadBlock.activate(world, pos, signalLength.get());
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
//						return new TranslatableText(SCContent.KEYPAD.get().getTranslationKey());
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
//							return new TranslatableText(SCContent.KEYPAD.get().getTranslationKey());
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
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), Formatting.RED);
		else if(!BlockUtils.getBlockProperty(world, pos, KeypadBlock.POWERED)) {
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

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST, ModuleType.BLACKLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ isAlwaysActive, sendMessage, signalLength };
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}
}
