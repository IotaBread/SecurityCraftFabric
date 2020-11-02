package net.geforcemods.securitycraft.blocks.reinforced;

//import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
//import net.minecraft.world.BlockView;
//import net.minecraft.world.WorldView;
//import net.minecraftforge.common.IPlantable;
//import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.function.Supplier;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Settings settings, Block vB)
	{
		this(settings, () -> vB);
	}

	public BaseReinforcedBlock(Settings settings, Supplier<Block> vB)
	{
		super(settings);

		vanillaBlockSupplier = vB;
	}

//	@Override // Forge method
//	public boolean canSustainPlant(BlockState state, BlockView world, BlockPos pos, Direction facing, IPlantable plantable)
//	{
//		BlockState plant = plantable.getPlant(world, pos.offset(facing));
//		PlantType type = plantable.getPlantType(world, pos.offset(facing));
//
//		if(plant.getBlock() == Blocks.field_10029)
//			return this == SCContent.REINFORCED_SAND || this == SCContent.REINFORCED_RED_SAND;
//
//		if (plantable instanceof PlantBlock) //a workaround because BaseReinforcedBlock can't use isValidGround because it is protected
//		{
//			boolean bushCondition = state.isOf(SCContent.REINFORCED_GRASS_BLOCK) || state.isOf(SCContent.REINFORCED_DIRT) || state.isOf(SCContent.REINFORCED_COARSE_DIRT) || state.isOf(SCContent.REINFORCED_PODZOL);
//
//			if (plantable instanceof SproutsBlock || plantable instanceof RootsBlock || plantable instanceof FungusBlock)
//				return state.isIn(SCTags.Blocks.REINFORCED_NYLIUM) || state.isOf(SCContent.REINFORCED_SOUL_SOIL) || bushCondition;
//		}
//
//		if(type == PlantType.DESERT)
//			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
//		else if(type == PlantType.CAVE)
//			return state.isSideSolidFullSquare(world, pos, Direction.field_11036);
//		else if(type == PlantType.PLAINS)
//			return isIn(SCTags.Blocks.REINFORCED_DIRT);
//		else if(type == PlantType.BEACH)
//		{
//			boolean isBeach = isIn(SCTags.Blocks.REINFORCED_DIRT) || this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
//			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.south()).getMaterial() == Material.WATER);
//			return isBeach && hasWater;
//		}
//		return false;
//	}
//
//	@Override // Forge method
//	public boolean isConduitFrame(BlockState state, WorldView world, BlockPos pos, BlockPos conduit)
//	{
//		return this == SCContent.REINFORCED_PRISMARINE || this == SCContent.REINFORCED_PRISMARINE_BRICKS || this == SCContent.REINFORCED_SEA_LANTERN || this == SCContent.REINFORCED_DARK_PRISMARINE;
//	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (this.getVanillaBlock() instanceof TransparentBlock)
			return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
		return false;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
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
}
