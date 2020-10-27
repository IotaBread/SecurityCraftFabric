package net.geforcemods.securitycraft.api;

//import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

import java.util.Iterator;
import java.util.List;

/**
 * Simple TileEntity that SecurityCraft uses to easily create blocks like
 * the retinal scanner (activated by looking at it) and attacking blocks
 * like the protecto. Everything can be overridden for easy customization
 * or use as an API.
 *
 * @version 1.1.3
 *
 * @author Geforce
 */
public class SecurityCraftTileEntity extends OwnableTileEntity implements Tickable, INameable {

	protected boolean intersectsEntities = false;
	protected boolean viewActivated = false;
	private boolean attacks = false;
	private boolean canBeNamed = false;
	private Text customName = new LiteralText("name");
	private double attackRange = 0.0D;
	private int viewCooldown = getViewCooldown();
	private int ticksBetweenAttacks = 0;
	private int attackCooldown = 0;
	private Class<? extends Entity> typeToAttack = Entity.class;

//	public SecurityCraftTileEntity()
//	{
//		this(SCContent.teTypeAbstract);
//	}

	public SecurityCraftTileEntity(BlockEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void tick() {
		if(intersectsEntities){
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			Box area = (new Box(x, y, z, x + 1, y + 1, z + 1));
			List<?> entities = world.getNonSpectatingEntities(Entity.class, area);
			Iterator<?> iterator = entities.iterator();
			Entity entity;

			while (iterator.hasNext())
			{
				entity = (Entity)iterator.next();
				entityIntersecting(entity);
			}
		}

		if(viewActivated){
			if(viewCooldown > 0){
				viewCooldown--;
				return;
			}

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			Box area = (new Box(x, y, z, (x), (y), (z)).expand(5, 5, 5));
			List<?> entities = world.getNonSpectatingEntities(LivingEntity.class, area);
			Iterator<?> iterator = entities.iterator();
			LivingEntity entity;

			while (iterator.hasNext())
			{
				entity = (LivingEntity)iterator.next();
				double eyeHeight = entity.getStandingEyeHeight();
				boolean isPlayer = (entity instanceof PlayerEntity);
				Vec3d lookVec = new Vec3d((entity.getX() + (entity.getRotationVector().x * 5)), ((eyeHeight + entity.getY()) + (entity.getRotationVector().y * 5)), (entity.getZ() + (entity.getRotationVector().z * 5)));

				HitResult mop = getWorld().raycast(new RaycastContext(new Vec3d(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ()), lookVec, ShapeType.COLLIDER, FluidHandling.NONE, entity));
				if(mop != null && mop.getType() == Type.BLOCK)
					if(((BlockHitResult)mop).getBlockPos().getX() == getPos().getX() && ((BlockHitResult)mop).getBlockPos().getY() == getPos().getY() && ((BlockHitResult)mop).getBlockPos().getZ() == getPos().getZ())
						if((isPlayer && activatedOnlyByPlayer()) || !activatedOnlyByPlayer()) {
							entityViewed(entity);
							viewCooldown = getViewCooldown();
						}
			}
		}

		if (attacks) {
			if (attackCooldown < getTicksBetweenAttacks()) {
				attackCooldown++;
				return;
			}

			if (canAttack()) {
				Box area = new Box(pos).expand(getAttackRange(), getAttackRange(), getAttackRange());
				List<?> entities = world.getNonSpectatingEntities(entityTypeToAttack(), area);
				Iterator<?> iterator = entities.iterator();

				if(!world.isClient){
					boolean attacked = false;

					if(!iterator.hasNext())
						attackFailed();

					while (iterator.hasNext()) {
						Entity mobToAttack = (Entity) iterator.next();

						if (mobToAttack == null || mobToAttack instanceof ItemEntity || !shouldAttackEntityType(mobToAttack))
							continue;

						if (attackEntity(mobToAttack))
							attacked = true;
					}

					if (attacked || shouldRefreshAttackCooldown())
						attackCooldown = 0;

					if(attacked || shouldSyncToClient())
						sync();
				}
			}
		}
	}

	public void entityIntersecting(Entity entity) {
		if(!(world.getBlockState(getPos()).getBlock() instanceof IIntersectable)) return;

		((IIntersectable) world.getBlockState(getPos()).getBlock()).onEntityIntersected(getWorld(), getPos(), entity);
	}

	/**
	 * Called when {@link SecurityCraftTileEntity}.isViewActivated(), and when an entity looks directly at this block.
	 */
	public void entityViewed(LivingEntity entity) {}

	/**
	 * Handle your TileEntity's attack to entities here.
	 * ONLY RUNS ON THE SERVER SIDE (to keep the TE's client cooldown in-sync)! If you need something done on the client,
	 * use packets.<p>
	 *
	 * @return True if it successfully attacked, false otherwise.
	 */
	public boolean attackEntity(Entity entity) {
		return false;
	}

	/**
	 * Is called when a {@link SecurityCraftTileEntity} is ready to attack, but cannot for some reason. <p>
	 *
	 * These reasons may include: <p>
	 * - There are no Entities in this block's attack range. <p>
	 * - Only ItemEntitys are in the attack range. <p>
	 * - The Entities in this block's attack range are not of the type set in entityTypeToAttack().
	 */
	public void attackFailed() {}

	/**
	 * Check if your TileEntity is ready to attack. (i.e: block conditions, metadata, etc.) <p>
	 * Different from {@link SecurityCraftTileEntity}.doesAttack(), which simply returns if your TileEntity <i>does</i> attack.
	 */
	public boolean canAttack() {
		return false;
	}

	public boolean shouldAttackEntityType(Entity entity) {
		return entity instanceof PlayerEntity || typeToAttack.isAssignableFrom(entity.getClass());
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		tag.putBoolean("intersectsEntities", intersectsEntities);
		tag.putBoolean("viewActivated", viewActivated);
		tag.putBoolean("attacks", attacks);
		tag.putBoolean("canBeNamed", canBeNamed);
		tag.putDouble("attackRange", attackRange);
		tag.putInt("attackCooldown", attackCooldown);
		tag.putInt("ticksBetweenAttacks", ticksBetweenAttacks);
		tag.putString("customName", customName.getString());
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if (tag.contains("intersectsEntities"))
			intersectsEntities = tag.getBoolean("intersectsEntities");

		if (tag.contains("viewActivated"))
			viewActivated = tag.getBoolean("viewActivated");

		if (tag.contains("attacks"))
			attacks = tag.getBoolean("attacks");

		if (tag.contains("canBeNamed"))
			canBeNamed = tag.getBoolean("canBeNamed");

		if (tag.contains("attackRange"))
			attackRange = tag.getDouble("attackRange");

		if (tag.contains("attackCooldown"))
			attackCooldown = tag.getInt("attackCooldown");

		if (tag.contains("ticksBetweenAttacks"))
			ticksBetweenAttacks = tag.getInt("ticksBetweenAttacks");

		if (tag.contains("customName"))
			customName = new LiteralText(tag.getString("customName"));
	}

	@Override
	public void markRemoved() {
		super.markRemoved();

		onTileEntityDestroyed();
	}

	public void onTileEntityDestroyed() {}

	/**
	 * Automatically detects the side this method was called on, and
	 * sends the client-side value of this TileEntity's CompoundNBT
	 * to the server-side, or the server-side value to the client-side,
	 * respectively.
	 */
	public void sync() {
		if(world == null) return;

		world.updateListeners(pos, getCachedState(), getCachedState(), 3);
	}

	/**
	 * Sets the TileEntity able to be intersected with.
	 * <p>
	 * Calls {@link IIntersectable}.onEntityIntersected(World, BlockPos, LivingEntity) when a {@link LivingEntity} comes into contact with this block.
	 * <p>
	 * Implement IIntersectable in your Block class in order to do stuff with that event.
	 */
	public SecurityCraftTileEntity intersectsEntities(){
		intersectsEntities = true;
		return this;
	}

	public boolean doesIntersectsEntities(){
		return intersectsEntities;
	}

	/**
	 * Sets this TileEntity able to be activated when a player looks at the block.
	 * <p>
	 * Calls {@link SecurityCraftTileEntity}.entityViewed(LivingEntity) when an {@link LivingEntity} looks at this block.
	 * <p>
	 * Implement IViewActivated in your Block class in order to do stuff with that event.
	 */
	public SecurityCraftTileEntity activatedByView(){
		viewActivated = true;
		return this;
	}

	/**
	 * @return The amount of ticks the block should "cooldown"
	 *         for after an Entity looks at this block.
	 */
	public int getViewCooldown() {
		return 0;
	}

	/**
	 * @return Can this TileEntity can only be activated by an PlayerEntity?
	 */
	public boolean activatedOnlyByPlayer() {
		return true;
	}

	/**
	 * @return If this TileEntity can be activated when an Entity looking at it.
	 */
	public boolean isActivatedByView(){
		return viewActivated;
	}

	/**
	 * Sets this TileEntity able to attack.
	 * <p>
	 * Calls {@link SecurityCraftTileEntity}.attackEntity(Entity) when this TE's cooldown equals 0.
	 */
	public SecurityCraftTileEntity attacks(Class<? extends Entity> type, double range, int cooldown) {
		attacks = true;
		typeToAttack = type;
		attackRange = range;
		ticksBetweenAttacks = cooldown;
		return this;
	}

	/**
	 * @return The class of the entity that this TileEntity should attack.
	 */
	public Class<? extends Entity> entityTypeToAttack(){
		return typeToAttack;
	}

	/**
	 * @return The range that this TileEntity checks for attackable entities.
	 */
	public double getAttackRange() {
		return attackRange;
	}

	/**
	 * @return The number of ticks between attacks.
	 */
	public int getTicksBetweenAttacks() {
		return ticksBetweenAttacks;
	}

	/**
	 * @return Gets the number of ticks before {@link SecurityCraftTileEntity}.attackEntity(Entity) is called.
	 */
	public int getAttackCooldown() {
		return attackCooldown;
	}

	/**
	 *  Set this TileEntity's attack cooldown.
	 */
	public void setAttackCooldown(int cooldown) {
		attackCooldown = cooldown;
	}

	/**
	 *  Maxes out this TileEntity's attack cooldown, so it'll attempt to attack next tick.
	 */
	public void attackNextTick() {
		attackCooldown = ticksBetweenAttacks;
	}

	/**
	 * @return If, once this TileEntity's attack cooldown gets to the set maximum,
	 *         it should start again automatically from 0.
	 */
	public boolean shouldRefreshAttackCooldown() {
		return true;
	}

	/**
	 * @return Should this TileEntity send an update packet
	 *         to all clients if it attacks unsuccessfully?
	 */
	public boolean shouldSyncToClient() {
		return true;
	}

	/**
	 * @return If this TileEntity can attack.
	 */
	public boolean doesAttack() {
		return attacks;
	}

	public SecurityCraftTileEntity nameable() {
		canBeNamed = true;
		return this;
	}

	@Override
	public Text getCustomSCName() {
		return customName;
	}

	@Override
	public void setCustomSCName(Text customName) {
		this.customName = customName;
		sync();
	}

	@Override
	public boolean hasCustomSCName() {
		return (customName != null && !customName.getString().equals("name"));
	}

	@Override
	public boolean canBeNamed() {
		return canBeNamed;
	}

}
