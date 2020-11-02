package net.geforcemods.securitycraft.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.compat.fabric.FabricEntityShapeContext;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockPocketWallBlock extends OwnableBlock implements IOverlayDisplay
{
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.of("see_through");
	public static final BooleanProperty SOLID = BooleanProperty.of("solid");

	public BlockPocketWallBlock(Settings settings)
	{
		super(settings);

		setDefaultState(stateManager.getDefaultState().with(SEE_THROUGH, true).with(SOLID, false));
	}

	public static boolean isNormalCube(BlockState state, BlockView world, BlockPos pos)
	{
		return false;
	}

	public static boolean causesSuffocation(BlockState state, BlockView world, BlockPos pos)
	{
		return state.get(SOLID);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx)
	{
		if(!state.get(SOLID) && ctx instanceof EntityShapeContext)
		{
			Entity entity = ((FabricEntityShapeContext) ctx).getEntity();

			if(entity instanceof PlayerEntity)
			{
				BlockEntity te1 = world.getBlockEntity(pos);

				if(te1 instanceof BlockPocketTileEntity)
				{
					BlockPocketTileEntity te = (BlockPocketTileEntity)te1;

					if(te.getManager() == null)
						return VoxelShapes.empty();

					if(te.getManager().hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(te.getManager().getWorld(), te.getManager().getPos(), ModuleType.WHITELIST).contains(entity.getName().getString().toLowerCase()))
						return VoxelShapes.empty();
					else if(!te.getOwner().isOwner((PlayerEntity)entity))
						return VoxelShapes.fullCube();
					else
						return VoxelShapes.empty();
				}
			}
		}

		return VoxelShapes.fullCube();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return state.get(SEE_THROUGH) && adjacentBlockState.getBlock() == SCContent.BLOCK_POCKET_WALL;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context)
	{
		return super.getPlacementState(context).with(SEE_THROUGH, true);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(SEE_THROUGH, SOLID);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new BlockPocketTileEntity();
//	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos)
	{
		return new ItemStack(SCContent.BLOCK_POCKET_WALL, 1);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos)
	{
		return true;
	}
}
