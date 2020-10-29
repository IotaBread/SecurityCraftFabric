package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.CameraView;
//import net.geforcemods.securitycraft.misc.ModuleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.item.TooltipContext;
//import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.PacketDistributor;
//import net.geforcemods.securitycraft.network.client.UpdateNBTTagOnClient;
//import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
//import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.PlayerUtils;
//import net.geforcemods.securitycraft.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CameraMonitorItem extends Item {

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);

	public CameraMonitorItem(Settings properties) {
		super(properties);
	}

//	@Override
//	public ActionResult useOnBlock(ItemUsageContext ctx)
//	{
//		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
//	}
//
//	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
//		if(!world.isClient){
//			if(BlockUtils.getBlock(world, pos) == SCContent.SECURITY_CAMERA && !PlayerUtils.isPlayerMountedOnCamera(player)){
//				if(!((IOwnable) world.getBlockEntity(pos)).getOwner().isOwner(player) && !((SecurityCameraTileEntity)world.getBlockEntity(pos)).hasModule(ModuleType.SMART)){
//					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.cannotView"), Formatting.RED);
//					return ActionResult.SUCCESS;
//				}
//
//				if(stack.getTag() == null)
//					stack.setTag(new CompoundTag());
//
//				CameraView view = new CameraView(pos, player.world.getRegistryKey());
//
//				if(isCameraAdded(stack.getTag(), view)){
//					stack.getTag().remove(getTagNameFromPosition(stack.getTag(), view));
//					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.unbound", Utils.getFormattedCoordinates(pos)), Formatting.RED);
//					return ActionResult.SUCCESS;
//				}
//
//				for(int i = 1; i <= 30; i++)
//					if (!stack.getTag().contains("Camera" + i)){
//						stack.getTag().putString("Camera" + i, view.toNBTString());
//						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.bound", Utils.getFormattedCoordinates(pos)), Formatting.GRAY);
//						break;
//					}
//
//				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new UpdateNBTTagOnClient(stack));
//
//				return ActionResult.SUCCESS;
//			}
//		}else if(world.isClient && (BlockUtils.getBlock(world, pos) != SCContent.SECURITY_CAMERA || PlayerUtils.isPlayerMountedOnCamera(player))){
//			if(stack.getTag() == null || stack.getTag().isEmpty()) {
//				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), Formatting.RED);
//				return ActionResult.SUCCESS;
//			}
//
//			SecurityCraft.proxy.displayCameraMonitorGui(player.inventory, (CameraMonitorItem) stack.getItem(), stack.getTag());
//			return ActionResult.SUCCESS;
//		}
//
//		return ActionResult.SUCCESS;
//	}
//
//	@Override
//	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//		ItemStack stack = player.getStackInHand(hand);
//
//		if (world.isClient) {
//			if(!stack.hasTag() || !hasCameraAdded(stack.getTag())) {
//				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.CAMERA_MONITOR.getTranslationKey()), ClientUtils.localize("messages.securitycraft:cameraMonitor.rightclickToView"), Formatting.RED);
//				return TypedActionResult.pass(stack);
//			}
//
//			if(stack.getItem() == SCContent.CAMERA_MONITOR)
//				SecurityCraft.proxy.displayCameraMonitorGui(player.inventory, (CameraMonitorItem) stack.getItem(), stack.getTag());
//		}
//
//		return TypedActionResult.pass(stack);
//	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext flag) {
		if(stack.getTag() == null)
			return;

		tooltip.add(ClientUtils.localize("tooltip.securitycraft:cameraMonitor").append(new LiteralText(" " + getNumberOfCamerasBound(stack.getTag()) + "/30")).setStyle(GRAY_STYLE));
	}

	public static String getTagNameFromPosition(CompoundTag tag, CameraView view) {
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return "Camera" + i;
			}

		return "";
	}

	public boolean hasCameraAdded(CompoundTag tag){
		if(tag == null) return false;

		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i))
				return true;

		return false;
	}

	public boolean isCameraAdded(CompoundTag tag, CameraView view){
		for(int i = 1; i <= 30; i++)
			if(tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				if(view.checkCoordinates(coords))
					return true;
			}

		return false;
	}

	public ArrayList<CameraView> getCameraPositions(CompoundTag tag){
		ArrayList<CameraView> list = new ArrayList<>();

		for(int i = 1; i <= 30; i++)
			if(tag != null && tag.contains("Camera" + i)){
				String[] coords = tag.getString("Camera" + i).split(" ");

				list.add(new CameraView(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), (coords.length == 4 ? new Identifier(coords[3]) : null)));
			}
			else
				list.add(null);

		return list;
	}

	public int getNumberOfCamerasBound(CompoundTag tag) {
		if(tag == null) return 0;

		int amount = 0;

		for(int i = 1; i <= 31; i++)
		{
			if(tag.contains("Camera" + i))
				amount++;
		}

		return amount;
	}

}