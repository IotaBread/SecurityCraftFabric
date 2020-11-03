package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.blocks.*;
//import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
//import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
//import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
//import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
//import net.geforcemods.securitycraft.util.ClientUtils;
//import net.geforcemods.securitycraft.util.IBlockMine;
//import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.enums.DoubleBlockHalf;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemUsageContext;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;

public class UniversalBlockRemoverItem extends Item
{
	public UniversalBlockRemoverItem(Settings properties)
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
//		BlockState state = world.getBlockState(pos);
//		Block block = state.getBlock();
//		BlockEntity tileEntity = world.getBlockEntity(pos);
//		PlayerEntity player = ctx.getPlayer();
//
//		if(tileEntity != null && isOwnableBlock(block, tileEntity))
//		{
//			if(!((IOwnable) tileEntity).getOwner().isOwner(player))
//			{
//				if(!(block instanceof IBlockMine) && (!(tileEntity instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)tileEntity).getCachedState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock)))
//					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned", ((IOwnable) tileEntity).getOwner().getName()), Formatting.RED);
//
//				return ActionResult.FAIL;
//			}
//
//			if(tileEntity instanceof IModuleInventory)
//			{
//				boolean isChest = tileEntity instanceof KeypadChestTileEntity;
//
//				for(ItemStack module : ((IModuleInventory)tileEntity).getInventory())
//				{
//					if(isChest)
//						((KeypadChestTileEntity)tileEntity).addOrRemoveModuleFromAttached(module, true);
//
//					Block.dropStack(world, pos, module);
//				}
//			}
//
//			if(block == SCContent.LASER_BLOCK)
//			{
//				CustomizableTileEntity te = (CustomizableTileEntity)world.getBlockEntity(pos);
//
//				for(ItemStack module : te.getInventory())
//				{
//					if(!module.isEmpty())
//						te.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[] {module, ((ModuleItem)module.getItem()).getModuleType()}, te);
//				}
//
//				world.breakBlock(pos, true);
//				LaserBlock.destroyAdjacentLasers(world, pos);
//				player.inventory.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(ctx.getHand()));
//			}
//			else if(block == SCContent.CAGE_TRAP && world.getBlockState(pos).get(CageTrapBlock.DEACTIVATED))
//			{
//				BlockPos originalPos = pos;
//				BlockPos middlePos = originalPos.up(4);
//
//				new CageTrapBlock.BlockModifier(world, new BlockPos.Mutable().set(originalPos), ((IOwnable)tileEntity).getOwner()).loop((w, p, o) -> {
//					BlockEntity te = w.getBlockEntity(p);
//
//					if(te instanceof IOwnable && ((IOwnable)te).getOwner().equals(o))
//					{
//						Block b = w.getBlockState(p).getBlock();
//
//						if(b == SCContent.REINFORCED_IRON_BARS || (p.equals(middlePos) && b == SCContent.HORIZONTAL_REINFORCED_IRON_BARS))
//							w.breakBlock(p, false);
//					}
//				});
//
//				world.breakBlock(originalPos, false);
//			}
//			else
//			{
//				if((block instanceof ReinforcedDoorBlock || block instanceof ScannerDoorBlock) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
//					pos = pos.down();
//
//				if(block == SCContent.INVENTORY_SCANNER)
//				{
//					InventoryScannerTileEntity te = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);
//
//					if(te != null)
//						te.getInventory().clear();
//				}
//
//				world.breakBlock(pos, true);
//				world.removeBlockEntity(pos);
//				player.inventory.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(ctx.getHand()));
//			}
//
//			return ActionResult.SUCCESS;
//		}
//
//		return ActionResult.PASS;
//	}

	private static boolean isOwnableBlock(Block block, BlockEntity te)
	{
		return (te instanceof OwnableTileEntity || te instanceof IOwnable || block instanceof OwnableBlock);
	}
}
