package net.geforcemods.securitycraft.entity.ai;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collections;
import java.util.List;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class TargetNearestPlayerOrMobGoal extends FollowTargetGoal<LivingEntity>
{
	private SentryEntity sentry;

	public TargetNearestPlayerOrMobGoal(SentryEntity sentry)
	{
		super(sentry, LivingEntity.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean canStart()
	{
		List<LivingEntity> list = mob.world.<LivingEntity>getEntitiesByClass(targetClass, getSearchBox(getFollowRange()), e -> !EntityUtils.isInvisible(e));

		if(list.isEmpty())
			return false;
		else
		{
			int i;

			Collections.sort(list, (e1, e2) -> {
				double distTo1 = mob.squaredDistanceTo(e1);
				double distTo2 = mob.squaredDistanceTo(e2);

				if(distTo1 < distTo2)
					return -1;
				else return distTo1 > distTo2 ? 1 : 0;
			});

			//get the nearest target that is either a mob or a player
			for(i = 0; i < list.size(); i++)
			{
				LivingEntity potentialTarget = list.get(i);

				if(potentialTarget instanceof PlayerEntity && !((PlayerEntity)potentialTarget).isSpectator() && !((PlayerEntity)potentialTarget).isCreative() && !((SentryEntity)mob).getOwner().isOwner(((PlayerEntity)potentialTarget)))
					break;
				else if(sentry.isTargetingWhitelistedPlayer(potentialTarget))
					break;
				else if(sentry.getMode() == SentryMode.AGGRESSIVE && isSupportedTarget(potentialTarget))
					break;
			}

			if(i < list.size())
			{
				if(isCloseEnough(list.get(i)))
				{
					targetEntity = list.get(i);
					mob.setTarget(targetEntity);
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean shouldContinue()
	{
		return (isSupportedTarget(targetEntity) || targetEntity instanceof PlayerEntity) && isCloseEnough(targetEntity) && canStart() && !sentry.isTargetingWhitelistedPlayer(target) && super.shouldContinue();
	}

	public boolean isCloseEnough(Entity entity)
	{
		return entity != null && mob.squaredDistanceTo(entity) <= getFollowRange() * getFollowRange();
	}

	public boolean isSupportedTarget(LivingEntity potentialTarget)
	{
		return (potentialTarget instanceof HostileEntity || potentialTarget instanceof FlyingEntity || potentialTarget instanceof SlimeEntity || potentialTarget instanceof ShulkerEntity || potentialTarget instanceof EnderDragonEntity) && potentialTarget.deathTime == 0;
	}

	@Override
	protected double getFollowRange()
	{
		return SentryEntity.MAX_TARGET_DISTANCE;
	}
}
