package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ReinforcedCobwebBlock extends BaseReinforcedBlock
{
	public ReinforcedCobwebBlock(Settings properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(entity instanceof PlayerEntity)
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof OwnableTileEntity)
			{
				if(((OwnableTileEntity)te).getOwner().isOwner((PlayerEntity)entity))
					return;
			}
		}

		entity.slowMovement(state, new Vec3d(0.25D, 0.05D, 0.25D));
	}
}
