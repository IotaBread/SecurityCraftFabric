package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
//import net.geforcemods.securitycraft.misc.SCSounds;
//import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.PacketDistributor;

public class KeyPanelItem extends Item {

	public KeyPanelItem(Settings properties){
		super(properties);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z);
	}

	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ){
		if(!world.isClient){
			IPasswordConvertible.BLOCKS.forEach(pc -> {
				if(BlockUtils.getBlock(world, pos) == ((IPasswordConvertible)pc).getOriginalBlock())
				{
					if(((IPasswordConvertible)pc).convert(player, world, pos))
					{
						if(!player.isCreative())
							stack.decrement(1);

//						SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SCSounds.LOCK.location.toString(), 1.0F, "blocks")); // TODO
					}
				}
			});
			return ActionResult.SUCCESS;
		}

		return ActionResult.FAIL;
	}
}
