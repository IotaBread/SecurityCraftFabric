package net.geforcemods.securitycraft.itemgroups;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SCDecorationGroup {
    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(SecurityCraft.MODID, "decoration"))
            .icon(() -> new ItemStack(SCContent.REINFORCED_OAK_STAIRS.asItem()))
            .build();

    public static ItemGroup get() {
        return ITEM_GROUP;
    }
}
