package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class HorizontalReinforcedIronBars extends BaseReinforcedBlock implements FluidDrainable, FluidFillable
{
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(-8.0D, 14.0D, -8.0D, 24.0D, 16.0D, 24.0D);

	public HorizontalReinforcedIronBars(Settings settings, Block vB)
	{
		super(settings, vB);
		setDefaultState(stateManager.getDefaultState().with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return SHAPE;
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if(state.getBlock() == this)
			return state;
		else
			return getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		if(!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isClient())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, true), 3);
				world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(WATERLOGGED, vanillaState.get(SlabBlock.WATERLOGGED));
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder)
	{
		return DefaultedList.copyOf(ItemStack.EMPTY);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SCContent.REINFORCED_IRON_BARS);
	}
}