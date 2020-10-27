package net.geforcemods.securitycraft.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public interface FillBucketCallback {
    Event<FillBucketCallback> EVENT = EventFactory.createArrayBacked(FillBucketCallback.class, (listeners) -> (player, itemStack, world, hitResult) -> {
        for (FillBucketCallback listener : listeners) {
            ActionResult result = listener.fill(player, itemStack, world, hitResult);

            if (result != ActionResult.PASS) return result;
        }

        return ActionResult.PASS;
    });

    ActionResult fill(PlayerEntity player, ItemStack itemStack, World world, HitResult hitResult);
}
