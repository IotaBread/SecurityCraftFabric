package net.geforcemods.securitycraft.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

public class LaserBlock extends DisguisableBlock {

	public static final BooleanProperty POWERED = Properties.POWERED;

	public LaserBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(POWERED, false));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onPlaced(world, pos, state, entity, stack);

		if(!world.isClient)
			setLaser(world, pos);
	}

	public void setLaser(World world, BlockPos pos)
	{
		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			inner: for(int i = 1; i <= ConfigHandler.CONFIG.laserBlockRange; i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				BlockState offsetState = world.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();

				if(!offsetState.isAir(/*world, offsetPos*/) && offsetBlock != SCContent.LASER_BLOCK) // Block#isAir(World, BlockPos) is a forge method, TODO
					break inner;
				else if(offsetBlock == SCContent.LASER_BLOCK)
				{
					LaserBlockTileEntity thisTe = (LaserBlockTileEntity)world.getBlockEntity(pos);
					LaserBlockTileEntity thatTe = (LaserBlockTileEntity)world.getBlockEntity(offsetPos);

					if(thisTe.getOwner().equals(thatTe.getOwner()))
					{
						CustomizableTileEntity.link(thisTe, thatTe);

						if (thisTe.isEnabled() && thatTe.isEnabled())
						{
							for(int j = 1; j < i; j++)
							{
								offsetPos = pos.offset(facing, j);

								if(world.getBlockState(offsetPos).isAir(/*world, offsetPos*/)) // Forge method, TODO
									world.setBlockState(offsetPos, SCContent.LASER_FIELD.getDefaultState().with(LaserFieldBlock.BOUNDTYPE, boundType));
							}
						}
					}
					break inner;
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		if(!world.isClient())
			destroyAdjacentLasers(world, pos);
	}

	public static void destroyAdjacentLasers(WorldAccess world, BlockPos pos)
	{
		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			for(int i = 1; i <= ConfigHandler.CONFIG.laserBlockRange; i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				BlockState state = world.getBlockState(offsetPos);

				if(state.getBlock() == SCContent.LASER_BLOCK)
					break;
				else if(state.getBlock() == SCContent.LASER_FIELD && state.get(LaserFieldBlock.BOUNDTYPE) == boundType)
					world.breakBlock(offsetPos, false);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		setLaser(world, pos);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state){
		return true;
	}

//	@Override // Forge method
//	public boolean shouldCheckWeakPower(BlockState state, WorldView world, BlockPos pos, Direction side)
//	{
//		return false;
//	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (!world.isClient && state.get(POWERED))
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand){
		if((state.get(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float f1 = 0.6F + 0.4F;
			float f2 = Math.max(0.0F, 0.7F - 0.5F);
			float f3 = Math.max(0.0F, 0.6F - 0.7F);

			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new LaserBlockTileEntity().linkable();
//	}
}
