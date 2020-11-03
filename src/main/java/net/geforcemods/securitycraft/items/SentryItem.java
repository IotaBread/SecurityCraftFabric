package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SentryItem extends Item
{
	public SentryItem(Settings properties)
	{
		super(properties);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
	}

	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ)
	{
		if(!world.isClient)
		{
			pos = pos.offset(facing); //get sentry position

			if(!world.isAir(pos))
				return ActionResult.PASS;
			else if(world.isAir(pos.down()))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:sentry.needsBlockBelow"), Formatting.DARK_RED);
				return ActionResult.FAIL;
			}

			SentryEntity entity = SCContent.eTypeSentry.create(world);

			entity.setupSentry(player);
			entity.updatePosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
			world.spawnEntity(entity);

			if(!player.isCreative())
				stack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}
}
