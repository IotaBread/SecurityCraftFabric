package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.SCSounds;
//import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
//import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
//import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Predicate;

public class TaserItem extends Item {

	public boolean powered;

	public TaserItem(Settings properties, boolean isPowered){
		super(properties);

		powered = isPowered;
	}

//	@Override // TODO
//	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items)
//	{
//		if((group == SecurityCraft.groupSCTechnical || group == ItemGroup.SEARCH) && !powered)
//			items.add(new ItemStack(this));
//	}

//	@Override // Forge method
//	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
//	{
//		return slotChanged || ((oldStack.getItem() == SCContent.TASER && newStack.getItem() == SCContent.TASER_POWERED) || (oldStack.getItem() == SCContent.TASER_POWERED && newStack.getItem() == SCContent.TASER));
//	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getStackInHand(hand);

		if(!world.isClient)
		{
			if(!stack.isDamaged()){
				if(player.isInSneakingPose() && (player.isCreative() || !powered))
				{
					ItemStack oneRedstone = new ItemStack(Items.REDSTONE, 1);

					if(player.isCreative())
					{
						if(player.getStackInHand(hand).getItem() == SCContent.TASER)
							setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER_POWERED, 1));
						else
							setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER, 1));
					}
					else if(player.inventory.contains(oneRedstone))
					{
						int redstoneSlot = player.inventory.method_7371(oneRedstone);
						ItemStack redstoneStack = player.inventory.getStack(redstoneSlot);

						redstoneStack.setCount(redstoneStack.getCount() - 1);
						player.inventory.setStack(redstoneSlot, redstoneStack);
						setSlotBasedOnHand(player, hand, new ItemStack(SCContent.TASER_POWERED, 1));
					}

					return TypedActionResult.pass(stack);
				}

				int range = 11;
				Vec3d startVec = player.getCameraPosVec(1.0F);
				Vec3d lookVec = player.getRotationVec(1.0F).multiply(range);
				Vec3d endVec = startVec.add(lookVec);
				Box boundingBox = player.getBoundingBox().stretch(lookVec).expand(1, 1, 1);
				EntityHitResult entityRayTraceResult = rayTraceEntities(player, startVec, endVec, boundingBox, s -> s instanceof LivingEntity, range * range);

//				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new PlaySoundAtPos(player.getX(), player.getY(), player.getZ(), SCSounds.TASERFIRED.path, 1.0F, "players")); // TODO

				if (entityRayTraceResult != null)
				{
					LivingEntity entity = (LivingEntity)entityRayTraceResult.getEntity();

					if(!entity.isBlocking() && entity.damage(CustomDamageSources.TASER, powered ? 2.0F : 1.0F))
					{
						int strength = powered ? 4 : 1;
						int length = powered ? 400 : 200;

						entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, length, strength));
						entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, length, strength));
						entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, length, strength));
					}
				}

				if(!player.isCreative())
				{
					if(powered)
					{
						ItemStack taser = new ItemStack(SCContent.TASER, 1);

						taser.damage(150, player, p -> p.sendToolBreakStatus(hand));
						setSlotBasedOnHand(player, hand, taser);
					}
					else
						stack.damage(150, player, p -> p.sendToolBreakStatus(hand));
				}
			}
		}

		return TypedActionResult.pass(stack);
	}

	//Copied from ProjectileHelper to get rid of the @OnlyIn(Dist.CLIENT) annotation
	private static EntityHitResult rayTraceEntities(Entity shooter, Vec3d startVec, Vec3d endVec, Box boundingBox, Predicate<Entity> filter, double dist)
	{
		World world = shooter.world;
		double distance = dist;
		Entity rayTracedEntity = null;
		Vec3d hitVec = null;

		for(Entity entity : world.getOtherEntities(shooter, boundingBox, filter))
		{
			Box boxToCheck = entity.getBoundingBox().expand(entity.getTargetingMargin());
			Optional<Vec3d> optional = boxToCheck.raycast(startVec, endVec);

			if(boxToCheck.contains(startVec))
			{
				if(distance >= 0.0D)
				{
					rayTracedEntity = entity;
					hitVec = optional.orElse(startVec);
					distance = 0.0D;
				}
			}
			else if(optional.isPresent())
			{
				Vec3d vector = optional.get();
				double sqDist = startVec.squaredDistanceTo(vector);

				if(sqDist < distance || distance == 0.0D)
				{
					if(entity.getRootVehicle() == shooter.getRootVehicle() /*&& !entity.canRiderInteract()*/) // TODO
					{
						if(distance == 0.0D)
						{
							rayTracedEntity = entity;
							hitVec = vector;
						}
					}
					else
					{
						rayTracedEntity = entity;
						hitVec = vector;
						distance = sqDist;
					}
				}
			}
		}

		return rayTracedEntity == null ? null : new EntityHitResult(rayTracedEntity, hitVec);
	}
	private void setSlotBasedOnHand(PlayerEntity player, Hand hand, ItemStack taser)
	{
		if(hand == Hand.MAIN_HAND)
			player.equipStack(EquipmentSlot.MAINHAND, taser);
		else
			player.equipStack(EquipmentSlot.OFFHAND, taser);
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(!world.isClient)
			if(par1ItemStack.getDamage() >= 1)
				par1ItemStack.setDamage(par1ItemStack.getDamage() - 1);
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
