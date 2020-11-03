package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
//import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ReinforcedLeverBlock extends LeverBlock implements IReinforcedBlock {

	public ReinforcedLeverBlock(Settings properties)
	{
		super(properties);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult result) {
		if(isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)world.getBlockEntity(pos), player))
			return super.onUse(state, world, pos, player, hand, result);
		return ActionResult.FAIL;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, WhitelistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(entity.getName().asString().toLowerCase());
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.LEVER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(FACE, vanillaState.get(FACE)).with(FACING, vanillaState.get(FACING)).with(POWERED, vanillaState.get(POWERED));
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder)
	{
		return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

//	@Override // Forge method
//	public boolean hasTileEntity(BlockState state)
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new WhitelistOnlyTileEntity();
//	}
}
