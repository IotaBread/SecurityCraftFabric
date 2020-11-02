package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.geforcemods.securitycraft.compat.fabric.FabricMisc;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class PlayerUtils{

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static PlayerEntity getPlayerFromName(String name){
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
			List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();
				if(tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}else{
			List<?> players = FabricMisc.getServer().getPlayerManager().getPlayerList();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();
				if(tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}
	}

	/**
	 * Returns true if a player with the given name is in the world.
	 */
	public static boolean isPlayerOnline(String name) {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
			for(AbstractClientPlayerEntity player : MinecraftClient.getInstance().world.getPlayers()){
				if(player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (FabricMisc.getServer().getPlayerManager().getPlayer(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, MutableText prefix, MutableText text, Formatting color){
		PlayerEntity player = getPlayerFromName(playerName);

		if(player != null)
		{
			player.sendSystemMessage(new LiteralText("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(new LiteralText("] ")).setStyle(Style.EMPTY.withColor(Formatting.WHITE))
					.append(text), Util.NIL_UUID); //appendSibling
		}
	}

	public static void sendMessageToPlayer(PlayerEntity player, MutableText prefix, MutableText text, Formatting color){
		player.sendSystemMessage(new LiteralText("[")
				.append(prefix.setStyle(Style.EMPTY.withColor(color)))
				.append(new LiteralText("] ")).setStyle(Style.EMPTY.withColor(Formatting.WHITE))
				.append(text), Util.NIL_UUID); //appendSibling
	}

	/**
	 * Sends the given {@link net.minecraft.command.CommandSource} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(CommandOutput sender, MutableText prefix, MutableText text, String link, Formatting color){
		sender.sendSystemMessage(new LiteralText("[")
				.append(prefix.setStyle(Style.EMPTY.withColor(color)))
				.append(new LiteralText("] ")).setStyle(Style.EMPTY.withColor(Formatting.WHITE))
				.append(text)
				.append(new LiteralText(": "))
				.append(FabricMisc.newChatLink(link)), Util.NIL_UUID); //appendSibling
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(PlayerEntity player, Supplier<Item> item){
		return isHoldingItem(player, item.get());
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(PlayerEntity player, Item item){
		if(item == null && player.inventory.getMainHandStack().isEmpty())
			return true;

		return (!player.inventory.getMainHandStack().isEmpty() && player.inventory.getMainHandStack().getItem() == item);
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		return entity.getVehicle() instanceof SecurityCameraEntity;
	}
}
