package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceGateBlock extends FenceGateBlock implements IIntersectable {

	public ReinforcedFenceGateBlock(Settings settings){
		super(settings);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		return ActionResult.FAIL;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(BlockUtils.getBlockProperty(world, pos, OPEN))
			return;

		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)entity;

			if(((OwnableTileEntity)world.getBlockEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(!world.isClient && entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningEntity lightning = WorldUtils.createLightning(world, Vec3d.ofBottomCenter(pos), true);

			creeper.onStruckByLightning((ServerWorld)world, lightning);
			return;
		}

		entity.damage(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!world.isClient) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

			if (isPoweredSCBlock || block.getDefaultState().emitsRedstonePower())
				if (isPoweredSCBlock && !state.get(OPEN) && !state.get(POWERED)) {
					world.setBlockState(pos, state.with(OPEN, true).with(POWERED, true), 2);
					world.syncWorldEvent(null, 1008, pos, 0);
				}
				else if (!isPoweredSCBlock && state.get(OPEN) && state.get(POWERED)) {
					world.setBlockState(pos, state.with(OPEN, false).with(POWERED, false), 2);
					world.syncWorldEvent(null, 1014, pos, 0);
				}
				else if (isPoweredSCBlock != state.get(POWERED))
					world.setBlockState(pos, state.with(POWERED, isPoweredSCBlock), 2);
		}
	}

	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int par5, int par6){
		super.onSyncedBlockEvent(state, world, pos, par5, par6);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity != null ? tileentity.onSyncedBlockEvent(par5, par6) : false;
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new SecurityCraftTileEntity().intersectsEntities();
//	}

}
