package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.function.Supplier;

public class ReinforcedWallBlock extends WallBlock implements IReinforcedBlock, BlockEntityProvider
{
	private final Supplier<Block> vanillaBlockSupplier;

	public ReinforcedWallBlock(Settings settings, Block vanillaBlock)
	{
		super(settings);

		this.vanillaBlockSupplier = () -> vanillaBlock;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState()
				.with(UP, vanillaState.get(UP))
				.with(NORTH_SHAPE, vanillaState.get(NORTH_SHAPE))
				.with(EAST_SHAPE, vanillaState.get(EAST_SHAPE))
				.with(SOUTH_SHAPE, vanillaState.get(SOUTH_SHAPE))
				.with(WEST_SHAPE, vanillaState.get(WEST_SHAPE))
				.with(WATERLOGGED, vanillaState.get(WATERLOGGED));
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
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity)placer);
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
