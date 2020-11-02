package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class DisguisableBlock extends OwnableBlock implements IOverlayDisplay
{
	public DisguisableBlock(Settings settings)
	{
		super(settings);
	}

	public static boolean isNormalCube(BlockState state, BlockView world, BlockPos pos)
	{
		if(state.getBlock() instanceof DisguisableBlock) //should not happen, but just to be safe
		{
			BlockState disguisedState = ((DisguisableBlock)state.getBlock()).getDisguisedStateOrDefault(state, world, pos);

			if(disguisedState.getBlock() != state.getBlock())
				return disguisedState.isSolidBlock(world, pos);
		}

		return state.getMaterial().blocksLight() && state.isFullCube(world, pos);
	}

	public static boolean isSuffocating(BlockState state, BlockView world, BlockPos pos)
	{
		if(state.getBlock() instanceof DisguisableBlock) //should not happen, but just to be safe
		{
			BlockState disguisedState = ((DisguisableBlock)state.getBlock()).getDisguisedStateOrDefault(state, world, pos);

			if(disguisedState.getBlock() != state.getBlock())
				return disguisedState.shouldSuffocate(world, pos);
		}

		return state.getMaterial().blocksMovement() && state.isFullCube(world, pos);
	}

//	@Override // Forge method
//	public BlockSoundGroup getSoundGroup(BlockState state, WorldView world, BlockPos pos, Entity entity)
//	{
//		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);
//
//		if(disguisedState.getBlock() != this)
//			return disguisedState.getSoundType(world, pos, entity);
//		else return super.getSoundType(state, world, pos, entity);
//	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getOutlineShape(world, pos, ctx);
		else return super.getOutlineShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getCollisionShape(world, pos, ctx);
		else return super.getCollisionShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getSidesShape(world, pos);
		else return super.getCullingShape(state, world, pos);
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getAmbientOcclusionLightLevel(world, pos);
		else return super.getAmbientOcclusionLightLevel(state, world, pos);
	}

	public final BlockState getDisguisedStateOrDefault(BlockState state, BlockView world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	public BlockState getDisguisedBlockState(BlockView world, BlockPos pos)
	{
		if(world.getBlockEntity(pos) instanceof DisguisableTileEntity)
		{
			DisguisableTileEntity te = (DisguisableTileEntity) world.getBlockEntity(pos);
			ItemStack module = te.hasModule(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if(!module.isEmpty() && !((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).isEmpty())
				return ((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).get(0).getDefaultState();
		}

		return null;
	}

	public ItemStack getDisguisedStack(BlockView world, BlockPos pos)
	{
		if(world != null && world.getBlockEntity(pos) instanceof DisguisableTileEntity)
		{
			DisguisableTileEntity te = (DisguisableTileEntity) world.getBlockEntity(pos);
			ItemStack stack = te.hasModule(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if(!stack.isEmpty() && !((ModuleItem) stack.getItem()).getBlockAddons(stack.getTag()).isEmpty())
			{
				ItemStack disguisedStack = ((ModuleItem) stack.getItem()).getAddons(stack.getTag()).get(0);

				if(Block.getBlockFromItem(disguisedStack.getItem()) != this)
					return disguisedStack;
			}
		}

		return new ItemStack(this);
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos)
	{
		return getDisguisedStack(world, pos);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos)
	{
		return getDisguisedStack(world, pos).getItem() == asItem();
	}

//	@Override // Forge method
//	public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos, PlayerEntity player)
//	{
//		return getDisguisedStack(world, pos);
//	}
}
