package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class KeypadFurnaceBlock extends OwnableBlock implements IPasswordConvertible, BlockEntityProvider {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final BooleanProperty LIT = Properties.LIT;
	private static final VoxelShape NORTH_OPEN = VoxelShapes.combine(VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 3, 16, 16, 16), Block.createCuboidShape(1, 1, 2, 15, 2, 3)), VoxelShapes.combine(Block.createCuboidShape(4, 1, 0, 12, 2, 2), Block.createCuboidShape(5, 1, 1, 11, 2, 2), BooleanBiFunction.ONLY_FIRST)), Block.createCuboidShape(1, 2, 3, 15, 15, 4), BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape NORTH_CLOSED = VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 3, 16, 16, 16), Block.createCuboidShape(1, 1, 2, 15, 15, 3)), VoxelShapes.combine(Block.createCuboidShape(4, 14, 0, 12, 15, 2), Block.createCuboidShape(5, 14, 1, 11, 15, 2), BooleanBiFunction.ONLY_FIRST));
	private static final VoxelShape EAST_OPEN = VoxelShapes.combine(VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 13, 16, 16), Block.createCuboidShape(13, 1, 1, 14, 2, 15)), VoxelShapes.combine(Block.createCuboidShape(14, 1, 4, 16, 2, 12), Block.createCuboidShape(14, 1, 5, 15, 2, 11), BooleanBiFunction.ONLY_FIRST)), Block.createCuboidShape(12, 2, 1, 13, 15, 15), BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape EAST_CLOSED = VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 13, 16, 16), Block.createCuboidShape(13, 1, 1, 14, 15, 15)), VoxelShapes.combine(Block.createCuboidShape(14, 14, 4, 16, 15, 12), Block.createCuboidShape(14, 14, 5, 15, 15, 11), BooleanBiFunction.ONLY_FIRST));
	private static final VoxelShape SOUTH_OPEN = VoxelShapes.combine(VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 16, 13), Block.createCuboidShape(1, 1, 13, 15, 2, 14)), VoxelShapes.combine(Block.createCuboidShape(4, 1, 14, 12, 2, 16), Block.createCuboidShape(5, 1, 14, 11, 2, 15), BooleanBiFunction.ONLY_FIRST)), Block.createCuboidShape(1, 2, 12, 15, 15, 13), BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape SOUTH_CLOSED = VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 16, 13), Block.createCuboidShape(1, 1, 13, 15, 15, 14)), VoxelShapes.combine(Block.createCuboidShape(4, 14, 14, 12, 15, 16), Block.createCuboidShape(5, 14, 14, 11, 15, 15), BooleanBiFunction.ONLY_FIRST));
	private static final VoxelShape WEST_OPEN = VoxelShapes.combine(VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(3, 0, 0, 16, 16, 16), Block.createCuboidShape(2, 1, 1, 3, 2, 15)), VoxelShapes.combine(Block.createCuboidShape(0, 1, 4, 2, 2, 12), Block.createCuboidShape(1, 1, 5, 2, 2, 11), BooleanBiFunction.ONLY_FIRST)), Block.createCuboidShape(3, 2, 1, 4, 15, 15), BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape WEST_CLOSED = VoxelShapes.union(VoxelShapes.union(Block.createCuboidShape(3, 0, 0, 16, 16, 16), Block.createCuboidShape(2, 1, 1, 3, 15, 15)), VoxelShapes.combine(Block.createCuboidShape(0, 14, 4, 2, 15, 12), Block.createCuboidShape(1, 14, 5, 2, 15, 11), BooleanBiFunction.ONLY_FIRST));

	public KeypadFurnaceBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(LIT, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		switch(state.get(FACING))
		{
			case NORTH:
				if(state.get(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if(state.get(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if(state.get(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if(state.get(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default: return VoxelShapes.fullCube();
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(!(newState.getBlock() instanceof KeypadFurnaceBlock))
		{
			BlockEntity tileentity = world.getBlockEntity(pos);

			if (tileentity instanceof Inventory)
			{
				ItemScatterer.spawn(world, pos, (Inventory)tileentity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient)
		{
			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.BLACKLIST))
				return ActionResult.FAIL;
			else if(ModuleUtils.checkForModule(world, pos, player, ModuleType.WHITELIST))
				activate(world, pos, player);
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER))
				((KeypadFurnaceTileEntity) world.getBlockEntity(pos)).openPasswordGUI(player);
		}

		return ActionResult.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		if(!BlockUtils.getBlockProperty(world, pos, KeypadFurnaceBlock.OPEN))
			BlockUtils.setBlockProperty(world, pos, KeypadFurnaceBlock.OPEN, true);

		if(player instanceof ServerPlayerEntity)
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof NamedScreenHandlerFactory)
			{
				world.syncWorldEvent((PlayerEntity)null, 1006, pos, 0);
//				NetworkHooks.openGui((ServerPlayerEntity)player, (NamedScreenHandlerFactory)te, pos); // TODO
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(OPEN, false).with(LIT, false);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(state.get(OPEN) && state.get(LIT))
		{
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;

			if(rand.nextDouble() < 0.1D)
				world.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

			Direction direction = state.get(FACING);
			Axis axis = direction.getAxis();
			double randomNumber = rand.nextDouble() * 0.6D - 0.3D;
			double xOffset = axis == Axis.X ? direction.getOffsetX() * 0.52D : randomNumber;
			double yOffset = rand.nextDouble() * 6.0D / 16.0D;
			double zOffset = axis == Axis.Z ? direction.getOffsetZ() * 0.52D : randomNumber;

			world.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, OPEN, LIT);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new KeypadFurnaceTileEntity();
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.FURNACE;
	}

	@Override
	public boolean convert(PlayerEntity player, World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		Direction facing = state.get(FACING);
		boolean lit = state.get(LIT);
		FurnaceBlockEntity furnace = (FurnaceBlockEntity)world.getBlockEntity(pos);
		CompoundTag tag = furnace.toTag(new CompoundTag());

		furnace.clear();
		world.setBlockState(pos, SCContent.KEYPAD_FURNACE.getDefaultState().with(FACING, facing).with(OPEN, false).with(LIT, lit));
		((IOwnable) world.getBlockEntity(pos)).getOwner().set(player.getUuid().toString(), player.getName().getString());
		((KeypadFurnaceTileEntity)world.getBlockEntity(pos)).fromTag(world.getBlockState(pos), tag);
		return true;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror)
	{
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
