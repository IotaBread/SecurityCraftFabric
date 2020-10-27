package net.geforcemods.securitycraft.mixin;

import net.geforcemods.securitycraft.event.LivingHurtCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin just triggers LivingHurtCallback
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), method = "applyDamage")
    private void onDamageTaken(DamageSource source, float amount, CallbackInfo info) {
        LivingHurtCallback.EVENT.invoker().hurt(((PlayerEntity) (Object) this), source, amount);
    }
}
