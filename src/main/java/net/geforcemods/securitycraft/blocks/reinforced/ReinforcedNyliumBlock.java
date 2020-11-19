package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVinesFeature;

import java.util.Random;

public class ReinforcedNyliumBlock extends BaseReinforcedBlock implements Fertilizable
{
	public ReinforcedNyliumBlock(Settings settings, Block vB) {
		super(settings, vB);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!hasLightAbove(state, world, pos)) {
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
		}
	}

	private static boolean hasLightAbove(BlockState state, WorldView world, BlockPos pos) {
		BlockPos upperPos = pos.up();
		BlockState upperState = world.getBlockState(upperPos);
		int lightLevel = ChunkLightProvider.getRealisticOpacity(world, state, pos, upperState, upperPos, Direction.UP, upperState.getOpacity(world, upperPos));
		return lightLevel < world.getMaxLightLevel();
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean flag) {
		return world.getBlockState(pos.up()).isAir(/*world, pos*/); // TODO
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState blockState) {
		BlockState state = world.getBlockState(pos);
		BlockPos upperPos = pos.up();

		if (state.isOf(SCContent.REINFORCED_CRIMSON_NYLIUM)) {
			NetherForestVegetationFeature.generate(world, random, upperPos, ConfiguredFeatures.Configs.CRIMSON_ROOTS_CONFIG, 3, 1);
		}
		else if (state.isOf(SCContent.REINFORCED_WARPED_NYLIUM)) {
			NetherForestVegetationFeature.generate(world, random, upperPos, ConfiguredFeatures.Configs.WARPED_ROOTS_CONFIG, 3, 1);
			NetherForestVegetationFeature.generate(world, random, upperPos, ConfiguredFeatures.Configs.NETHER_SPROUTS_CONFIG, 3, 1);

			if (random.nextInt(8) == 0) {
				TwistingVinesFeature.method_26265(world, random, upperPos, 3, 1, 2);
			}
		}
	}
}
