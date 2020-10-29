package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;

import java.util.List;

public class AdminToolItem extends Item {

	public AdminToolItem(Settings properties) {
		super(properties);
	}

//	@Override
	public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		PlayerEntity player = ctx.getPlayer();

		if(world.isClient && ConfigHandler.CONFIG.allowAdminTool) {
			MutableText adminToolName = ClientUtils.localize(SCContent.ADMIN_TOOL.getTranslationKey());

			if(!player.isCreative())
			{
				PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.needCreative"), Formatting.DARK_PURPLE);
				return ActionResult.FAIL;
			}

			if(world.getBlockEntity(pos) != null) {
				BlockEntity te = world.getBlockEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), Formatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), Formatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.password", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), Formatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory) {
					List<ModuleType> modules = ((IModuleInventory) te).getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.equippedModules"), Formatting.DARK_PURPLE);

						for(ModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new LiteralText("- ").append(new TranslatableText(module.getTranslationKey())), Formatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

//				if(te instanceof SecretSignTileEntity)
//				{
//					PlayerUtils.sendMessageToPlayer(player, adminToolName, new LiteralText(""), Formatting.DARK_PURPLE); //EMPTY
//
//					for(int i = 0; i < 4; i++)
//					{
//						StringVisitable text = ((SecretSignTileEntity)te).getTextOnRow(i);
//
//						if(text instanceof MutableText)
//							PlayerUtils.sendMessageToPlayer(player, adminToolName, (MutableText)text, Formatting.DARK_PURPLE);
//					}
//
//					hasInfo = true;
//				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), Formatting.DARK_PURPLE);

				return ActionResult.FAIL;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), Formatting.DARK_PURPLE);
		}

		return ActionResult.FAIL;
	}

}
