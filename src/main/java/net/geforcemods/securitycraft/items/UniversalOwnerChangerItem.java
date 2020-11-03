package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class UniversalOwnerChangerItem extends Item
{
	public UniversalOwnerChangerItem(Settings properties)
	{
		super(properties);
	}

//	@Override // Forge method
//	public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext ctx)
//	{
//		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), stack, ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
//	}

	public ActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction side, double hitX, double hitY, double hitZ)
	{
		Block block = world.getBlockState(pos).getBlock();
		BlockEntity te = world.getBlockEntity(pos);
		String newOwner = stack.getName().getString();

		if(!world.isClient)
		{
			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), Formatting.RED);
				return ActionResult.FAIL;
			}

			Owner owner = ((IOwnable)te).getOwner();
			boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

			if(!owner.isOwner(player) && !isDefault)
			{
				if(!(block instanceof IBlockMine) && (!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getCachedState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock)))
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), Formatting.RED);

				return ActionResult.FAIL;
			}

			if(!stack.hasCustomName() && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), Formatting.RED);
				return ActionResult.FAIL;
			}

			if(isDefault)
			{
				if(ConfigHandler.CONFIG.allowBlockClaim)
					newOwner = player.getName().getString();
				else
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), Formatting.RED);
					return ActionResult.FAIL;
				}
			}

			boolean door = false;
			boolean updateTop = true;

			if(BlockUtils.getBlock(world, pos) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos) instanceof ScannerDoorBlock)
			{
				door = true;
				((IOwnable)world.getBlockEntity(pos)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID", newOwner);

				if(BlockUtils.getBlock(world, pos.up()) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos.up()) instanceof ScannerDoorBlock)
					((IOwnable)world.getBlockEntity(pos.up())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID", newOwner);
				else if(BlockUtils.getBlock(world, pos.down()) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos.down()) instanceof ScannerDoorBlock)
				{
					((IOwnable)world.getBlockEntity(pos.down())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID", newOwner);
					updateTop = false;
				}
			}

			if(te instanceof IOwnable)
				((IOwnable)te).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID", newOwner);

			world.getServer().getPlayerManager().sendToAll(te.toUpdatePacket());

			if(door)
				world.getServer().getPlayerManager().sendToAll(((OwnableTileEntity)world.getBlockEntity(updateTop ? pos.up() : pos.down())).toUpdatePacket());

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), Formatting.GREEN);
			return ActionResult.SUCCESS;
		}

		return ActionResult.FAIL;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack ownerChanger = player.getStackInHand(hand);

		if (!world.isClient) {
			if (!ownerChanger.hasCustomName()) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), Formatting.RED);
				return TypedActionResult.fail(ownerChanger);
			}

			if (hand == Hand.MAIN_HAND && player.getOffHandStack().getItem() == SCContent.BRIEFCASE) {
				ItemStack briefcase = player.getOffHandStack();

				if (BriefcaseItem.isOwnedBy(briefcase, player)) {
					String newOwner = ownerChanger.getName().getString();

					if (!briefcase.hasTag())
						briefcase.setTag(new CompoundTag());

					briefcase.getTag().putString("owner", newOwner);
					briefcase.getTag().putString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID");
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), Formatting.GREEN);
					return TypedActionResult.success(ownerChanger);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), Formatting.RED);
			}
		}

		return TypedActionResult.fail(ownerChanger);
	}
}