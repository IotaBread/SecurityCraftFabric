package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScannerDoorItem extends Item
{
	public ScannerDoorItem(Settings properties)
	{
		super(properties);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		return useOnBlock(ctx.getPlayer(), ctx.getWorld(), ctx.getBlockPos(), ctx.getStack(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx);
	}

	public ActionResult useOnBlock(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, ItemUsageContext ctx)
	{
		if(world.isClient)
			return ActionResult.FAIL;

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.canReplace(world.getBlockState(pos), new ItemPlacementContext(ctx)))
			pos = pos.offset(facing);

		if (player.canPlaceOn(pos, facing, stack) && BlockUtils.isSideSolid(world, pos.down(), Direction.UP))
		{
			Direction angleFacing = Direction.fromRotation(player.yaw);
			int offsetX = angleFacing.getOffsetX();
			int offsetZ = angleFacing.getOffsetZ();
			boolean flag = offsetX < 0 && hitZ < 0.5F || offsetX > 0 && hitZ > 0.5F || offsetZ < 0 && hitX > 0.5F || offsetZ > 0 && hitX < 0.5F;

			if(!placeDoor(world, pos, angleFacing, SCContent.SCANNER_DOOR, flag))
				return ActionResult.FAIL;

			BlockSoundGroup soundtype = world.getBlockState(pos).getBlock().getSoundGroup(world.getBlockState(pos));

			world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

			if(!player.isCreative())
				stack.decrement(1);

			if(world.getBlockEntity(pos) != null)
			{
				CustomizableTileEntity lowerTe = ((CustomizableTileEntity) world.getBlockEntity(pos));
				CustomizableTileEntity upperTe = ((CustomizableTileEntity) world.getBlockEntity(pos.up()));

				lowerTe.getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				upperTe.getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				CustomizableTileEntity.link(lowerTe, upperTe);
			}

			return ActionResult.SUCCESS;
		}
		else
			return ActionResult.FAIL;
	}

	public boolean placeDoor(World world, BlockPos pos, Direction facing, Block door, boolean isRightHinge) //naming might not be entirely correct, but it's giving a rough idea
	{
		BlockPos posAbove = pos.up();

		if(!world.getBlockState(posAbove).isAir())
			return false;

		BlockPos left = pos.offset(facing.rotateYClockwise());
		BlockPos right = pos.offset(facing.rotateYCounterclockwise());
		int rightNormalCubeAmount = (world.getBlockState(right).isSolidBlock(world, pos) ? 1 : 0) + (world.getBlockState(right.up()).isSolidBlock(world, pos) ? 1 : 0);
		int leftNormalCubeAmount = (world.getBlockState(left).isSolidBlock(world, pos) ? 1 : 0) + (world.getBlockState(left.up()).isSolidBlock(world, pos) ? 1 : 0);
		boolean isRightDoor = world.getBlockState(right).getBlock() == door || world.getBlockState(right.up()).getBlock() == door;
		boolean isLeftDoor = world.getBlockState(left).getBlock() == door || world.getBlockState(left.up()).getBlock() == door;

		if ((!isRightDoor || isLeftDoor) && leftNormalCubeAmount <= rightNormalCubeAmount)
		{
			if (isLeftDoor && !isRightDoor || leftNormalCubeAmount < rightNormalCubeAmount)
				isRightHinge = false;
		}
		else
			isRightHinge = true;

		boolean isAnyPowered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(posAbove);
		BlockState state = door.getDefaultState().with(DoorBlock.FACING, facing).with(DoorBlock.HINGE, isRightHinge ? DoorHinge.RIGHT : DoorHinge.LEFT).with(DoorBlock.POWERED, isAnyPowered).with(DoorBlock.OPEN, isAnyPowered);

		world.setBlockState(pos, state.with(DoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
		world.setBlockState(posAbove, state.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
		world.updateNeighborsAlways(pos, door);
		world.updateNeighborsAlways(posAbove, door);
		return true;
	}
}
