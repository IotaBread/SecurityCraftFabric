package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
//import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowerFeature;
//import net.minecraftforge.common.IPlantable;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;

public class ReinforcedSnowyDirtBlock extends SnowyBlock implements IReinforcedBlock, Fertilizable, BlockEntityProvider
{
	private Block vanillaBlock;

	public ReinforcedSnowyDirtBlock(Settings settings, Block vB)
	{
		super(settings);
		this.vanillaBlock = vB;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState facingState, WorldAccess world, BlockPos currentPos, BlockPos facingPos)
	{
		if(facing != Direction.UP)
			return super.getStateForNeighborUpdate(state, facing, facingState, world, currentPos, facingPos);
		else
		{
			Block block = facingState.getBlock();
			return state.with(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Block block = ctx.getWorld().getBlockState(ctx.getBlockPos().up()).getBlock();
		return getDefaultState().with(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(this == SCContent.REINFORCED_MYCELIUM)
		{
			super.randomDisplayTick(state, world, pos, rand);

			if(rand.nextInt(10) == 0)
				world.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}

//	@Override // Forge method
//	public boolean canSustainPlant(BlockState state, BlockView world, BlockPos pos, Direction facing, IPlantable plantable)
//	{
//		PlantType type = plantable.getPlantType(world, pos.offset(facing));
//
//		if(type == PlantType.CAVE)
//			return state.isSideSolidFullSquare(world, pos, Direction.UP);
//		else if(type == PlantType.PLAINS)
//			return true;
//		else if(type == PlantType.BEACH)
//		{
//			boolean isBeach = isIn(SCTags.Blocks.REINFORCED_DIRT) || this == SCContent.REINFORCED_SAND || this == SCContent.REINFORCED_RED_SAND;
//			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
//					world.getBlockState(pos.south()).getMaterial() == Material.WATER);
//			return isBeach && hasWater;
//		}
//
//		return false;
//	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient)
	{
		return this == SCContent.REINFORCED_GRASS_BLOCK && world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random rand, BlockPos pos, BlockState state)
	{
		return this == SCContent.REINFORCED_GRASS_BLOCK;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		BlockPos posAbove = pos.up();
		BlockState grass = Blocks.GRASS.getDefaultState();

		for(int i = 0; i < 128; ++i)
		{
			BlockPos tempPos = posAbove;
			int j = 0;

			while(true)
			{
				if(j >= i / 16)
				{
					BlockState tempState = world.getBlockState(tempPos);

					if(tempState.getBlock() == grass.getBlock() && rand.nextInt(10) == 0)
						((Fertilizable)grass.getBlock()).grow(world, rand, tempPos, tempState);

					if(!tempState.isAir())
						break;

					BlockState placeState;

					if(rand.nextInt(8) == 0)
					{
						List<ConfiguredFeature<?, ?>> flowers = world.getBiome(tempPos).getGenerationSettings().getFlowerFeatures();

						if(flowers.isEmpty())
							break;

						ConfiguredFeature<?, ?> configuredfeature = flowers.get(0);
						FlowerFeature flowersfeature = (FlowerFeature)configuredfeature.feature;

						placeState = flowersfeature.getFlowerState(rand, tempPos, configuredfeature.getConfig());
					}
					else
						placeState = grass;

					if(placeState.canPlaceAt(world, tempPos))
						world.setBlockState(tempPos, placeState, 3);

					break;
				}

				tempPos = tempPos.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if(world.getBlockState(tempPos.down()).getBlock() != this || world.getBlockState(tempPos).isFullCube(world, tempPos))
					break;

				++j;
			}
		}
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(SNOWY, vanillaState.get(SNOWY));
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

	@Override
	public BlockEntity createBlockEntity(BlockView world)
	{
		return new OwnableTileEntity();
	}
}

