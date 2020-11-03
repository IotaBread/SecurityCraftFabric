package net.geforcemods.securitycraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
//import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class MineRemoteAccessToolItem extends Item {

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

	public MineRemoteAccessToolItem(Settings properties) {
		super(properties);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
//		if(world.isClient) // TODO
//			SecurityCraft.proxy.displayMRATGui(player.getStackInHand(hand));

		return TypedActionResult.pass(player.getStackInHand(hand));
	}

//	@Override // Forge method
//	public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext ctx)
//	{
//		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), stack, ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
//	}

	public ActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){

		if(!world.isClient)
		{
			if(BlockUtils.getBlock(world, pos) instanceof IExplosive){
				if(!isMineAdded(stack, pos)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_MINE.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.noSlots"), Formatting.RED);
						return ActionResult.FAIL;
					}

					if(world.getBlockEntity(pos) instanceof IOwnable && !((IOwnable) world.getBlockEntity(pos)).getOwner().isOwner(player))
					{
//						SecurityCraft.proxy.displayMRATGui(stack); // TODO
						return ActionResult.SUCCESS;
					}

					if(stack.getTag() == null)
						stack.setTag(new CompoundTag());

					stack.getTag().putIntArray(("mine" + availSlot), BlockUtils.fromPos(pos));
//					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack)); // TODO
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_MINE.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.bound", Utils.getFormattedCoordinates(pos)), Formatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_MINE.getTranslationKey()), ClientUtils.localize("messages.securitycraft:mrat.unbound", Utils.getFormattedCoordinates(pos)), Formatting.RED);
				}
			}
		}
//		else if(world.isClient && !(BlockUtils.getBlock(world, pos) instanceof IExplosive)) // TODO
//			SecurityCraft.proxy.displayMRATGui(stack);

		return ActionResult.SUCCESS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					list.add(new LiteralText(Formatting.GRAY + "---"));
					continue;
				}
				else
					list.add(ClientUtils.localize("tooltip.securitycraft:mine").append(new LiteralText(" " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2])).setStyle(GRAY_STYLE));
			}
			else
				list.add(new LiteralText(Formatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
//					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack)); // TODO
					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isMineAdded(ItemStack stack, BlockPos pos) {
		if(stack.getTag() == null)
			return false;

		for(int i = 1; i <= 6; i++)
			if(stack.getTag().getIntArray("mine" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;

		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 6; i++)
			if(stack.getTag() == null)
				return 1;
			else if(stack.getTag().getIntArray("mine" + i).length == 0 || (stack.getTag().getIntArray("mine" + i)[0] == 0 && stack.getTag().getIntArray("mine" + i)[1] == 0 && stack.getTag().getIntArray("mine" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}