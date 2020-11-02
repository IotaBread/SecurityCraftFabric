package net.geforcemods.securitycraft.compat.fabric.mixin;

import net.geforcemods.securitycraft.compat.fabric.FabricEntityShapeContext;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(EntityShapeContext.class)
public class EntityShapeContextMixin implements FabricEntityShapeContext {
    @Nullable
    private Entity entity;

    @Inject(at = @At(value = "TAIL"), method = "<init>(ZDLnet/minecraft/item/Item;Ljava/util/function/Predicate;)V")
    private void setEntity(boolean descending, double minY, Item heldItem, Predicate<Fluid> predicate, CallbackInfo info) {
        this.entity = null;
    }

    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/entity/Entity;)V")
    private void setEntity(Entity entity, CallbackInfo info) {
        this.entity = entity;
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.entity;
    }
}
