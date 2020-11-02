package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.InventoryScannerContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.tileentity.*;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;

public class SCContent { // TODO: Everything


    //fluids
    public static FlowableFluid FLOWING_FAKE_WATER;
    public static FlowableFluid FAKE_WATER;
    public static FlowableFluid FLOWING_FAKE_LAVA;
    public static FlowableFluid FAKE_LAVA;

    //blocks
    @HasManualPage @RegisterItemBlock public static Block ALARM;
    @HasManualPage(designedBy="Henzoid") @RegisterItemBlock public static Block BLOCK_POCKET_MANAGER;
    @HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static Block BLOCK_POCKET_WALL;
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block BOUNCING_BETTY;
    @HasManualPage @RegisterItemBlock public static Block CAGE_TRAP;
    @HasManualPage @OwnableTE @RegisterItemBlock public static Block FRAME;
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block IMS;
    @HasManualPage @RegisterItemBlock public static Block INVENTORY_SCANNER;
    public static Block INVENTORY_SCANNER_FIELD;
    @HasManualPage @RegisterItemBlock public static Block KEYCARD_READER;
    @HasManualPage @RegisterItemBlock public static Block KEYPAD;
    @HasManualPage public static Block KEYPAD_CHEST;
    @HasManualPage @RegisterItemBlock public static Block KEYPAD_FURNACE;
    public static Block LASER_FIELD;
    @HasManualPage @RegisterItemBlock public static Block LASER_BLOCK;
    public static Block SCANNER_DOOR;
    @HasManualPage @RegisterItemBlock public static Block SECURITY_CAMERA;
    @HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.TECHNICAL) public static Block TROPHY_SYSTEM;
    @HasManualPage @RegisterItemBlock public static Block USERNAME_LOGGER;
    @HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static Block MINE;
    public static Block FAKE_WATER_BLOCK;
    public static Block FAKE_LAVA_BLOCK;

    //block mines

    //reinforced blocks (ordered by vanilla building blocks creative tab order)
    //ordered by vanilla decoration blocks creative tab order
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_IRON_BARS;
    //ordered by vanilla redstone tab order

    //misc
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CHISELED_CRYSTAL_QUARTZ;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ_PILLAR;
    @OwnableTE public static Block HORIZONTAL_REINFORCED_IRON_BARS;

    //items
    @HasManualPage public static Item ADMIN_TOOL;
    @HasManualPage public static Item CAMERA_MONITOR;
    @HasManualPage public static Item CODEBREAKER;
    @HasManualPage public static Item FAKE_LAVA_BUCKET;
    @HasManualPage public static Item FAKE_WATER_BUCKET;
    @HasManualPage public static Item KEY_PANEL;
    @HasManualPage public static Item LIMITED_USE_KEYCARD;
    @HasManualPage public static Item REMOTE_ACCESS_MINE;
    @HasManualPage public static Item SCANNER_DOOR_ITEM;
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
    public static BlockEntityType<SecurityCraftTileEntity> teTypeAbstract;
    public static BlockEntityType<KeypadTileEntity> teTypeKeypad;
    public static BlockEntityType<LaserBlockTileEntity> teTypeLaserBlock;
    public static BlockEntityType<CageTrapTileEntity> teTypeCageTrap;
    public static BlockEntityType<KeycardReaderTileEntity> teTypeKeycardReader;
    public static BlockEntityType<InventoryScannerTileEntity> teTypeInventoryScanner;
    public static BlockEntityType<SecurityCameraTileEntity> teTypeSecurityCamera;
    public static BlockEntityType<UsernameLoggerTileEntity> teTypeUsernameLogger;
    public static BlockEntityType<KeypadChestTileEntity> teTypeKeypadChest;
    public static BlockEntityType<AlarmTileEntity> teTypeAlarm;
    public static BlockEntityType<KeypadFurnaceTileEntity> teTypeKeypadFurnace;
    public static BlockEntityType<IMSTileEntity> teTypeIms;
    public static BlockEntityType<ScannerDoorTileEntity> teTypeScannerDoor;
    public static BlockEntityType<TrophySystemTileEntity> teTypeTrophySystem;
    public static BlockEntityType<BlockPocketManagerTileEntity> teTypeBlockPocketManager;
    public static BlockEntityType<BlockPocketTileEntity> teTypeBlockPocket;

    //entity types
    public static EntityType<IMSBombEntity> eTypeImsBomb;
    public static EntityType<SecurityCameraEntity> eTypeSecurityCamera;

    //container types
    public static ScreenHandlerType<InventoryScannerContainer> cTypeInventoryScanner;
    public static ScreenHandlerType<KeypadFurnaceContainer> cTypeKeypadFurnace;
    public static ScreenHandlerType<GenericTEContainer> cTypeCheckPassword;
    public static ScreenHandlerType<GenericTEContainer> cTypeSetPassword;
    public static ScreenHandlerType<GenericTEContainer> cTypeUsernameLogger;
    public static ScreenHandlerType<GenericTEContainer> cTypeIMS;
    public static ScreenHandlerType<GenericTEContainer> cTypeKeycardSetup;
    public static ScreenHandlerType<GenericTEContainer> cTypeBlockPocketManager;
}
