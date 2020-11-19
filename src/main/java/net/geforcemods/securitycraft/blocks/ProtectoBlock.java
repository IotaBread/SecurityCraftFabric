package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ProtectoBlock extends OwnableBlock implements BlockEntityProvider {

	public static final BooleanProperty ACTIVATED = Properties.ENABLED;
	public static final VoxelShape SHAPE = VoxelShapes.union(Block.createCuboidShape(0, 0, 5, 16, 16, 11), Block.createCuboidShape(5, 0, 0, 11, 16, 16));

	public ProtectoBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(ACTIVATED, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), Direction.UP);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(ACTIVATED, false);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(ACTIVATED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ProtectoTileEntity().attacks(LivingEntity.class, 10, 200);
	}

}
