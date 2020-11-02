package net.geforcemods.securitycraft.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
//import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
//import net.minecraftforge.fluids.FluidAttributes;

import org.jetbrains.annotations.Nullable;
import java.util.Random;

public abstract class FakeWaterFluid extends FlowableFluid
{
	@Override
	public Fluid getFlowing()
	{
		return SCContent.FLOWING_FAKE_WATER;
	}

	@Override
	public Fluid getStill()
	{
		return SCContent.FAKE_WATER;
	}

	@Override
	public Item getBucketItem()
	{
		return SCContent.FAKE_WATER_BUCKET;
	}

//	@Override // Forge methods, TODO
//	protected FluidAttributes createAttributes()
//	{
//		return FluidAttributes.Water.builder(
//				new Identifier("block/water_still"),
//				new Identifier("block/water_flow"))
//				.overlay(new Identifier("block/water_overlay"))
//				.translationKey("block.minecraft.water")
//				.color(0xFF3F76E4).build(this);
//	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random)
	{
		if(!state.isStill() && !state.get(FALLING))
		{
			if(random.nextInt(64) == 0)
				world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
		}
		else if(random.nextInt(10) == 0)
			world.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleEffect getParticle()
	{
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean isInfinite()
	{
		return true;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
	{
		BlockEntity te = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;

		Block.dropStacks(state, world, pos, te);
	}

	@Override
	public int getFlowSpeed(WorldView world)
	{
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState state)
	{
		return SCContent.FAKE_WATER_BLOCK.getDefaultState().with(FluidBlock.LEVEL, method_15741(state));
	}

	@Override
	public boolean matchesType(Fluid fluid)
	{
		return fluid == SCContent.FAKE_WATER || fluid == SCContent.FLOWING_FAKE_WATER;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world)
	{
		return 1;
	}

	@Override
	public int getTickRate(WorldView world)
	{
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockView world, BlockPos pos, Fluid fluid, Direction dir)
	{
		return dir == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
	}

	@Override
	protected float getBlastResistance()
	{
		return 100.0F;
	}

	public static class Flowing extends FakeWaterFluid
	{
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
		{
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState p_207192_1_)
		{
			return p_207192_1_.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeWaterFluid
	{
		@Override
		public int getLevel(FluidState p_207192_1_)
		{
			return 8;
		}

		@Override
		public boolean isStill(FluidState state)
		{
			return true;
		}
	}
}