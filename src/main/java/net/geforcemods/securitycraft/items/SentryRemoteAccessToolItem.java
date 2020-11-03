package net.geforcemods.securitycraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
//import net.geforcemods.securitycraft.network.client.OpenSRATGui;
//import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class SentryRemoteAccessToolItem extends Item {

	public SentryRemoteAccessToolItem(Settings properties) {
		super(properties);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getStackInHand(hand);

//		if (!world.isClient) // TODO
//			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new OpenSRATGui((player.getServer().getPlayerManager().getViewDistance() - 1) * 16));

		return TypedActionResult.pass(stack);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
	}

	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		List<SentryEntity> sentries = world.getNonSpectatingEntities(SentryEntity.class, new Box(pos));

		if(!world.isClient){
			if(!sentries.isEmpty()) {
				SentryEntity sentry = sentries.get(0);
				BlockPos pos2 = sentry.getBlockPos();

				if(!isSentryAdded(stack, pos2)){
					int availSlot = getNextAvaliableSlot(stack);

					if(availSlot == 0){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.noSlots"), Formatting.RED);
						return ActionResult.FAIL;
					}

					if(!sentry.getOwner().isOwner(player)){
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.cantBind"), Formatting.RED);
						return ActionResult.FAIL;
					}

					if(stack.getTag() == null)
						stack.setTag(new CompoundTag());

					stack.getTag().putIntArray(("sentry" + availSlot), BlockUtils.fromPos(pos2));
//					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack)); // TODO
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.bound", pos2), Formatting.GREEN);
				}else{
					removeTagFromItemAndUpdate(stack, pos2, player);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.REMOTE_ACCESS_SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:srat.unbound", pos2), Formatting.RED);
				}
			}
//			else // TODO
//				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new OpenSRATGui((player.getServer().getPlayerManager().getViewDistance() - 1) * 16));
		}
		return ActionResult.SUCCESS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext flag) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
					tooltip.add(new LiteralText(Formatting.GRAY + "---"));
					continue;
				}
				else
				{
					BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
					List<SentryEntity> sentries = MinecraftClient.getInstance().player.world.getNonSpectatingEntities(SentryEntity.class, new Box(pos));
					String nameToShow;

					if(!sentries.isEmpty() && sentries.get(0).hasCustomName())
						nameToShow = sentries.get(0).getCustomName().getString();
					else
						nameToShow = ClientUtils.localize("tooltip.securitycraft:sentry") + " " + i;

					tooltip.add(new LiteralText(Formatting.GRAY + nameToShow + ": " + Utils.getFormattedCoordinates(pos)));
				}
			}
			else
				tooltip.add(new LiteralText(Formatting.GRAY + "---"));
	}

	private void removeTagFromItemAndUpdate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					stack.getTag().putIntArray("sentry" + i, new int[]{0, 0, 0});
//					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack)); // TODO
					return;
				}
			}
			else
				continue;

		return;
	}

	private boolean isSentryAdded(ItemStack stack, BlockPos pos) {
		if(stack.getTag() == null)
			return false;

		for(int i = 1; i <= 12; i++)
			if(stack.getTag().getIntArray("sentry" + i).length > 0){
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
					return true;
			}
			else
				continue;

		return false;
	}

	private int getNextAvaliableSlot(ItemStack stack){
		for(int i = 1; i <= 12; i++)
			if(stack.getTag() == null)
				return 1;
			else if(stack.getTag().getIntArray("sentry" + i).length == 0 || (stack.getTag().getIntArray("sentry" + i)[0] == 0 && stack.getTag().getIntArray("sentry" + i)[1] == 0 && stack.getTag().getIntArray("sentry" + i)[2] == 0))
				return i;
			else
				continue;

		return 0;
	}
}
