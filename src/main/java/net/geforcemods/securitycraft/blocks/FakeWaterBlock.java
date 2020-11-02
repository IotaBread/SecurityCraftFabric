package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class FakeWaterBlock extends FluidBlock
{
	public FakeWaterBlock(Supplier<? extends FlowableFluid> fluid, Settings settings)
	{
		super(fluid.get(), settings);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isClient && !(entity instanceof ItemEntity) && !(entity instanceof BoatEntity))
		{
			if(!(entity instanceof PlayerEntity) || (!((PlayerEntity) entity).isCreative() && !(((PlayerEntity)entity).getVehicle() instanceof BoatEntity)))
				entity.damage(CustomDamageSources.FAKE_WATER, 1.5F);
		}
	}
}
