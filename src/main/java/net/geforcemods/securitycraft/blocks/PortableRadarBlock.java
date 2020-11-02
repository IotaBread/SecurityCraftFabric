package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
//import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PortableRadarBlock extends OwnableBlock {

	public static final BooleanProperty POWERED = Properties.POWERED;
	private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 7, 11);

	public PortableRadarBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(POWERED, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		return SHAPE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.down(), Direction.UP);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			world.breakBlock(pos, true);
	}

	public static void togglePowerOutput(World world, BlockPos pos, boolean par5) {
		if(par5 && !world.getBlockState(pos).get(POWERED)){
			BlockUtils.setBlockProperty(world, pos, POWERED, true, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}else if(!par5 && world.getBlockState(pos).get(POWERED)){
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
			BlockUtils.updateAndNotify(world, pos, BlockUtils.getBlock(world, pos), 1, false);
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView world, BlockPos pos, Direction side){
		if(blockState.get(POWERED) && ((IModuleInventory) world.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new PortableRadarTileEntity().nameable();
//	}

}
