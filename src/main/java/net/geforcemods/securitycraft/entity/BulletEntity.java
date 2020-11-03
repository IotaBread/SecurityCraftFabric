package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends PersistentProjectileEntity
{
	public BulletEntity(EntityType<BulletEntity> type, World world)
	{
		super(SCContent.eTypeBullet, world);
	}

	public BulletEntity(World world, LivingEntity shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);
	}

	@Override
	protected void onEntityHit(EntityHitResult raytraceResult)
	{
		if(!(raytraceResult.getEntity() instanceof SentryEntity))
		{
			raytraceResult.getEntity().damage(DamageSource.arrow(this, getOwner()), MathHelper.ceil(getVelocity().length()));
			remove();
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult raytraceResult) //onBlockHit
	{
		remove();
	}

	@Override
	protected void onHit(LivingEntity entity)
	{
		remove();
	}

	@Override
	protected ItemStack asItemStack()
	{
		return ItemStack.EMPTY;
	}

	@Override // TODO
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
//		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
