package net.geforcemods.securitycraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerLoggedOutCallback {
    Event<PlayerLoggedOutCallback> EVENT = EventFactory.createArrayBacked(PlayerLoggedOutCallback.class, (listeners) -> (player) -> {
        for (PlayerLoggedOutCallback listener : listeners) {
            ActionResult result = listener.logOut(player);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult logOut(ServerPlayerEntity player);
}
