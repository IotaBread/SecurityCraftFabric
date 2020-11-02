package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

import java.util.function.Supplier;

public class ReinforcedRotatedPillarBlock extends BaseReinforcedBlock
{
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

	public ReinforcedRotatedPillarBlock(Settings settings, Block vB)
	{
		this(settings, () -> vB);
	}

	public ReinforcedRotatedPillarBlock(Settings settings, Supplier<Block> vB)
	{
		super(settings, vB);

		setDefaultState(stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		switch(rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.get(AXIS))
				{
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AXIS);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context)
	{
		return getDefaultState().with(AXIS, context.getSide().getAxis());
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(AXIS, vanillaState.get(PillarBlock.AXIS));
	}
}
