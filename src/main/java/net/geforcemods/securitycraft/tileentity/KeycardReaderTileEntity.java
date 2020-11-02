package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
//import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
//import net.minecraft.util.Formatting;
//import net.minecraftforge.fml.network.NetworkHooks;

public class KeycardReaderTileEntity extends DisguisableTileEntity implements IPasswordProtected, NamedScreenHandlerFactory {

	private int passLV = 0;
	private boolean requiresExactKeycard = false;
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this, "signalLength", 60, 5, 400, 5, true); //20 seconds max

	public KeycardReaderTileEntity()
	{
		super(SCContent.teTypeKeycardReader);
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag toTag(CompoundTag tag){
		super.toTag(tag);
		tag.putInt("passLV", passLV);
		tag.putBoolean("requiresExactKeycard", requiresExactKeycard);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void fromTag(BlockState state, CompoundTag tag){
		super.fromTag(state, tag);

		if (tag.contains("passLV"))
			passLV = tag.getInt("passLV");

		if (tag.contains("requiresExactKeycard"))
			requiresExactKeycard = tag.getBoolean("requiresExactKeycard");

	}

	public void setRequiresExactKeycard(boolean par1) {
		requiresExactKeycard = par1;
	}

	public boolean doesRequireExactKeycard() {
		return requiresExactKeycard;
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isClient && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeycardReaderBlock)
			KeycardReaderBlock.activate(world, getPos(), signalLength.get());
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(getPassword() == null && player instanceof ServerPlayerEntity)
		{
//			if(getOwner().isOwner(player)) // TODO
//				NetworkHooks.openGui((ServerPlayerEntity)player, this, pos);
//			else
//				PlayerUtils.sendMessageToPlayer(player, new LiteralText("SecurityCraft"), ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), Formatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player, boolean isCodebreakerDisabled) {
		return false;
	}

	@Override
	public String getPassword() {
		return passLV == 0 ? null : String.valueOf(passLV);
	}

	@Override
	public void setPassword(String password) {
		passLV = Integer.parseInt(password);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST, ModuleType.BLACKLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ sendMessage, signalLength };
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}

	@Override
	public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeKeycardSetup, windowId, world, pos);
	}

	@Override
	public Text getDisplayName()
	{
		return new TranslatableText(SCContent.KEYCARD_READER.getTranslationKey());
	}
}
