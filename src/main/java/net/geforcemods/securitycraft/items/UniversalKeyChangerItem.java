package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
//import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalKeyChangerItem extends Item {

	public UniversalKeyChangerItem(Settings properties) {
		super(properties);
	}

//	@Override // Forge method
//	public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext ctx)
//	{
//		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getStack());
//	}

	public ActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, double hitX, double hitY, double hitZ, ItemStack stack) {
		BlockEntity te = world.getBlockEntity(pos);

		if(!world.isClient && te instanceof IPasswordProtected) {
			if(((IOwnable) te).getOwner().isOwner(player))
			{
//				if(player instanceof ServerPlayerEntity)
//					NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//						@Override
//						public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//						{
//							return new GenericTEContainer(SCContent.cTypeKeyChanger, windowId, world, pos);
//						}
//
//						@Override
//						public Text getDisplayName()
//						{
//							return new TranslatableText(getTranslationKey());
//						}
//					}, pos);

				return ActionResult.SUCCESS;
			}
			else if(!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getCachedState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned", ((IOwnable) world.getBlockEntity(pos)).getOwner().getName()), Formatting.RED);
		}

		return ActionResult.FAIL;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack keyChanger = player.getStackInHand(hand);

		if (!world.isClient) {
			if (hand == Hand.MAIN_HAND && player.getOffHandStack().getItem() == SCContent.BRIEFCASE) {
				ItemStack briefcase = player.getOffHandStack();

				if (BriefcaseItem.isOwnedBy(briefcase, player)) {
					if (briefcase.hasTag() && briefcase.getTag().contains("passcode")) {
						briefcase.getTag().remove("passcode");
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), Formatting.GREEN);
						return TypedActionResult.success(keyChanger);
					}
					else
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), Formatting.RED);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), Formatting.RED);
			}
		}

		return TypedActionResult.fail(keyChanger);
	}
}
