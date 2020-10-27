package net.geforcemods.securitycraft.mixin;

import net.geforcemods.securitycraft.event.LivingHurtCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin just triggers LivingHurtCallback
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), method = "applyDamage")
    private void onDamageTaken(DamageSource source, float amount, CallbackInfo info) {
        LivingHurtCallback.EVENT.invoker().hurt(((LivingEntity) (Object) this), source, amount);
    }
}
