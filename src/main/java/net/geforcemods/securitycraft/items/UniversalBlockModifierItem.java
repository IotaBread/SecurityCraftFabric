package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.api.IModuleInventory;
//import net.geforcemods.securitycraft.api.IOwnable;
//import net.geforcemods.securitycraft.blocks.DisguisableBlock;
//import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
//import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
//import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.PlayerUtils;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUsageContext;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockModifierItem extends Item
{
	public UniversalBlockModifierItem(Settings properties)
	{
		super(properties);
	}

//	@Override // Forge method
//	public ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext ctx)
//	{
//		if(ctx.getWorld().isClient)
//			return ActionResult.FAIL;
//
//		World world = ctx.getWorld();
//		BlockPos pos = ctx.getBlockPos();
//		BlockEntity te = world.getBlockEntity(pos);
//		PlayerEntity player = ctx.getPlayer();
//
//		if(te instanceof IModuleInventory)
//		{
//			if(te instanceof IOwnable && !((IOwnable) te).getOwner().isOwner(player))
//			{
//				if(!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getCachedState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))
//					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_BLOCK_MODIFIER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned", ((IOwnable) te).getOwner().getName()), Formatting.RED);
//
//				return ActionResult.FAIL;
//			}
//			else if(player instanceof ServerPlayerEntity)
//			{
//				NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//					@Override
//					public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//					{
//						return new CustomizeBlockContainer(windowId, world, pos, inv);
//					}
//
//					@Override
//					public Text getDisplayName()
//					{
//						return new TranslatableText(te.getCachedState().getBlock().getTranslationKey());
//					}
//				}, pos);
//			}
//
//			return ActionResult.SUCCESS;
//		}
//
//		return ActionResult.PASS;
//	}
}
