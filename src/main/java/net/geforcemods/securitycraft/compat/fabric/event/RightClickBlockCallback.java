package net.geforcemods.securitycraft.compat.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface RightClickBlockCallback {
    Event<RightClickBlockCallback> EVENT = EventFactory.createArrayBacked(RightClickBlockCallback.class, (listeners) -> (player, hand, pos, face) -> {
        for (RightClickBlockCallback listener : listeners) {
            ActionResult result = listener.rightClickBlock(player, hand, pos, face);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult rightClickBlock(PlayerEntity player, Hand hand, BlockPos pos, Direction face);
}
