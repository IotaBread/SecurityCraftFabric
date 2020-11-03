package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedHopperTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock
{
	public ReinforcedHopperBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);

		if(stack.hasCustomName())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				((ReinforcedHopperTileEntity)te).setCustomName(stack.getName());
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				player.openHandledScreen((ReinforcedHopperTileEntity)te);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
			{
				ItemScatterer.spawn(world, pos, (ReinforcedHopperTileEntity)te);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof ReinforcedHopperTileEntity)
			((ReinforcedHopperTileEntity)te).onEntityCollision(entity);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new ReinforcedHopperTileEntity();
	}

	@Override
	public boolean is(Block block)
	{
		return block == this || block == Blocks.HOPPER;
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.HOPPER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(ENABLED, vanillaState.get(ENABLED)).with(FACING, vanillaState.get(FACING));
	}

	public static class ExtractionBlock implements IExtractionBlock
	{
		@Override
		public boolean canExtract(IOwnable te, World world, BlockPos pos, BlockState state)
		{
			ReinforcedHopperTileEntity hopperTe = (ReinforcedHopperTileEntity)world.getBlockEntity(pos);

			if(!te.getOwner().owns(hopperTe))
			{
				if(te instanceof IModuleInventory)
				{
					IModuleInventory inv = (IModuleInventory)te;

					if(inv.hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(inv.getModule(ModuleType.WHITELIST)).contains(hopperTe.getOwner().getName().toLowerCase()))
						return true;
				}

				return false;
			}
			else return true;
		}

		@Override
		public Block getBlock()
		{
			return SCContent.REINFORCED_HOPPER;
		}
	}
}
