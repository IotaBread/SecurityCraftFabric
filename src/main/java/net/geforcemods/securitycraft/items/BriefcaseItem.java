package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SCContent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUsageContext;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
//import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
//import net.minecraft.util.Hand;
//import net.minecraft.util.TypedActionResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;

import java.util.List;

public class BriefcaseItem extends Item {

	public static final Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

	public BriefcaseItem(Settings properties)
	{
		super(properties);
	}

//	@Override
//	public ActionResult useOnBlock(ItemUsageContext ctx)
//	{
//		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getHand());
//	}
//
//	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, Hand hand) {
//		if(world.isClient && hand == Hand.MAIN_HAND) {
//			if(!stack.hasTag()) {
//				stack.setTag(new CompoundTag());
//				ClientUtils.syncItemNBT(stack);
//			}
//
//			if(!stack.getTag().contains("passcode"))
//				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), stack.getName()));
//			else
//				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), stack.getName()));
//		}
//
//		return ActionResult.FAIL;
//	}
//
//	@Override
//	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//		ItemStack stack = player.getStackInHand(hand);
//
//		if(world.isClient && hand == Hand.MAIN_HAND) {
//			if(!stack.hasTag()) {
//				stack.setTag(new CompoundTag());
//				ClientUtils.syncItemNBT(stack);
//			}
//
//			if(!stack.getTag().contains("passcode"))
//				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), stack.getName()));
//			else
//				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), stack.getName()));
//		}
//
//		return TypedActionResult.pass(stack);
//	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack briefcase, World world, List<Text> tooltip, TooltipContext flag) {
		if (briefcase.hasTag() && briefcase.getTag().contains("owner"))
			tooltip.add(ClientUtils.localize("tooltip.securitycraft:briefcase.owner", briefcase.getTag().getString("owner")).setStyle(GRAY_STYLE));
	}

	public static boolean isOwnedBy(ItemStack briefcase, PlayerEntity player) {
		return !briefcase.hasTag() || !briefcase.getTag().contains("owner") || briefcase.getTag().getString("ownerUUID").equals(player.getUuid().toString()) || (briefcase.getTag().getString("ownerUUID").equals("ownerUUID") && briefcase.getTag().getString("owner").equals(player.getName().getString()));
	}
}
