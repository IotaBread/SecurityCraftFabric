package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.compat.fabric.FabricEntityShapeContext;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.CageTrapTileEntity;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.TriConsumer;

public class CageTrapBlock extends DisguisableBlock implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.of("deactivated");

	public CageTrapBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(DEACTIVATED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx){
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof DisguisableTileEntity)
		{
			DisguisableTileEntity disguisableTe = (DisguisableTileEntity)te;

			if(ctx instanceof EntityShapeContext)
			{
				EntityShapeContext esc = (EntityShapeContext)ctx;

				if(((FabricEntityShapeContext) esc).getEntity() instanceof PlayerEntity && te instanceof IOwnable && ((IOwnable) te).getOwner().isOwner((PlayerEntity) ((FabricEntityShapeContext) esc).getEntity()))
					return getCorrectShape(state, world, pos, ctx, disguisableTe);
				if(((FabricEntityShapeContext) esc).getEntity() instanceof MobEntity && te instanceof CageTrapTileEntity && !state.get(DEACTIVATED))
					return (((CageTrapTileEntity) te).capturesMobs() ? VoxelShapes.empty() : getCorrectShape(state, world, pos, ctx, disguisableTe));
				else if(((FabricEntityShapeContext) esc).getEntity() instanceof ItemEntity)
					return getCorrectShape(state, world, pos, ctx, disguisableTe);
			}

			return state.get(DEACTIVATED) ? getCorrectShape(state, world, pos, ctx, disguisableTe) : VoxelShapes.empty();
		}
		else return VoxelShapes.empty(); //shouldn't happen
	}

	private VoxelShape getCorrectShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx, DisguisableTileEntity disguisableTe)
	{
		ItemStack moduleStack = disguisableTe.getModule(ModuleType.DISGUISE);

		if(!moduleStack.isEmpty() && (((ModuleItem)moduleStack.getItem()).getBlockAddons(moduleStack.getTag()).size() > 0))
			return super.getCollisionShape(state, world, pos, ctx);
		else return VoxelShapes.fullCube();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isClient){
			CageTrapTileEntity tileEntity = (CageTrapTileEntity) world.getBlockEntity(pos);
			boolean isPlayer = entity instanceof PlayerEntity;

			if(isPlayer || (entity instanceof MobEntity && tileEntity.capturesMobs())){
				if((isPlayer && ((IOwnable)world.getBlockEntity(pos)).getOwner().isOwner((PlayerEntity)entity)))
					return;

				if(BlockUtils.getBlockProperty(world, pos, DEACTIVATED))
					return;

				BlockPos topMiddle = pos.up(4);
				String ownerName = ((IOwnable)world.getBlockEntity(pos)).getOwner().getName();

				BlockModifier placer = new BlockModifier(world, new BlockPos.Mutable().set(pos), tileEntity.getOwner());

				placer.loop((w, p, o) -> {
					if(w.isAir(p))
					{
						if(p.equals(topMiddle))
							w.setBlockState(p, SCContent.HORIZONTAL_REINFORCED_IRON_BARS.getDefaultState());
						else
							w.setBlockState(p, ((ReinforcedPaneBlock)SCContent.REINFORCED_IRON_BARS).getPlacementState(w, p));
					}
				});
				placer.loop((w, p, o) -> {
					BlockEntity te = w.getBlockEntity(p);

					if(te instanceof IOwnable)
						((IOwnable)te).getOwner().set(o);
				});
				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if(isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.sendMessageToPlayer(ownerName, ClientUtils.localize(SCContent.CAGE_TRAP.getTranslationKey()), ClientUtils.localize("messages.securitycraft:cageTrap.captured", ((PlayerEntity) entity).getName(), Utils.getFormattedCoordinates(pos)), Formatting.BLACK);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity player)
	{
		return getDefaultState().with(DEACTIVATED, false);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new CageTrapTileEntity().intersectsEntities();
//	}

	public static class BlockModifier
	{
		private World world;
		private BlockPos.Mutable pos;
		private BlockPos origin;
		private Owner owner;

		public BlockModifier(World world, BlockPos.Mutable origin, Owner owner)
		{
			this.world = world;
			pos = origin.move(-1, 1, -1);
			this.origin = origin.toImmutable();
			this.owner = owner;
		}

		public void loop(TriConsumer<World,BlockPos.Mutable,Owner> ifTrue)
		{
			for(int y = 0; y < 4; y++)
			{
				for(int x = 0; x < 3; x++)
				{
					for(int z = 0; z < 3; z++)
					{
						//skip the middle column above the cage trap, but not the place where the horiztonal iron bars are
						if(!(x == 1 && z == 1 && y != 3))
							ifTrue.accept(world, pos, owner);

						pos.move(0, 0, 1);
					}

					pos.move(1, 0, -3);
				}

				pos.move(-3, 1, 0);
			}

			pos.set(origin); //reset the mutable block pos for the next usage
		}

		@FunctionalInterface
		public interface TriFunction<T,U,V,R>
		{
			R apply(T t, U u, V v);
		}
	}
}
