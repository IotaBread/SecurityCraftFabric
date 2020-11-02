package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.tileentity.IMSTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;

public class SCContent { // TODO: Everything


    //fluids

    //blocks
    @HasManualPage @RegisterItemBlock public static Block ALARM;
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block BOUNCING_BETTY;
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block IMS;
    @HasManualPage @RegisterItemBlock public static Block SECURITY_CAMERA;
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block MINE;

    //block mines

    //reinforced blocks (ordered by vanilla building blocks creative tab order)
    //ordered by vanilla decoration blocks creative tab order
    //ordered by vanilla redstone tab order

    //misc

    //items
    @HasManualPage public static Item ADMIN_TOOL;
    @HasManualPage public static Item CAMERA_MONITOR;
    @HasManualPage public static Item REMOTE_ACCESS_MINE;
    @HasManualPage public static Item WIRE_CUTTERS;

    //modules
    @HasManualPage public static ModuleItem BLACKLIST_MODULE;
    @HasManualPage public static ModuleItem DISGUISE_MODULE;
    @HasManualPage public static ModuleItem HARMING_MODULE;
    @HasManualPage public static ModuleItem REDSTONE_MODULE;
    @HasManualPage public static ModuleItem SMART_MODULE;
    @HasManualPage public static ModuleItem STORAGE_MODULE;
    @HasManualPage public static ModuleItem WHITELIST_MODULE;

    //tile entity types
    public static BlockEntityType<OwnableTileEntity> teTypeOwnable;
    public static BlockEntityType<SecurityCameraTileEntity> teTypeSecurityCamera;
    public static BlockEntityType<AlarmTileEntity> teTypeAlarm;
    public static BlockEntityType<IMSTileEntity> teTypeIms;
    public static BlockEntityType<TrophySystemTileEntity> teTypeTrophySystem;

    //entity types
    public static EntityType<IMSBombEntity> eTypeImsBomb;
    public static EntityType<SecurityCameraEntity> eTypeSecurityCamera;

    //container types
    public static ScreenHandlerType<GenericTEContainer> cTypeIMS;
}
