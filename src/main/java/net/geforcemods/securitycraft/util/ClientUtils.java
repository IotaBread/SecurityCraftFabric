package net.geforcemods.securitycraft.util;

//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.geforcemods.securitycraft.SecurityCraft;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.util.ScreenshotUtils;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
//import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
//import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;

public class ClientUtils{

//	@Environment(EnvType.CLIENT)
//	public static void closePlayerScreen(){
//		MinecraftClient.getInstance().player.closeHandledScreen();
//	}
//
//	/**
//	 * Takes a screenshot, and sends the player a notification. <p>
//	 *
//	 * Only works on the CLIENT side.
//	 */
//	@Environment(EnvType.CLIENT)
//	public static void takeScreenshot() {
//		ScreenshotUtils.saveScreenshot(
//				MinecraftClient.getInstance().runDirectory,
//				MinecraftClient.getInstance().getWindow().getWidth(),
//				MinecraftClient.getInstance().getWindow().getHeight(),
//				MinecraftClient.getInstance().getFramebuffer(),
//				msg -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg)));
//	}
//
//	/**
//	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
//	 *
//	 * Only works on the CLIENT side.
//	 */
//	@Environment(EnvType.CLIENT)
//	public static String getFormattedMinecraftTime(){
//		Long time = MinecraftClient.getInstance().world.getTimeOfDay();
//
//		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
//		int hours = hours24 % 12;
//		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);
//
//		return String.format("%02d:%02d %s", Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM");
//	}
//
//	/**
//	 * Sends the client-side CompoundNBT of a block's TileEntity to the server.
//	 *
//	 * Only works on the CLIENT side.
//	 */
//	@Environment(EnvType.CLIENT)
//	public static void syncTileEntity(BlockEntity tileEntity){
//		CompoundTag tag = new CompoundTag();
//		tileEntity.toTag(tag);
//		SecurityCraft.channel.sendToServer(new SyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
//	}
//
//	/**
//	 * Sends the client-side CompoundNBT of a player's held item to the server.
//	 *
//	 * Only works on the CLIENT side.
//	 */
//	@Environment(EnvType.CLIENT)
//	public static void syncItemNBT(ItemStack item){
//		SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(item));
//	}

	/**
	 * Localizes a String with the given format
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslatableText localize(String key, Object... params)
	{
		for(int i = 0; i < params.length; i++)
		{
			if(params[i] instanceof TranslatableText)
				params[i] = localize(((TranslatableText)params[i]).getKey(), ((TranslatableText)params[i]).getArgs());
			else if(params[i] instanceof BlockPos)
				params[i] = Utils.getFormattedCoordinates((BlockPos)params[i]);
		}

		return new TranslatableText(key, params);
	}
}