package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
//import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SignType;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.common.MinecraftForge;

public class SecretWallSignBlock extends WallSignBlock
{
	public SecretWallSignBlock(Settings settings, SignType woodType)
	{
		super(settings, woodType);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			OwnershipEvent.EVENT.invoker().own(world, pos, (PlayerEntity) placer);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient && player.getStackInHand(hand).getItem() == SCContent.ADMIN_TOOL)
			return SCContent.ADMIN_TOOL.useOnBlock(new ItemUsageContext(player, hand, hit));

		SecretSignTileEntity te = (SecretSignTileEntity)world.getBlockEntity(pos);

		if (te != null && te.isPlayerAllowedToSeeText(player))
			return super.onUse(state, world, pos, player, hand, hit);

		return ActionResult.FAIL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new SecretSignTileEntity();
	}

//	@Override // TODO
//	public String getTranslationKey() {
//		return Util.createTranslationKey("block", this.getRegistryName()).replace("_wall", "");
//	}
}