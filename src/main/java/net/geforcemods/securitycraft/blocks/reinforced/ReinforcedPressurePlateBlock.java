package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
//import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ReinforcedPressurePlateBlock extends PressurePlateBlock implements IReinforcedBlock
{
	private final Block vanillaBlock;

	public ReinforcedPressurePlateBlock(ActivationRule sensitivity, Settings settings, Block vanillaBlock)
	{
		super(sensitivity, settings);

		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		int redstoneStrength = getRedstoneOutput(state);

		if(!world.isClient && redstoneStrength == 0 && entity instanceof PlayerEntity)
		{
			BlockEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof WhitelistOnlyTileEntity)
			{
				if(isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
					updatePlateState(world, pos, state, redstoneStrength);
			}
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos)
	{
		Box aabb = BOX.offset(pos);
		List<? extends Entity> list;

		list = world.getOtherEntities(null, aabb);

		if(!list.isEmpty())
		{
			BlockEntity tileEntity = world.getBlockEntity(pos);

			if(tileEntity instanceof WhitelistOnlyTileEntity)
			{
				for(Entity entity : list)
				{
					if(entity instanceof PlayerEntity && isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)tileEntity, (PlayerEntity)entity))
						return 15;
				}
			}
		}

		return 0;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, WhitelistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(entity.getName().asString().toLowerCase());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity)  placer);
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder)
	{
		return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(this));
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new WhitelistOnlyTileEntity();
//	}
}
