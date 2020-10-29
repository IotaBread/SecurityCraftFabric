package net.geforcemods.securitycraft.compat.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BreakBlockCallback {
    Event<BreakBlockCallback> EVENT = EventFactory.createArrayBacked(BreakBlockCallback.class, (listeners) -> (world, pos, state, player) -> {
        for (BreakBlockCallback listener : listeners) {
            ActionResult result = listener.breakBlock(world, pos, state, player);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult breakBlock(World world, BlockPos pos, BlockState state, PlayerEntity player);
}
