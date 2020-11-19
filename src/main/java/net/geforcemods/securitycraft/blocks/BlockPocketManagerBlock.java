package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

public class BlockPocketManagerBlock extends OwnableBlock implements BlockEntityProvider
{
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);

	public BlockPocketManagerBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient)
		{
			BlockEntity te = world.getBlockEntity(pos);

//			if(te instanceof NamedScreenHandlerFactory) // TODO
//				NetworkHooks.openGui((ServerPlayerEntity)player, (NamedScreenHandlerFactory)te, pos);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context)
	{
		return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new BlockPocketManagerTileEntity();
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
