package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.stream.Stream;

public class TrophySystemBlock extends OwnableBlock implements BlockEntityProvider {

	private static final VoxelShape SHAPE = Stream.of(
			Block.createCuboidShape(6.5, 0, 12, 9.5, 1.5, 15),
			Block.createCuboidShape(5.5, 7, 5.5, 10.5, 11, 10.5),
			Block.createCuboidShape(7, 12, 7, 9, 13, 9),
			Block.createCuboidShape(6.5, 12.5, 6.5, 9.5, 15, 9.5),
			Block.createCuboidShape(7, 14.5, 7, 9, 15.5, 9),
			Block.createCuboidShape(7.25, 9, 7.25, 8.75, 12, 8.75),
			Block.createCuboidShape(1, 0, 6.5, 4, 1.5, 9.5),
			Block.createCuboidShape(12, 0, 6.5, 15, 1.5, 9.5),
			Block.createCuboidShape(6.5, 0, 1, 9.5, 1.5, 4)
			).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).orElse(VoxelShapes.fullCube());

	public TrophySystemBlock(Settings settings) {
		super(settings);
	}

	public static boolean isNormalCube(BlockState state, BlockView reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.down(), Direction.UP);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!canPlaceAt(state, world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new TrophySystemTileEntity();
	}

}
