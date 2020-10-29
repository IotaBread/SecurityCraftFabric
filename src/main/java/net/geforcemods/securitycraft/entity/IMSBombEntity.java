package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
//import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
//import net.minecraftforge.fml.network.NetworkHooks;

public class IMSBombEntity extends AbstractFireballEntity {

	private String playerName = null;
	private LivingEntity targetMob = null;
	public int ticksFlying = 0;
	private int launchHeight;
	public boolean launching = true;

	public IMSBombEntity(EntityType<IMSBombEntity> type, World world){
		super(SCContent.eTypeImsBomb, world);
	}

	public IMSBombEntity(World world, PlayerEntity targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(SCContent.eTypeImsBomb, x, y, z, targetX, targetY, targetZ, world);
		playerName = targetEntity.getName().getString();
		launchHeight = height;
	}

	public IMSBombEntity(World world, LivingEntity targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(SCContent.eTypeImsBomb, x, y, z, targetX, targetY, targetZ, world);
		targetMob = targetEntity;
		launchHeight = height;
	}

	@Override
	public void tick(){
		if(!launching){
			super.tick();
			return;
		}

		if(ticksFlying < launchHeight){
			setVelocity(getVelocity().x, 0.35F, getVelocity().z);
			ticksFlying++;
			move(MovementType.SELF, getVelocity());
		}else
			setTarget();
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			PlayerEntity target = PlayerUtils.getPlayerFromName(playerName);

			double targetX = target.getX() - getX();
			double targetY = target.getBoundingBox().minY + target.getHeight() / 2.0F - (getY() + 1.25D);
			double targetZ = target.getZ() - getZ();
			IMSBombEntity imsBomb = new IMSBombEntity(world, target, getX(), getY(), getZ(), targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(imsBomb));
			remove();
		}else if(targetMob != null && !targetMob.removed){
			double targetX = targetMob.getX() - getX();
			double targetY = targetMob.getBoundingBox().minY + targetMob.getHeight() / 2.0F - (getY() + 1.25D);
			double targetZ = targetMob.getZ() - getZ();
			IMSBombEntity imsBomb = new IMSBombEntity(world, targetMob, getX(), getY(), getZ(), targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(imsBomb));
			remove();
		}
	}

	@Override
	protected void onCollision(HitResult result){
		if(!world.isClient)
			if(result.getType() == Type.BLOCK && BlockUtils.getBlock(world, ((BlockHitResult)result).getBlockPos()) != SCContent.IMS){
				world.createExplosion(this, ((BlockHitResult)result).getBlockPos().getX(), ((BlockHitResult)result).getBlockPos().getY() + 1D, ((BlockHitResult)result).getBlockPos().getZ(), 7F, ConfigHandler.CONFIG.shouldSpawnFire, DestructionType.BREAK);
				remove();
			}
	}

	@Override
	protected float getDrag(){
		return 1F;
	}

	@Override
	protected boolean canClimb(){
		return false;
	}

	@Override
	public boolean collides(){
		return false;
	}

	@Override
	public float getTargetingMargin(){
		return 0.3F;
	}

//	@Override
//	public Packet<?> createSpawnPacket()
//	{
//		return NetworkHooks.getEntitySpawningPacket(this);
//	}
}
