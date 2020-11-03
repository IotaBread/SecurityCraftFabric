package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.compat.fabric.FabricEntityShapeContext;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;

public class BaseFullMineBlock extends ExplosiveBlock implements IIntersectable, IOverlayDisplay, IBlockMine {

	private final Block blockDisguisedAs;

	public BaseFullMineBlock(Settings settings, Block disguisedBlock) {
		super(settings);
		blockDisguisedAs = disguisedBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		if(ctx instanceof EntityShapeContext)
		{
			Entity entity = ((FabricEntityShapeContext)ctx).getEntity();

			if(entity instanceof ItemEntity)
				return VoxelShapes.fullCube();
			else if(entity instanceof PlayerEntity)
			{
				BlockEntity te = world.getBlockEntity(pos);

				if(te instanceof OwnableTileEntity)
				{
					OwnableTileEntity ownableTe = (OwnableTileEntity) te;

					if(ownableTe.getOwner().isOwner((PlayerEntity)entity))
						return VoxelShapes.fullCube();
				}
			}
		}

		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity){
		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof LivingEntity && !PlayerUtils.isPlayerMountedOnCamera((LivingEntity)entity) && !EntityUtils.doesEntityOwn(entity, world, pos))
			explode(world, pos);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion){
		if (!world.isClient)
		{
//			if(pos.equals(new BlockPos(explosion.getPosition()))) // TODO
//				return;

			explode(world, pos);
		}
	}

//	@Override // Forge method
//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
//		if(!world.isClient)
//			if(player != null && player.isCreative() && !ConfigHandler.CONFIG.mineExplodesWhenInCreative)
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			else if(!EntityUtils.doesPlayerOwn(player, world, pos)){
//				explode(world, pos);
//				return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//			}
//
//		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
//	}

	@Override
	public void activateMine(World world, BlockPos pos) {}

	@Override
	public void defuseMine(World world, BlockPos pos) {}

	@Override
	public void explode(World world, BlockPos pos) {
		world.breakBlock(pos, false);

		if(ConfigHandler.CONFIG.smallerMineExplosion)
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 2.5F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
		else
			world.createExplosion((Entity)null, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 5.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
	}

	/**
	 * Return whether this block can drop from an explosion.
	 */
	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion){
		return false;
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public boolean isDefusable() {
		return false;
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new SecurityCraftTileEntity().intersectsEntities();
//	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos) {
		return new ItemStack(blockDisguisedAs);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos) {
		return false;
	}

}