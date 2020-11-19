package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class ReinforcedRedstoneLampBlock extends BaseReinforcedBlock
{
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public ReinforcedRedstoneLampBlock(Settings settings, Block vB)
	{
		super(settings, vB);

		setDefaultState(getDefaultState().with(LIT, false));
	}
	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		if(!world.isClient)
		{
			boolean isLit = state.get(LIT);

			if(isLit != world.isReceivingRedstonePower(pos))
			{
				if(isLit)
					world.getBlockTickScheduler().schedule(pos, this, 4);
				else
					world.setBlockState(pos, state.cycle(LIT), 2); //cycle
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(state.get(LIT) && !world.isReceivingRedstonePower(pos))
			world.setBlockState(pos, state.cycle(LIT), 2); //cycle
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
