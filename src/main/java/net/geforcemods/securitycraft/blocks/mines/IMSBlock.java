package net.geforcemods.securitycraft.blocks.mines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.ParticleTypes;
//import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.Random;

public class IMSBlock extends OwnableBlock {

	public static final IntProperty MINES = IntProperty.of("mines", 0, 4);
	private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 5, 12, 7, 11);
	private static final VoxelShape SHAPE_1_MINE = VoxelShapes.union(SHAPE, Block.createCuboidShape(0, 0, 0, 5, 5, 5));
	private static final VoxelShape SHAPE_2_MINES = VoxelShapes.union(SHAPE_1_MINE, Block.createCuboidShape(0, 0, 11, 5, 5, 16));
	private static final VoxelShape SHAPE_3_MINES = VoxelShapes.union(SHAPE_2_MINES, Block.createCuboidShape(11, 0, 0, 16, 5, 5));
	private static final VoxelShape SHAPE_4_MINES = VoxelShapes.union(SHAPE_3_MINES, Block.createCuboidShape(11, 0, 11, 16, 5, 16));

	public IMSBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(MINES, 4));
	}

	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos)
	{
		return !ConfigHandler.CONFIG.ableToBreakMines ? -1F : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView source, BlockPos pos, ShapeContext ctx)
	{
		switch(state.get(MINES))
		{
			case 4: return SHAPE_4_MINES;
			case 3: return SHAPE_3_MINES;
			case 2: return SHAPE_2_MINES;
			case 1: return SHAPE_1_MINE;
			default: return SHAPE;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.down()).getMaterial() != Material.AIR)
			return;
		else
			world.breakBlock(pos, true);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!world.isClient)
		{
			if(((IOwnable) world.getBlockEntity(pos)).getOwner().isOwner(player))
			{
				ItemStack held = player.getStackInHand(hand);
				int mines = state.get(MINES);

				if(held.getItem() == SCContent.BOUNCING_BETTY.asItem() && mines < 4)
				{
					if(!player.isCreative())
						held.decrement(1);

					world.setBlockState(pos, state.with(MINES, mines + 1));
					((IMSTileEntity)world.getBlockEntity(pos)).setBombsRemaining(mines + 1);
				}
				else if(player instanceof ServerPlayerEntity)
				{
					BlockEntity te = world.getBlockEntity(pos);

//					if(te instanceof NamedScreenHandlerFactory) // TODO
//						NetworkHooks.openGui((ServerPlayerEntity)player, (NamedScreenHandlerFactory)te, pos);
				}
			}
		}

		return ActionResult.SUCCESS;
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random){
		if(state.get(MINES) == 0){
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.addParticle(ParticleTypes.SMOKE, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y, z, 0.0D, 0.0D, 0.0D);

			world.addParticle(ParticleTypes.FLAME, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder)
	{
		int mines = state.get(MINES);

		if(mines != 0)
			return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(SCContent.BOUNCING_BETTY, mines));
		else return super.getDroppedStacks(state, builder);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(MINES, 4);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder)
	{
		builder.add(MINES);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new IMSTileEntity();
//	}

}
