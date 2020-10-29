package net.geforcemods.securitycraft.compat.fabric.mixin;

import net.geforcemods.securitycraft.compat.fabric.event.BreakBlockCallback;
import net.geforcemods.securitycraft.compat.fabric.event.RightClickBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// This mixin just triggers RightClickBlockCallback and BreakBlockCallback
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow public ServerWorld world;

    @Shadow private GameMode gameMode;

    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), method = "interactBlock", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRightClickBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info, BlockPos blockPos) {
        RightClickBlockCallback.EVENT.invoker().rightClickBlock(player, hand, blockPos, hitResult.getSide());
    }

    @Inject(at = @At(value = "INVOKE"), method = "tryBreakBlock")
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        BreakBlockCallback.EVENT.invoker().breakBlock(this.world, pos, this.world.getBlockState(pos), player);
    }
}
