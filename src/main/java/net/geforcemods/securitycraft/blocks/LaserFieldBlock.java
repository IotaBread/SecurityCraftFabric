package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LaserFieldBlock extends OwnableBlock implements IIntersectable, BlockEntityProvider {

	public static final IntProperty BOUNDTYPE = IntProperty.of("boundtype", 1, 3);
	private static final VoxelShape SHAPE_X = Block.createCuboidShape(0, 6.75, 6.75, 16, 9.25, 9.25);
	private static final VoxelShape SHAPE_Y = Block.createCuboidShape(6.75, 0, 6.75, 9.25, 16, 9.25);
	private static final VoxelShape SHAPE_Z = Block.createCuboidShape(6.75, 6.75, 0, 9.25, 9.25, 16);

	public LaserFieldBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(BOUNDTYPE, 1));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		if(!world.isClient && entity instanceof LivingEntity && !EntityUtils.isInvisible((LivingEntity)entity))
		{
			for(Direction facing : Direction.values())
			{
				for(int i = 0; i < ConfigHandler.CONFIG.laserBlockRange; i++)
				{
					BlockPos offsetPos = pos.offset(facing, i);
					Block block = world.getBlockState(offsetPos).getBlock();

					if(block == SCContent.LASER_BLOCK && !BlockUtils.getBlockProperty(world, offsetPos, LaserBlock.POWERED))
					{
						BlockEntity te = world.getBlockEntity(offsetPos);

						if(te instanceof IModuleInventory && ((IModuleInventory)te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, offsetPos, ModuleType.WHITELIST).contains(((LivingEntity) entity).getName().getString().toLowerCase()))
							return;

						BlockUtils.setBlockProperty(world, offsetPos, LaserBlock.POWERED, true, true);
						world.updateNeighborsAlways(offsetPos, SCContent.LASER_BLOCK);
						world.getBlockTickScheduler().schedule(offsetPos, SCContent.LASER_BLOCK, 50);

						if(te instanceof IModuleInventory && ((IModuleInventory)te).hasModule(ModuleType.HARMING))
						{
							if(!(entity instanceof PlayerEntity && ((IOwnable)te).getOwner().isOwner((PlayerEntity)entity)))
								((LivingEntity) entity).damage(CustomDamageSources.LASER, 10F);
						}
					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, pos, state
	 */
	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state)
	{
		if(!world.isClient())
		{
			Direction[] facingArray = {Direction.byId((state.get(LaserFieldBlock.BOUNDTYPE) - 1) * 2), Direction.byId((state.get(LaserFieldBlock.BOUNDTYPE) - 1) * 2).getOpposite()};

			for(Direction facing : facingArray)
			{
				for(int i = 0; i < ConfigHandler.CONFIG.laserBlockRange; i++)
				{
					if(BlockUtils.getBlock(world, pos.offset(facing, i)) == SCContent.LASER_BLOCK)
					{
						for(int j = 1; j < i; j++)
						{
							world.breakBlock(pos.offset(facing, j), false);
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		if(source.getBlockState(pos).getBlock() instanceof LaserFieldBlock)
		{
			int boundType = source.getBlockState(pos).get(BOUNDTYPE);

			if (boundType == 1)
				return SHAPE_Y;
			else if (boundType == 2)
				return SHAPE_Z;
			else if (boundType == 3)
				return SHAPE_X;
		}

		return VoxelShapes.empty();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(BOUNDTYPE, 1);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(BOUNDTYPE);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SecurityCraftTileEntity().intersectsEntities();
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot)
	{
		int boundType = state.get(BOUNDTYPE);

		return rot == BlockRotation.CLOCKWISE_180 ? state : state.with(BOUNDTYPE, boundType == 2 ? 3 : (boundType == 3 ? 2 : 1));
	}
}
