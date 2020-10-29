package net.geforcemods.securitycraft.compat.fabric.mixin;

import net.geforcemods.securitycraft.compat.fabric.event.PlayerLoggedInCallback;
import net.geforcemods.securitycraft.compat.fabric.event.PlayerLoggedOutCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// This mixin just triggers PlayerLoggedInCallback and PlayerLoggedOutCallback
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PlayerLoggedInCallback.EVENT.invoker().logIn(player);
    }

    @Inject(at = @At(value = "HEAD"), method = "remove")
    private void onPlayerLogout(ServerPlayerEntity player, CallbackInfo info) {
        PlayerLoggedOutCallback.EVENT.invoker().logOut(player);
    }
}
