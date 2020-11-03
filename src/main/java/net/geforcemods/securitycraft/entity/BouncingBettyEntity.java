package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
//import net.minecraftforge.fml.network.NetworkHooks;

public class BouncingBettyEntity extends Entity {

	/** How long the fuse is */
	public int fuse;

	public BouncingBettyEntity(EntityType<BouncingBettyEntity> type, World world){
		super(SCContent.eTypeBouncingBetty, world);
	}

	public BouncingBettyEntity(World world, double x, double y, double z){
		this(SCContent.eTypeBouncingBetty, world);
		updatePosition(x, y, z);
		float f = (float)(Math.random() * Math.PI * 2.0D);
		setVelocity(-((float)Math.sin(f)) * 0.02F, 0.20000000298023224D, -((float)Math.cos(f)) * 0.02F);
		fuse = 80;
		prevX = x;
		prevY = y;
		prevZ = z;
	}

	@Override
	protected void initDataTracker() {}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean canClimb()
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean collides()
	{
		return !removed;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick()
	{
		prevX = getX();
		prevY = getY();
		prevZ = getZ();
		setVelocity(getVelocity().add(0, -0.03999999910593033D, 0));
		move(MovementType.SELF, getVelocity());
		setVelocity(getVelocity().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if (onGround)
			setVelocity(getVelocity().multiply(0.699999988079071D, 0.699999988079071D, -0.5D));

		if (fuse-- <= 0)
		{
			remove();

			if (!world.isClient)
				explode();
		}
		else if(world.isClient)
			world.addParticle(ParticleTypes.SMOKE, false, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void explode()
	{
		float f = 6.0F;

		if(ConfigHandler.CONFIG.smallerMineExplosion)
			world.createExplosion(this, getX(), getY(), getZ(), (f / 2), ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
		else
			world.createExplosion(this, getX(), getY(), getZ(), f, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeCustomDataToTag(CompoundTag tag)
	{
		tag.putByte("Fuse", (byte)fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readCustomDataFromTag(CompoundTag tag)
	{
		fuse = tag.getByte("Fuse");
	}

	@Override // TODO
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this); // Probably won't work
//		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
