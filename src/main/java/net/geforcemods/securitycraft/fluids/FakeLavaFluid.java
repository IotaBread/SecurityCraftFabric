package net.geforcemods.securitycraft.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
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
import net.minecraft.world.*;
//import net.minecraftforge.fluids.FluidAttributes;

import org.jetbrains.annotations.Nullable;
import java.util.Random;

public abstract class FakeLavaFluid extends FlowableFluid
{
	@Override
	public Fluid getFlowing()
	{
		return SCContent.FLOWING_FAKE_LAVA;
	}

	@Override
	public Fluid getStill()
	{
		return SCContent.FAKE_LAVA;
	}

	@Override
	public Item getBucketItem()
	{
		return SCContent.FAKE_LAVA_BUCKET;
	}

//	@Override
//	protected FluidAttributes createAttributes()
//	{
//		return FluidAttributes.builder(
//				new Identifier("block/lava_still"),
//				new Identifier("block/lava_flow"))
//				.translationKey("block.minecraft.lava")
//				.luminosity(15).density(3000).viscosity(6000).temperature(1300).build(this);
//	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random)
	{
		BlockPos blockpos = pos.up();

		if(world.getBlockState(blockpos).isAir() && !world.getBlockState(blockpos).isOpaqueFullCube(world, blockpos))
		{
			if(random.nextInt(100) == 0)
			{
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + 1;
				double z = pos.getZ() + random.nextFloat();

				world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
				world.playSound(x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if(random.nextInt(200) == 0)
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}

	}

	@Override
	public void onRandomTick(World world, BlockPos pos, FluidState state, Random random)
	{
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK))
		{
			int i = random.nextInt(3);

			if(i > 0)
			{
				BlockPos blockpos = pos;

				for(int j = 0; j < i; ++j)
				{
					blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);

					if(!world.canSetBlock(blockpos))
						return;

					BlockState blockState = world.getBlockState(blockpos);

					if(blockState.isAir())
					{
						if(isSurroundingBlockFlammable(world, blockpos))
						{
							world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
							return;
						}
					}
					else if(blockState.getMaterial().blocksMovement())
						return;
				}
			}
			else
			{
				for(int k = 0; k < 3; ++k)
				{
					BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

					if(!world.canSetBlock(blockpos1))
						return;

					if(world.isAir(blockpos1.up()) && this.getCanBlockBurn(world, blockpos1))
						world.setBlockState(blockpos1.up(), Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	private boolean isSurroundingBlockFlammable(World world, BlockPos pos)
	{
		for(Direction Direction : Direction.values())
		{
			if(this.getCanBlockBurn(world, pos.offset(Direction)))
				return true;
		}

		return false;
	}

	private boolean getCanBlockBurn(World world, BlockPos pos)
	{
		return !world.canSetBlock(pos) ? false : world.getBlockState(pos).getMaterial().isBurnable();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleEffect getParticle()
	{
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)
	{
		triggerEffects(world, pos);
	}

	@Override
	public int getFlowSpeed(WorldView world)
	{
		return world.getDimension().isUltrawarm() ? 4 : 2;
	}

	@Override
	public BlockState toBlockState(FluidState state)
	{
		return SCContent.FAKE_LAVA_BLOCK.getDefaultState().with(FluidBlock.LEVEL, method_15741(state));
	}

	@Override
	public boolean matchesType(Fluid fluid)
	{
		return fluid == SCContent.FAKE_LAVA || fluid == SCContent.FLOWING_FAKE_LAVA;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world)
	{
		return world.getDimension().isUltrawarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockView world, BlockPos pos, Fluid fluid, Direction dir)
	{
		return fluidState.getHeight(world, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
	}

	@Override
	public int getTickRate(WorldView world)
	{
		return world.getDimension().isUltrawarm() ? 10 : 30;
	}

	@Override
	public int getNextTickDelay(World world, BlockPos pos, FluidState fluidState1, FluidState fluidState2)
	{
		int i = getTickRate(world);

		if(!fluidState1.isEmpty() && !fluidState2.isEmpty() && !fluidState1.get(FALLING) && !fluidState2.get(FALLING) && fluidState2.getHeight(world, pos) > fluidState1.getHeight(world, pos) && world.getRandom().nextInt(4) != 0)
			i *= 4;

		return i;
	}

	protected void triggerEffects(WorldAccess world, BlockPos pos)
	{
		world.syncWorldEvent(1501, pos, 0);
	}

	@Override
	protected boolean isInfinite()
	{
		return false;
	}

	@Override
	protected void flow(WorldAccess world, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState)
	{
		if(direction == Direction.DOWN)
		{
			FluidState ifluidstate = world.getFluidState(pos);

			if(isIn(FluidTags.LAVA) && ifluidstate.isIn(FluidTags.WATER))
			{
				if(blockState.getBlock() instanceof FluidBlock)
					world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);

				triggerEffects(world, pos);
				return;
			}
		}

		super.flow(world, pos, blockState, direction, fluidState);
	}

	@Override
	protected boolean hasRandomTicks()
	{
		return true;
	}

	@Override
	protected float getBlastResistance()
	{
		return 100.0F;
	}

	public static class Flowing extends FakeLavaFluid
	{
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
		{
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state)
		{
			return state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state)
		{
			return false;
		}
	}

	public static class Source extends FakeLavaFluid
	{
		@Override
		public int getLevel(FluidState state)
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