package net.geforcemods.securitycraft.itemgroups;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SCTechnicalGroup {
    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(SecurityCraft.MODID, "technical"))
            .icon(() -> new ItemStack(SCContent.USERNAME_LOGGER.asItem()))
            .build();

    public static ItemGroup get() {
        return ITEM_GROUP;
    }
}
