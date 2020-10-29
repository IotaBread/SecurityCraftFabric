package net.geforcemods.securitycraft.misc;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface OwnershipEvent {
    Event<OwnershipEvent> EVENT = EventFactory.createArrayBacked(OwnershipEvent.class, (listeners) -> (world, pos, player) -> {
        for (OwnershipEvent listener : listeners) {
            ActionResult result = listener.own(world, pos, player);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult own(World world, BlockPos pos, PlayerEntity player);
}
