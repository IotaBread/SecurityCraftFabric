package net.geforcemods.securitycraft.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BaseKeycardItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class KeycardReaderBlock extends DisguisableBlock  {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public KeycardReaderBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	public void insertCard(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
		if(ModuleUtils.checkForModule(world, pos, player, ModuleType.BLACKLIST))
			return;

		boolean whitelisted = ModuleUtils.checkForModule(world, pos, player, ModuleType.WHITELIST);
		int requiredLevel = -1;
		int cardLvl = ((BaseKeycardItem) stack.getItem()).getKeycardLvl();
		KeycardReaderTileEntity te = ((KeycardReaderTileEntity)world.getBlockEntity(pos));
		boolean exact = te.doesRequireExactKeycard();

		if(te.getPassword() != null)
			requiredLevel = Integer.parseInt(te.getPassword());

		if(whitelisted || (!exact && requiredLevel <= cardLvl || exact && requiredLevel == cardLvl)){
			if(cardLvl == 6 && stack.getTag() != null && !player.isCreative()){
				stack.getTag().putInt("Uses", stack.getTag().getInt("Uses") - 1);

				if(stack.getTag().getInt("Uses") <= 0)
					stack.decrement(1);
			}

			KeycardReaderBlock.activate(world, pos, te.getSignalLength());
		}
		else if (world.isClient) {
			if(requiredLevel == -1)
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.notSet"), Formatting.RED);
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:keycardReader.required", te.getPassword(), ((BaseKeycardItem) stack.getItem()).getKeycardLvl()), Formatting.RED);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(player.inventory.getMainHandStack().isEmpty() || (!(player.inventory.getMainHandStack().getItem() instanceof BaseKeycardItem) && player.inventory.getMainHandStack().getItem() != SCContent.ADMIN_TOOL))
			((KeycardReaderTileEntity) world.getBlockEntity(pos)).openPasswordGUI(player);
		else if(player.inventory.getMainHandStack().getItem() == SCContent.ADMIN_TOOL)
			((KeycardReaderBlock) BlockUtils.getBlock(world, pos)).insertCard(world, pos, new ItemStack(SCContent.LIMITED_USE_KEYCARD, 1), player);
		else
			((KeycardReaderBlock) BlockUtils.getBlock(world, pos)).insertCard(world, pos, player.inventory.getMainHandStack(), player);

		return ActionResult.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, int signalLength){
		BlockUtils.setBlockProperty(world, pos, POWERED, true);
		world.updateNeighborsAlways(pos, SCContent.KEYCARD_READER);
		world.getBlockTickScheduler().schedule(pos, SCContent.KEYCARD_READER, signalLength);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(!world.isClient){
			BlockUtils.setBlockProperty(world, pos, POWERED, false);
			world.updateNeighborsAlways(pos, SCContent.KEYCARD_READER);
		}
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand){
		if((state.get(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float f1 = 0.6F + 0.4F;
			float f2 = Math.max(0.0F, 0.7F - 0.5F);
			float f3 = Math.max(0.0F, 0.6F - 0.7F);

			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleEffect(f1, f2, f3, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side)
	{
		if((blockState.get(POWERED)))
			return 15;
		else
			return 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), ctx.getSide(), ctx.getHitPos().x, ctx.getHitPos().y, ctx.getHitPos().z, ctx.getPlayer());
	}

	public BlockState getPlacementState(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(POWERED, false);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world) {
//		return new KeycardReaderTileEntity();
//	}

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
