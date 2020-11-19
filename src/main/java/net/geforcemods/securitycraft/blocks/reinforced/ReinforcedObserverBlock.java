package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;


//import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ReinforcedObserverBlock extends ObserverBlock implements IReinforcedBlock, BlockEntityProvider
{
	public ReinforcedObserverBlock(Settings settings)
	{
		super(settings);
	}

//	@Override // Forge method
//	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, @Nullable Direction side)
//	{
//		return side == state.get(ObserverBlock.FACING);
//	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.OBSERVER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(FACING, vanillaState.get(FACING)).with(POWERED, vanillaState.get(POWERED));
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder)
	{
		return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new OwnableTileEntity();
	}
}
