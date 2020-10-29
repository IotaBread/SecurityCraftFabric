package net.geforcemods.securitycraft.compat.fabric.mixin;

import net.geforcemods.securitycraft.compat.fabric.event.FillBucketCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// This mixin just triggers FillBucketCallback
@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Inject(at = @At(value = "INVOKE"), method = "use", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onBucketUsed(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack, HitResult hitResult) {
        FillBucketCallback.EVENT.invoker().fill(user, itemStack, world, hitResult);
    }
}
