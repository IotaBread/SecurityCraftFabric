package net.geforcemods.securitycraft.compat.fabric;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

// This class is just to make everything public, and I'm not even sure if it's going to work
public class FabricSlot extends Slot {
    private final int index;
    public final Inventory inventory;
    public int id;
    public final int x;
    public final int y;

    public FabricSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.index = index;
        this.x = x;
        this.y = y;
    }

    @Override
    public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
        int i = itemStack.getCount() - originalItem.getCount();
        if (i > 0) {
            this.onCrafted(itemStack, i);
        }

    }

    @Override
    public void onCrafted(ItemStack stack, int amount) {
    }

    @Override
    public void onTake(int amount) {
    }

    @Override
    public void onCrafted(ItemStack stack) {
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        this.markDirty();
        return stack;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getStack() {
        return this.inventory.getStack(this.index);
    }

    @Override
    public boolean hasStack() {
        return !this.getStack().isEmpty();
    }

    @Override
    public void setStack(ItemStack stack) {
        this.inventory.setStack(this.index, stack);
        this.markDirty();
    }

    @Override
    public void markDirty() {
        this.inventory.markDirty();
    }

    @Override
    public int getMaxItemCount() {
        return this.inventory.getMaxCountPerStack();
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return this.getMaxItemCount();
    }

    @Override
    @Nullable
    @Environment(EnvType.CLIENT)
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return null;
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.inventory.removeStack(this.index, amount);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean doDrawHoveringEffect() {
        return true;
    }

    public int getSlotIndex() {
        return this.index;
    }
}
