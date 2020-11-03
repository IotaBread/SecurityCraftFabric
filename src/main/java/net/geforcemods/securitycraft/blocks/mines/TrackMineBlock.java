package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TrackMineTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
//import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends RailBlock implements IExplosive {

	public TrackMineBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient){
			if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE))
				return ActionResult.SUCCESS;

			if(isActive(world, pos) && isDefusable() && player.getStackInHand(hand).getItem() == SCContent.WIRE_CUTTERS) {
				defuseMine(world, pos);
				player.inventory.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(hand));
				return ActionResult.SUCCESS;
			}

			if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL)) {
				activateMine(world, pos);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines ? -1F : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

//	@Override // Forge method
//	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart){
//		BlockEntity te = world.getBlockEntity(pos);
//
//		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
//		{
//			world.breakBlock(pos, false);
//			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.CONFIG.smallerMineExplosion ? 4.0F : 8.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
//			cart.remove();
//		}
//	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onStateReplaced(state, world, pos, newState, isMoving);
		world.removeBlockEntity(pos);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
		{
			world.breakBlock(pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.up().getY(), pos.getZ(), ConfigHandler.CONFIG.smallerMineExplosion ? 4.0F : 8.0F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
		}
	}

	@Override
	public void activateMine(World world, BlockPos pos)
	{
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && !((TrackMineTileEntity)te).isActive())
			((TrackMineTileEntity)te).activate();
	}

	@Override
	public void defuseMine(World world, BlockPos pos)
	{
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive())
			((TrackMineTileEntity)te).deactivate();
	}

	@Override
	public boolean isActive(World world, BlockPos pos)
	{
		BlockEntity te = world.getBlockEntity(pos);

		return te instanceof TrackMineTileEntity && ((TrackMineTileEntity)te).isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new TrackMineTileEntity();
//	}

}
