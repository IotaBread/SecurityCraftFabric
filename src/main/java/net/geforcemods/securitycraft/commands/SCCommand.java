package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class SCCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("sc")
				.requires(Predicates.alwaysTrue())
				.then(connect())
				.then(help())
				.then(bug()));
	}

	private static ArgumentBuilder<ServerCommandSource, ?> connect() {
		return CommandManager.literal("connect").executes(ctx -> {
			ctx.getSource().getPlayer().sendSystemMessage(new LiteralText("[")
					.append(new LiteralText("IRC").formatted(Formatting.GREEN))
					.append(new LiteralText("] "))
					.append(ClientUtils.localize("messages.securitycraft:irc.connected"))
					.append(new LiteralText(" "))
					.append(Utils.newChatLink(SCEventHandler.tipsWithLink.get("discord"))), Util.NIL_UUID); //appendSibling
			return 0;
		});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> help() {
		return CommandManager.literal("help").executes(ctx -> {
			ctx.getSource().getPlayer().sendSystemMessage(new TranslatableText("messages.securitycraft:sc_help",
					new TranslatableText(Blocks.CRAFTING_TABLE.getTranslationKey()),
					new TranslatableText(Items.BOOK.getTranslationKey()),
					new TranslatableText(Items.IRON_BARS.getTranslationKey())), Util.NIL_UUID);
			return 0;
		});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> bug() {
		return CommandManager.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayer(), new LiteralText("SecurityCraft"), ClientUtils.localize("messages.securitycraft:bugReport"), SCEventHandler.tipsWithLink.get("discord"), Formatting.GOLD);
			return 0;
		});
	}
}
