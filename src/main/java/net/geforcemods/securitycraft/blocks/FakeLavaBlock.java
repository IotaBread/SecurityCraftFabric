package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class FakeLavaBlock extends FluidBlock
{
	private static final StatusEffectInstance SHORT_FIRE_RESISTANCE = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1);

	public FakeLavaBlock(Supplier<? extends FlowableFluid> fluid, Settings settings)
	{
		super(fluid.get(), settings);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		super.onEntityCollision(state, world, pos, entity);

		if(entity instanceof LivingEntity)
		{
			LivingEntity lEntity = (LivingEntity)entity;

			lEntity.extinguish();

			if(!world.isClient)
			{
				lEntity.addStatusEffect(SHORT_FIRE_RESISTANCE);

				if(!lEntity.hasStatusEffect(StatusEffects.REGENERATION))
					lEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 2));
			}
		}
	}
}
