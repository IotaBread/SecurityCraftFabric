package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.ai.AttackRangedIfEnabledGoal;
import net.geforcemods.securitycraft.entity.ai.TargetNearestPlayerOrMobGoal;
import net.geforcemods.securitycraft.items.ModuleItem;
//import net.geforcemods.securitycraft.network.client.InitSentryAnimation;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
//import net.minecraftforge.fml.network.NetworkHooks;
//import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Random;

public class SentryEntity extends PathAwareEntity implements RangedAttackMob //needs to be a creature so it can target a player, ai is also only given to living entities
{
	private static final TrackedData<Owner> OWNER = DataTracker.<Owner>registerData(SentryEntity.class, Owner.getSerializer());
	private static final TrackedData<CompoundTag> MODULE = DataTracker.<CompoundTag>registerData(SentryEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	private static final TrackedData<CompoundTag> WHITELIST = DataTracker.<CompoundTag>registerData(SentryEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	private static final TrackedData<Integer> MODE = DataTracker.<Integer>registerData(SentryEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final TrackedData<Float> HEAD_ROTATION = DataTracker.<Float>registerData(SentryEntity.class, TrackedDataHandlerRegistry.FLOAT);
	public static final float MAX_TARGET_DISTANCE = 20.0F;
	private float headYTranslation = 0.9F;
	private final float animationStepSize = 0.025F;
	public boolean animateUpwards = true;
	public boolean animate = false;
	private long previousTargetId = Long.MIN_VALUE;

	public SentryEntity(EntityType<SentryEntity> type, World world)
	{
		super(SCContent.eTypeSentry, world);
	}

	public void setupSentry(PlayerEntity owner)
	{
		dataTracker.set(OWNER, new Owner(owner.getName().getString(), PlayerEntity.getUuidFromProfile(owner.getGameProfile()).toString()));
		dataTracker.set(MODULE, new CompoundTag());
		dataTracker.set(WHITELIST, new CompoundTag());
		dataTracker.set(MODE, SentryMode.CAMOUFLAGE.ordinal());
		dataTracker.set(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(OWNER, new Owner());
		dataTracker.startTracking(MODULE, new CompoundTag());
		dataTracker.startTracking(WHITELIST, new CompoundTag());
		dataTracker.startTracking(MODE, SentryMode.CAMOUFLAGE.ordinal());
		dataTracker.startTracking(HEAD_ROTATION, 0.0F);
	}

	@Override
	protected void initGoals()
	{
		goalSelector.add(1, new AttackRangedIfEnabledGoal(this, 5, 10.0F));
		targetSelector.add(1, new TargetNearestPlayerOrMobGoal(this));
	}

	@Override
	public void tick()
	{
		super.tick();

		if(world.isClient)
		{
			if(!animate && headYTranslation > 0.0F && dataTracker.get(MODE) == 0)
			{
				animateUpwards = true;
				animate = true;
			}

			if(animate) //no else if because animate can be changed in the above if statement
			{
				if(animateUpwards && headYTranslation > 0.0F)
				{
					headYTranslation -= animationStepSize;

					if(headYTranslation <= 0.0F)
					{
						animateUpwards = false;
						animate = false;
					}
				}
				else if(!animateUpwards && headYTranslation < 0.9F)
				{
					headYTranslation += animationStepSize;

					if(headYTranslation >= 0.9F)
					{
						animateUpwards = true;
						animate = false;
					}
				}
			}
		}
	}

//	@Override // Forge method
//	public ItemStack getPickedResult(HitResult target)
//	{
//		return new ItemStack(SCContent.SENTRY);
//	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand)
	{
		BlockPos pos = getBlockPos();

		if(getOwner().isOwner(player) && hand == Hand.MAIN_HAND)
		{
			Item item = player.getMainHandStack().getItem();

//			player.closeHandledScreen(); // TODO

			if(player.isInSneakingPose())
				remove();
			else if(item == SCContent.UNIVERSAL_BLOCK_REMOVER)
			{
				remove();

				if(!player.isCreative())
					player.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(hand));
			}
			else if(item == SCContent.DISGUISE_MODULE)
			{
				ItemStack module = getDisguiseModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
				{
					Block.dropStack(world, pos, module);

					List<Block> blocks = ((ModuleItem)module.getItem()).getBlockAddons(module.getTag());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				setDisguiseModule(player.getMainHandStack());

				if(!player.isCreative())
					player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.WHITELIST_MODULE)
			{
				ItemStack module = getWhitelistModule();

				if(!module.isEmpty()) //drop the old module as to not override it with the new one
					Block.dropStack(world, pos, module);

				setWhitelistModule(player.getMainHandStack());

				if(!player.isCreative())
					player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			else if(item == SCContent.UNIVERSAL_BLOCK_MODIFIER)
			{
				if (!getDisguiseModule().isEmpty())
				{
					List<Block> blocks = ((ModuleItem)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTag());

					if(blocks.size() > 0)
					{
						if(blocks.get(0) == world.getBlockState(pos).getBlock())
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}

				Block.dropStack(world, pos, getDisguiseModule());
				Block.dropStack(world, pos, getWhitelistModule());
				dataTracker.set(MODULE, new CompoundTag());
				dataTracker.set(WHITELIST, new CompoundTag());
			}
			else if(item == SCContent.REMOTE_ACCESS_SENTRY) //bind/unbind sentry to remote control
				item.useOnBlock(new ItemUsageContext(player, hand, new BlockHitResult(new Vec3d(0.0D, 0.0D, 0.0D), Direction.NORTH, pos, false)));
			else if(item == Items.NAME_TAG)
			{
				setCustomName(player.getMainHandStack().getName());
				player.getMainHandStack().decrement(1);
			}
			else if(item == SCContent.UNIVERSAL_OWNER_CHANGER)
			{
				String newOwner = player.getMainHandStack().getName().getString();

				dataTracker.set(OWNER, new Owner(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUuid().toString() : "ownerUUID"));

				if(world.isClient)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), Formatting.GREEN);
			}
			else
				toggleMode(player);

			player.swingHand(Hand.MAIN_HAND);
			return ActionResult.SUCCESS;
		}
		else if(!getOwner().isOwner(player) && hand == Hand.MAIN_HAND && player.isCreative())
		{
			if(player.isInSneakingPose() || player.getMainHandStack().getItem() == SCContent.UNIVERSAL_BLOCK_REMOVER)
				remove();
		}

		return super.interactMob(player, hand);
	}

	/**
	 * Cleanly removes this sentry from the world, dropping the module and removing the block the sentry is disguised with
	 */
	@Override
	public void remove()
	{
		BlockPos pos = getBlockPos();

		if (!getDisguiseModule().isEmpty())
		{
			List<Block> blocks = ((ModuleItem)getDisguiseModule().getItem()).getBlockAddons(getDisguiseModule().getTag());

			if(blocks.size() > 0)
			{
				if(blocks.get(0) == world.getBlockState(pos).getBlock())
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}

		super.remove();
		Block.dropStack(world, pos, new ItemStack(SCContent.SENTRY));
		Block.dropStack(world, pos, getDisguiseModule()); //if there is none, nothing will drop
		Block.dropStack(world, pos, getWhitelistModule()); //if there is none, nothing will drop
	}

	@Override
	public void kill()
	{
		remove();
	}

	/**
	 * Sets this sentry's mode to the next one and sends the player a message about the switch
	 * @param player The player to send the message to
	 */
	public void toggleMode(PlayerEntity player)
	{
		int mode = dataTracker.get(MODE) + 1;

		if(mode >= 3) //bigger than three in case that players set the value manually with command
			mode = 0;

		dataTracker.set(MODE, mode);

//		if(player.world.isClient) // TODO
//			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:sentry.mode" + (mode + 1)).append(ClientUtils.localize("messages.securitycraft:sentry.descriptionMode" + (mode + 1))), Formatting.DARK_RED);
//		else
//			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new InitSentryAnimation(getBlockPos(), true, mode == 0));
	}

	/**
	 * Sets this sentry's mode to the given mode (or 0 if the mode is not one of 0, 1, 2) and sends the player a message about the switch if wanted
	 * @param player The player to send the message to
	 * @param mode The mode (int) to switch to (instead of sequentially toggling)
	 */
	public void toggleMode(PlayerEntity player, int mode, boolean sendMessage)
	{
		if(mode < 0 || mode > 2)
			mode = 0;

		dataTracker.set(MODE, mode);

//		if(player.world.isClient && sendMessage) // TODO
//			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SENTRY.getTranslationKey()), ClientUtils.localize("messages.securitycraft:sentry.mode" + (mode + 1)).append(ClientUtils.localize("messages.securitycraft:sentry.descriptionMode" + (mode + 1))), Formatting.DARK_RED);
//		else if(!player.world.isClient)
//			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new InitSentryAnimation(getBlockPos(), true, mode == 0));
	}

	@Override
	public void setTarget(LivingEntity target)
	{
		if(getMode() != SentryMode.AGGRESSIVE && (target == null && previousTargetId != Long.MIN_VALUE || (target != null && previousTargetId != target.getEntityId())))
		{
			animateUpwards = getMode() == SentryMode.CAMOUFLAGE && target != null;
			animate = true;
//			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new InitSentryAnimation(getBlockPos(), animate, animateUpwards)); // TODO
		}

		previousTargetId = target == null ? Long.MIN_VALUE : target.getEntityId();
		super.setTarget(target);
	}

	@Override
	public float getEyeHeight(EntityPose pose) //the sentry's eyes are higher so that it can see players even if it's inside a block when disguised - this also makes bullets spawn higher
	{
		return 1.5F;
	}

	@Override
	public void attack(LivingEntity target, float distanceFactor)
	{
		//don't shoot if somehow a non player is a target, or if the player is in spectator or creative mode
		if(target instanceof PlayerEntity && (((PlayerEntity)target).isSpectator() || ((PlayerEntity)target).isCreative()))
			return;

		//also don't shoot if the target is too far away
		if(squaredDistanceTo(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE)
			return;

		BulletEntity throwableEntity = new BulletEntity(world, this);
		double y = target.getY() + target.getStandingEyeHeight() - 1.100000023841858D;
		double x = target.getX() - getX();
		double d2 = y - throwableEntity.getY();
		double z = target.getZ() - getZ();
		float f = MathHelper.sqrt(x * x + z * z) * 0.2F;

		throwableEntity.setPos(throwableEntity.getX(), throwableEntity.getY() - 0.1F, throwableEntity.getZ());
		dataTracker.set(HEAD_ROTATION, (float)(MathHelper.atan2(x, -z) * (180D / Math.PI)));
		throwableEntity.setVelocity(x, d2 + f, z, 1.6F, 0.0F); //no inaccuracy for sentries!
		playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
		world.spawnEntity(throwableEntity);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag)
	{
		tag.put("TileEntityData", getOwnerTag());
		tag.put("InstalledModule", getDisguiseModule().toTag(new CompoundTag()));
		tag.put("InstalledWhitelist", getWhitelistModule().toTag(new CompoundTag()));
		tag.putInt("SentryMode", dataTracker.get(MODE));
		tag.putFloat("HeadRotation", dataTracker.get(HEAD_ROTATION));
		super.writeCustomDataToTag(tag);
	}

	private CompoundTag getOwnerTag()
	{
		CompoundTag tag = new CompoundTag();
		Owner owner = dataTracker.get(OWNER);

		tag.putString("owner", owner.getName());
		tag.putString("ownerUUID", owner.getUUID());
		return tag;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag)
	{
		CompoundTag teTag = tag.getCompound("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataTracker.set(OWNER, new Owner(name, uuid));
		dataTracker.set(MODULE, tag.getCompound("InstalledModule"));
		dataTracker.set(WHITELIST, tag.getCompound("InstalledWhitelist"));
		dataTracker.set(MODE, tag.getInt("SentryMode"));
		dataTracker.set(HEAD_ROTATION, tag.getFloat("HeadRotation"));
		super.readCustomDataFromTag(tag);
	}

	/**
	 * @return The owner of this sentry
	 */
	public Owner getOwner()
	{
		return dataTracker.get(OWNER);
	}

	/**
	 * Sets the sentry's disguise module and places a block if possible
	 * @param module The module to set
	 */
	public void setDisguiseModule(ItemStack module)
	{
		List<ItemStack> blocks = ((ModuleItem)module.getItem()).getAddons(module.getTag());

		if(blocks.size() > 0)
		{
			ItemStack disguiseStack = blocks.get(0);
			BlockState state = Block.getBlockFromItem(disguiseStack.getItem()).getDefaultState();

			if (world.getBlockState(getBlockPos()).isAir())
				world.setBlockState(getBlockPos(), state.getOutlineShape(world, getBlockPos()) == VoxelShapes.fullCube() ? state : Blocks.AIR.getDefaultState());
		}

		dataTracker.set(MODULE, module.toTag(new CompoundTag()));
	}

	/**
	 * Sets the sentry's whitelist module
	 * @param module The module to set
	 */
	public void setWhitelistModule(ItemStack module)
	{
		dataTracker.set(WHITELIST, module.toTag(new CompoundTag()));
	}

	/**
	 * @return The disguise module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getDisguiseModule()
	{
		CompoundTag tag = dataTracker.get(MODULE);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return ItemStack.fromTag(tag);
	}

	/**
	 * @return The whitelist module that is added to this sentry. ItemStack.EMPTY if none available
	 */
	public ItemStack getWhitelistModule()
	{
		CompoundTag tag = dataTracker.get(WHITELIST);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;
		else
			return ItemStack.fromTag(tag);
	}

	/**
	 * @return The mode in which the sentry is currently in, CAMOUFLAGE if the saved mode is smaller than 0 or greater than 2 (there are only 3 valid modes: 0, 1, 2)
	 */
	public SentryMode getMode()
	{
		int mode = dataTracker.get(MODE);

		return mode < 0 || mode > 2 ? SentryMode.CAMOUFLAGE : SentryMode.values()[mode];
	}

	/**
	 * @return The amount of y translation from the head's default position, used for animation
	 */
	public float getHeadYTranslation()
	{
		return headYTranslation;
	}

	public boolean isTargetingWhitelistedPlayer(LivingEntity potentialTarget)
	{
		if(potentialTarget != null)
		{
			List<String> players = ModuleUtils.getPlayersFromModule(getWhitelistModule());

			for(String s : players)
			{
				if(potentialTarget.getName().asString().equalsIgnoreCase(s))
					return true;
			}
		}

		return false;
	}

	//start: disallow sentry to take damage
	@Override
	public boolean tryAttack(Entity entity)
	{
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public boolean isAttackable()
	{
		return false;
	}

	@Override
	public boolean isMobOrPlayer()
	{
		return false;
	}
	//end: disallow sentry to take damage

	@Override
	public boolean canSpawn(WorldAccess world, SpawnReason reason)
	{
		return false;
	}

	@Override
	public void jump() {} //sentries don't jump!

	@Override
	public boolean isNavigating()
	{
		return false;
	}

	@Override
	public void checkDespawn() {} //sentries don't despawn

	@Override
	public boolean canImmediatelyDespawn(double distanceClosestToPlayer)
	{
		return false; //sentries don't despawn
	}

	//sentries are heavy, so don't push them around!
	@Override
	public void onPlayerCollision(PlayerEntity entity) {}

	@Override
	public void move(MovementType type, Vec3d vec) {} //no moving sentries!

	@Override
	protected void pushAway(Entity entity) {}

	@Override
	protected void tickCramming() {}

	@Override
	public boolean isImmuneToExplosion()
	{
		return true; //does not get pushed around by explosions
	}

	@Override
	public boolean collides()
	{
		return true; //needs to stay true so blocks can't be broken through the sentry
	}

	@Override
	public boolean isPushable()
	{
		return false;
	}

	@Override
	public PistonBehavior getPistonBehavior()
	{
		return PistonBehavior.IGNORE;
	}

	@Override
	public void updateLeash() {} //no leashing for sentry

	//this last code is here so the ai task gets executed, which it doesn't for some weird reason
	@Override
	public Random getRandom()
	{
		return notRandom;
	}

	@Override // TODO
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
//		return NetworkHooks.getEntitySpawningPacket(this);
	}

	private static Random notRandom = new NotRandom();

	private static class NotRandom extends Random
	{
		@Override
		public int nextInt(int bound)
		{
			return 0;
		}
	}

	public static enum SentryMode
	{
		AGGRESSIVE, CAMOUFLAGE, IDLE;
	}
}
