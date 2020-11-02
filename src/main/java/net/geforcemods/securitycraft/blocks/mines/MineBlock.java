package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class MineBlock extends ExplosiveBlock {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.of("deactivated");
	private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 3, 11);

	public MineBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(DEACTIVATED, false));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else if (world.getBlockState(pos).get(DEACTIVATED))
			world.breakBlock(pos, true);
		else
			explode(world, pos);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
	 */
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		Material mat = world.getBlockState(pos.down()).getMaterial();

		return !(mat == Material.GLASS || mat == Material.CACTUS || mat == Material.AIR || mat == Material.CAKE || mat == Material.PLANT);
	}

//	@Override // Forge method
//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
//		if(!world.isClient)
//			if(player != null && player.isCreative() && !ConfigHandler.CONFIG.mineExplodesWhenInCreative.get())
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
//				explode(world, pos);
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			}
//
//		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		return SHAPE;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
		if(world.isClient)
			return;
		else if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity) && !EntityUtils.doesEntityOwn(entity, world, pos))
			explode(world, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isClient)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isClient)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(world.isClient)
			return;

		if(!world.getBlockState(pos).get(DEACTIVATED)){
			world.breakBlock(pos, false);
			if(ConfigHandler.CONFIG.smallerMineExplosion)
				world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 1.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
			else
				world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
		}
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getPickStack(BlockView worldIn, BlockPos pos, BlockState state){
		return new ItemStack(SCContent.MINE.asItem());
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new OwnableTileEntity();
//	}

}
