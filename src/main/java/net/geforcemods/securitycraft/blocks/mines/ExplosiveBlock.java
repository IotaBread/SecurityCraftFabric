package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {

	public ExplosiveBlock(Settings settings) {
		super(settings);
	}

	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines ? -1F : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient){
			if(player.inventory.getMainHandStack().isEmpty() && explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos)) {
				explode(world, pos);
				return ActionResult.SUCCESS;
			}

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

			if(explodesWhenInteractedWith() && isActive(world, pos) && !EntityUtils.doesPlayerOwn(player, world, pos))
			{
				explode(world, pos);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.FAIL;
	}

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public boolean isDefusable(){
		return true;
	}

}
