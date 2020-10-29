package net.geforcemods.securitycraft.compat.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerLoggedInCallback {
    Event<PlayerLoggedInCallback> EVENT = EventFactory.createArrayBacked(PlayerLoggedInCallback.class, (listeners) -> (player) -> {
        for (PlayerLoggedInCallback listener : listeners) {
            ActionResult result = listener.logIn(player);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult logIn(ServerPlayerEntity player);
}
