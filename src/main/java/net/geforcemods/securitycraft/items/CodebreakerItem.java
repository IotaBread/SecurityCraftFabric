package net.geforcemods.securitycraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.containers.BriefcaseContainer;
//import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class CodebreakerItem extends Item {

	public CodebreakerItem(Settings properties) {
		super(properties);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack codebreaker = player.getStackInHand(hand);

		if (hand == Hand.MAIN_HAND && player.getOffHandStack().getItem() == SCContent.BRIEFCASE) {
			if(!world.isClient && !ConfigHandler.CONFIG.allowCodebreakerItem) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.BRIEFCASE.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), Formatting.RED);
				return TypedActionResult.fail(codebreaker);
			}
			else {
				codebreaker.damage(1, player, p -> p.sendToolBreakStatus(hand));

				if (!world.isClient && new Random().nextInt(3) == 1) {
					ItemStack briefcase = player.getOffHandStack();

//					NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//						@Override
//						public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//						{
//							return new BriefcaseContainer(windowId, inv, new BriefcaseInventory(briefcase));
//						}
//
//						@Override
//						public Text getDisplayName()
//						{
//							return briefcase.getName();
//						}
//					}, player.getBlockPos());
				}
			}

			return TypedActionResult.success(codebreaker);
		}

		return TypedActionResult.pass(codebreaker);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean hasGlint(ItemStack stack){
		return true;
	}

	/**
	 * Return an item rarity from Rarity
	 */
	@Override
	public Rarity getRarity(ItemStack stack){
		return Rarity.RARE;
	}

//	@Override // Forge method
//	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
//	{
//		return false;
//	}
//
//	@Override // Forge method
//	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
//	{
//		return false;
//	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
