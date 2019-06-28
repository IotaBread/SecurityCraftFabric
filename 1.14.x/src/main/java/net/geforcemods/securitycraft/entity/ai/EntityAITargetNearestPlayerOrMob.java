package net.geforcemods.securitycraft.entity.ai;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class EntityAITargetNearestPlayerOrMob extends NearestAttackableTargetGoal<LivingEntity>
{
	private EntitySentry sentry;

	public EntityAITargetNearestPlayerOrMob(EntitySentry sentry)
	{
		super(sentry, LivingEntity.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean shouldExecute()
	{
		List<LivingEntity> list = field_75299_d.world.<LivingEntity>getEntitiesWithinAABB(targetClass, getTargetableArea(getTargetDistance()), e -> true);

		if(list.isEmpty())
			return false;
		else
		{
			int i;

			Collections.sort(list, (e1, e2) -> {
				double distTo1 = field_75299_d.getDistanceSq(e1);
				double distTo2 = field_75299_d.getDistanceSq(e2);

				if(distTo1 < distTo2)
					return -1;
				else return distTo1 > distTo2 ? 1 : 0;
			});

			//get the nearest target that is either a mob or a player
			for(i = 0; i < list.size(); i++)
			{
				LivingEntity potentialTarget = list.get(i);

				if(potentialTarget instanceof PlayerEntity && !((PlayerEntity)potentialTarget).isSpectator() && !((PlayerEntity)potentialTarget).isCreative() && !((EntitySentry)field_75299_d).getOwner().isOwner(((PlayerEntity)potentialTarget)))
					break;
				else if(potentialTarget instanceof MonsterEntity && sentry.getMode() == EnumSentryMode.AGGRESSIVE)
					break;
			}

			if(i < list.size())
			{
				if(isCloseEnough(list.get(i)))
				{
					field_75309_a = list.get(i);
					field_75299_d.setAttackTarget(field_75309_a);
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return (field_75309_a instanceof MonsterEntity || field_75309_a instanceof PlayerEntity) && isCloseEnough(field_75309_a) && shouldExecute() && super.shouldContinueExecuting();
	}

	public boolean isCloseEnough(Entity entity)
	{
		return entity != null && field_75299_d.getDistanceSq(entity) <= getTargetDistance() * getTargetDistance();
	}

	@Override
	protected double getTargetDistance()
	{
		return EntitySentry.MAX_TARGET_DISTANCE;
	}
}
