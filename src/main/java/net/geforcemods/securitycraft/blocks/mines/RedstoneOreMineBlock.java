package net.geforcemods.securitycraft.blocks.mines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class RedstoneOreMineBlock extends BaseFullMineBlock
{
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneOreMineBlock(Settings settings, Block disguisedBlock)
	{
		super(settings, disguisedBlock);

		setDefaultState(getDefaultState().with(LIT, false));
	}

	@Override
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player)
	{
		activate(state, world, pos);
		super.onBlockBreakStart(state, world, pos, player);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity)
	{
		activate(world.getBlockState(pos), world, pos);
		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		ItemStack stack = player.getStackInHand(hand);

		if(world.isClient)
			spawnParticles(world, pos);
		else
			activate(state, world, pos);

		return stack.getItem() instanceof BlockItem && (new ItemPlacementContext(player, hand, stack, hit)).canPlace() ? ActionResult.PASS : ActionResult.SUCCESS;
	}

	private static void activate(BlockState state, World world, BlockPos pos)
	{
		spawnParticles(world, pos);

		if(!state.get(LIT))
			world.setBlockState(pos, state.with(LIT, true), 3);
	}

	@Override
	public boolean hasRandomTicks(BlockState state)
	{
		return state.get(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(state.get(LIT))
			world.setBlockState(pos, state.with(LIT, false), 3);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(state.get(LIT))
			spawnParticles(world, pos);
	}

	private static void spawnParticles(World world, BlockPos pos)
	{
		Random random = world.random;

		for(Direction direction : Direction.values())
		{
			BlockPos offsetPos = pos.offset(direction);

			if(!world.getBlockState(offsetPos).isOpaqueFullCube(world, offsetPos))
			{
				Direction.Axis axis = direction.getAxis();
				double d1 = axis == Direction.Axis.X ? 0.5D + 0.5625D * direction.getOffsetX() : (double)random.nextFloat();
				double d2 = axis == Direction.Axis.Y ? 0.5D + 0.5625D * direction.getOffsetY() : (double)random.nextFloat();
				double d3 = axis == Direction.Axis.Z ? 0.5D + 0.5625D * direction.getOffsetZ() : (double)random.nextFloat();

				world.addParticle(DustParticleEffect.RED, pos.getX() + d1, pos.getY() + d2, pos.getZ() + d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
