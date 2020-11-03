package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IIntersectable;
//import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
//import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
//import net.minecraftforge.registries.ForgeRegistries;

public class BouncingBettyBlock extends ExplosiveBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.of("deactivated");
	private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 3, 13);

	public BouncingBettyBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		return SHAPE;
	}

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
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.down(), Direction.UP);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!EntityUtils.doesEntityOwn(entity, world, pos))
			if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity))
				explode(world, pos);
	}
	@Override
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player){
		if(!player.isCreative() && !EntityUtils.doesPlayerOwn(player, world, pos))
			explode(world, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos){
		if(world.isClient)
			return;
		if(BlockUtils.getBlockProperty(world, pos, DEACTIVATED))
			return;

		world.breakBlock(pos, false);
		BouncingBettyEntity entitytntprimed = new BouncingBettyEntity(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
		entitytntprimed.fuse = 15;
		entitytntprimed.setVelocity(entitytntprimed.getVelocity().multiply(1, 0, 1).add(0, 0.5D, 0));
		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(entitytntprimed));
		entitytntprimed.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getPickStack(BlockView worldIn, BlockPos pos, BlockState state){
		return new ItemStack(asItem());
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
//		return new SecurityCraftTileEntity().intersectsEntities();
//	}

}
