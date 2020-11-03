package net.geforcemods.securitycraft.entity.ai;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class AttackRangedIfEnabledGoal extends Goal
{
	private SentryEntity sentry;
	private LivingEntity attackTarget;
	private int rangedAttackTime;
	private final int attackIntervalMin;
	private final int maxRangedAttackTime;
	private final float attackRadius;

	public AttackRangedIfEnabledGoal(RangedAttackMob attacker, int maxAttackTime, float maxAttackDistance)
	{
		sentry = (SentryEntity)attacker;
		rangedAttackTime = -1;
		attackIntervalMin = maxAttackTime;
		maxRangedAttackTime = maxAttackTime;
		attackRadius = maxAttackDistance;
		setControls(EnumSet.of(Control.MOVE, Control.LOOK));
	}

	@Override
	public boolean canStart()
	{
		LivingEntity potentialTarget = sentry.getTarget();

		if(potentialTarget == null)
			return false;
		else if(sentry.isTargetingWhitelistedPlayer(potentialTarget) || EntityUtils.isInvisible(potentialTarget))
		{
			sentry.setTarget(null);
			return false;
		}
		else
		{
			attackTarget = potentialTarget;
			return sentry.getMode() != SentryMode.IDLE;
		}
	}

	@Override
	public void stop()
	{
		attackTarget = null;
		rangedAttackTime = -3;
	}

	@Override
	public void tick() //copied from vanilla to remove pathfinding code
	{
		double targetDistance = sentry.squaredDistanceTo(attackTarget.getX(), attackTarget.getBoundingBox().minY, attackTarget.getZ());

		sentry.getLookControl().lookAt(attackTarget, 30.0F, 30.0F);

		if(--rangedAttackTime == 0)
		{
			if(!sentry.getVisibilityCache().canSee(attackTarget))
				return;

			float f = MathHelper.sqrt(targetDistance) / attackRadius;
			float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);

			sentry.attack(attackTarget, lvt_5_1_);
			rangedAttackTime = MathHelper.floor(f * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
		}
		else if(rangedAttackTime < 0)
			rangedAttackTime = MathHelper.floor((MathHelper.sqrt(targetDistance) / attackRadius) * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
	}
}
