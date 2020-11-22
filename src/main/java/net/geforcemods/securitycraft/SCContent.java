package net.geforcemods.securitycraft;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.*;
import net.geforcemods.securitycraft.blocks.mines.*;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceGateBlock;
import net.geforcemods.securitycraft.compat.fabric.DeferredRegister;
import net.geforcemods.securitycraft.compat.fabric.ObjectHolder;
import net.geforcemods.securitycraft.containers.*;
import net.geforcemods.securitycraft.entity.*;
import net.geforcemods.securitycraft.fluids.FakeLavaFluid;
import net.geforcemods.securitycraft.fluids.FakeWaterFluid;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.tileentity.*;
import net.geforcemods.securitycraft.util.HasManualPage;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.SignType;
import net.minecraft.util.registry.Registry;

public class SCContent { // TODO: Everything
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(Registry.BLOCK, SecurityCraft.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(Registry.FLUID, SecurityCraft.MODID);
    public static final String KEYPAD_CHEST_PATH = "keypad_chest";

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new DeferredRegister<>(Registry.BLOCK_ENTITY_TYPE, SecurityCraft.MODID);

    //fluids
    public static final FlowableFluid FLOWING_FAKE_WATER = (FlowableFluid) FLUIDS.register("flowing_fake_water", () -> new FakeWaterFluid.Flowing());
    public static final FlowableFluid FAKE_WATER = (FlowableFluid) FLUIDS.register("fake_water", () -> new FakeWaterFluid.Source());
    public static final FlowableFluid FLOWING_FAKE_LAVA = (FlowableFluid) FLUIDS.register("flowing_fake_lava", () -> new FakeLavaFluid.Flowing());
    public static final FlowableFluid FAKE_LAVA = (FlowableFluid) FLUIDS.register("fake_lava", () -> new FakeLavaFluid.Source());

    //blocks
    @HasManualPage @RegisterItemBlock public static final Block ALARM = BLOCKS.register("alarm", () -> new AlarmBlock(prop(Material.METAL).ticksRandomly().luminance(state -> state.get(AlarmBlock.LIT) ? 15 : 0)));
    @HasManualPage(designedBy="Henzoid") @RegisterItemBlock public static final Block BLOCK_POCKET_MANAGER = BLOCKS.register("block_pocket_manager", () -> new BlockPocketManagerBlock(prop()));
    @HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block BLOCK_POCKET_WALL = BLOCKS.register("block_pocket_wall", () -> new BlockPocketWallBlock(prop().noCollision().solidBlock(BlockPocketWallBlock::isNormalCube).suffocates(BlockPocketWallBlock::causesSuffocation).blockVision(BlockPocketWallBlock::causesSuffocation)));
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block BOUNCING_BETTY = BLOCKS.register("bouncing_betty", () -> new BouncingBettyBlock(prop(Material.SUPPORTED, 1.0F)));
    @HasManualPage @RegisterItemBlock public static final Block CAGE_TRAP = BLOCKS.register("cage_trap", () -> new CageTrapBlock(propDisguisable(Material.METAL).sounds(BlockSoundGroup.METAL).noCollision()));
    @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block CHISELED_CRYSTAL_QUARTZ = BLOCKS.register("chiseled_crystal_quartz", () -> new Block(FabricBlockSettings.of(Material.STONE).strength(0.8F)));
    @HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block CRYSTAL_QUARTZ = BLOCKS.register("crystal_quartz", () -> new Block(FabricBlockSettings.of(Material.STONE).strength(0.8F)));
    @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block CRYSTAL_QUARTZ_PILLAR = BLOCKS.register("crystal_quartz_pillar", () -> new PillarBlock(FabricBlockSettings.of(Material.STONE).strength(0.8F)));
    @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block CRYSTAL_QUARTZ_SLAB = BLOCKS.register("crystal_quartz_slab", () -> new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 6.0F)));
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block CLAYMORE = BLOCKS.register("claymore", () -> new ClaymoreBlock(prop(Material.SUPPORTED)));
    @HasManualPage @OwnableTE @RegisterItemBlock public static final Block FRAME = BLOCKS.register("keypad_frame", () -> new FrameBlock(prop()));
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block IMS = BLOCKS.register("ims", () -> new IMSBlock(prop(Material.METAL, 0.7F).sounds(BlockSoundGroup.METAL)));
    @HasManualPage @RegisterItemBlock public static final Block INVENTORY_SCANNER = BLOCKS.register("inventory_scanner", () -> new InventoryScannerBlock(propDisguisable()));
    public static final Block INVENTORY_SCANNER_FIELD = BLOCKS.register("inventory_scanner_field", () -> new InventoryScannerFieldBlock(prop(Material.GLASS)));
    @HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block IRON_FENCE = BLOCKS.register("electrified_iron_fence", () -> new IronFenceBlock(prop(Material.METAL, MaterialColor.IRON).sounds(BlockSoundGroup.METAL)));
    @HasManualPage @RegisterItemBlock public static final Block KEYCARD_READER = BLOCKS.register("keycard_reader", () -> new KeycardReaderBlock(propDisguisable(Material.METAL).sounds(BlockSoundGroup.METAL)));
    @HasManualPage @RegisterItemBlock public static final Block KEYPAD = BLOCKS.register("keypad", () -> new KeypadBlock(propDisguisable(Material.METAL)));
    @HasManualPage public static final Block KEYPAD_CHEST = BLOCKS.register(KEYPAD_CHEST_PATH, () -> new KeypadChestBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD)));
    @HasManualPage @RegisterItemBlock public static final Block KEYPAD_FURNACE = BLOCKS.register("keypad_furnace", () -> new KeypadFurnaceBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL).luminance(state -> state.get(KeypadFurnaceBlock.OPEN) && state.get(KeypadFurnaceBlock.LIT) ? 13 : 0)));
    @HasManualPage @RegisterItemBlock public static final Block LASER_BLOCK = BLOCKS.register("laser_block", () -> new LaserBlock(propDisguisable(Material.METAL).ticksRandomly().sounds(BlockSoundGroup.METAL)));
    public static final Block LASER_FIELD = BLOCKS.register("laser", () -> new LaserFieldBlock(prop()));
    @HasManualPage @RegisterItemBlock public static final Block MOTION_ACTIVATED_LIGHT = BLOCKS.register("motion_activated_light", () -> new MotionActivatedLightBlock(prop(Material.GLASS).sounds(BlockSoundGroup.GLASS).luminance(state -> state.get(MotionActivatedLightBlock.LIT) ? 15 : 0)));
    @HasManualPage @OwnableTE @RegisterItemBlock public static final Block PANIC_BUTTON = BLOCKS.register("panic_button", () -> new PanicButtonBlock(false, prop().luminance(state -> state.get(PanicButtonBlock.POWERED) ? 4 : 0)));
    @HasManualPage @RegisterItemBlock public static final Block PORTABLE_RADAR = BLOCKS.register("portable_radar", () -> new PortableRadarBlock(prop(Material.SUPPORTED)));
    @HasManualPage @OwnableTE @RegisterItemBlock public static final Block PROJECTOR = BLOCKS.register("projector", () -> new ProjectorBlock(propDisguisable(Material.METAL).sounds(BlockSoundGroup.METAL).ticksRandomly()));
    @HasManualPage @RegisterItemBlock public static final Block PROTECTO = BLOCKS.register("protecto", () -> new ProtectoBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL).luminance(state -> 7)));
    @OwnableTE public static final Block REINFORCED_DOOR = BLOCKS.register("iron_door_reinforced", () -> new ReinforcedDoorBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque()));
    @HasManualPage @RegisterItemBlock(SCItemGroup.DECORATION) public static final Block REINFORCED_FENCEGATE = BLOCKS.register("reinforced_fence_gate", () -> new ReinforcedFenceGateBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL)));
    @HasManualPage @RegisterItemBlock public static final Block RETINAL_SCANNER = BLOCKS.register("retinal_scanner", () -> new RetinalScannerBlock(propDisguisable(Material.METAL).sounds(BlockSoundGroup.METAL)));
    public static final Block SCANNER_DOOR = BLOCKS.register("scanner_door", () -> new ScannerDoorBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block SECRET_OAK_SIGN = BLOCKS.register("secret_oak_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.OAK));
    public static final Block SECRET_OAK_WALL_SIGN = BLOCKS.register("secret_oak_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.OAK));
    public static final Block SECRET_SPRUCE_SIGN = BLOCKS.register("secret_spruce_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.SPRUCE));
    public static final Block SECRET_SPRUCE_WALL_SIGN = BLOCKS.register("secret_spruce_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.SPRUCE));
    public static final Block SECRET_BIRCH_SIGN = BLOCKS.register("secret_birch_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.BIRCH));
    public static final Block SECRET_BIRCH_WALL_SIGN = BLOCKS.register("secret_birch_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.BIRCH));
    public static final Block SECRET_JUNGLE_SIGN = BLOCKS.register("secret_jungle_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.JUNGLE));
    public static final Block SECRET_JUNGLE_WALL_SIGN = BLOCKS.register("secret_jungle_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.JUNGLE));
    public static final Block SECRET_ACACIA_SIGN = BLOCKS.register("secret_acacia_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.ACACIA));
    public static final Block SECRET_ACACIA_WALL_SIGN = BLOCKS.register("secret_acacia_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.ACACIA));
    public static final Block SECRET_DARK_OAK_SIGN = BLOCKS.register("secret_dark_oak_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.DARK_OAK));
    public static final Block SECRET_DARK_OAK_WALL_SIGN = BLOCKS.register("secret_dark_oak_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.DARK_OAK));
    public static final Block SECRET_CRIMSON_SIGN = BLOCKS.register("secret_crimson_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.CRIMSON));
    public static final Block SECRET_CRIMSON_WALL_SIGN = BLOCKS.register("secret_crimson_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.CRIMSON));
    public static final Block SECRET_WARPED_SIGN = BLOCKS.register("secret_warped_sign_standing", () -> new SecretStandingSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.WARPED));
    public static final Block SECRET_WARPED_WALL_SIGN = BLOCKS.register("secret_warped_sign_wall", () -> new SecretWallSignBlock(prop(Material.WOOD).sounds(BlockSoundGroup.WOOD), SignType.WARPED));
    @HasManualPage @RegisterItemBlock public static final Block SECURITY_CAMERA = BLOCKS.register("security_camera", () -> new SecurityCameraBlock(prop(Material.METAL)));
    @RegisterItemBlock(SCItemGroup.DECORATION) public static Block STAIRS_CRYSTAL_QUARTZ/* = BLOCKS.register("crystal_quartz_stairs", () -> new StairsBlock(CRYSTAL_QUARTZ.getDefaultState(), FabricBlockSettings.copy(CRYSTAL_QUARTZ)))*/; // TODO
    @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block TRACK_MINE = BLOCKS.register("track_mine", () -> new TrackMineBlock(prop(Material.METAL, 0.7F).sounds(BlockSoundGroup.METAL)));
    @HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.TECHNICAL) public static final Block TROPHY_SYSTEM = BLOCKS.register("trophy_system", () -> new TrophySystemBlock(prop(Material.METAL).sounds(BlockSoundGroup.METAL).solidBlock(TrophySystemBlock::isNormalCube)));
    @HasManualPage @RegisterItemBlock public static final Block USERNAME_LOGGER = BLOCKS.register("username_logger", () -> new LoggerBlock(propDisguisable()));
    @HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block MINE = BLOCKS.register("mine", () -> new MineBlock(prop(Material.SUPPORTED, 1.0F)));
    public static final Block FAKE_WATER_BLOCK = BLOCKS.register("fake_water_block", () -> new FakeWaterBlock(() -> FAKE_WATER, prop(Material.WATER).noCollision()));
    public static final Block FAKE_LAVA_BLOCK = BLOCKS.register("fake_lava_block", () -> new FakeLavaBlock(() -> FAKE_LAVA, prop(Material.LAVA).noCollision().ticksRandomly().luminance(state -> 15)));

    //block mines
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block STONE_MINE = BLOCKS.register("stone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 1.5F), Blocks.STONE));
    @HasManualPage @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block DIRT_MINE = BLOCKS.register("dirt_mine", () -> new BaseFullMineBlock(prop(Material.SOIL, 0.5F).sounds(BlockSoundGroup.GRAVEL), Blocks.DIRT));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block COBBLESTONE_MINE = BLOCKS.register("cobblestone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 2.0F), Blocks.COBBLESTONE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block SAND_MINE = BLOCKS.register("sand_mine", () -> new FallingBlockMineBlock(prop(Material.AGGREGATE, 0.5F).sounds(BlockSoundGroup.SAND), Blocks.SAND));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block GRAVEL_MINE = BLOCKS.register("gravel_mine", () -> new FallingBlockMineBlock(prop(Material.SOIL, 0.6F).sounds(BlockSoundGroup.GRAVEL), Blocks.GRAVEL));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block GOLD_ORE_MINE = BLOCKS.register("gold_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.GOLD_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block IRON_ORE_MINE = BLOCKS.register("iron_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.IRON_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block COAL_ORE_MINE = BLOCKS.register("coal_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.COAL_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block NETHER_GOLD_ORE_MINE = BLOCKS.register("nether_gold_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).sounds(BlockSoundGroup.NETHER_GOLD_ORE), Blocks.NETHER_GOLD_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block LAPIS_ORE_MINE = BLOCKS.register("lapis_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.LAPIS_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block DIAMOND_ORE_MINE = BLOCKS.register("diamond_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.DIAMOND_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block REDSTONE_ORE_MINE = BLOCKS.register("redstone_mine", () -> new RedstoneOreMineBlock(prop(Material.STONE, 3.0F).ticksRandomly().luminance(state -> state.get(RedstoneOreMineBlock.LIT) ? 9 : 0), Blocks.REDSTONE_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block EMERALD_ORE_MINE = BLOCKS.register("emerald_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F), Blocks.EMERALD_ORE));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block QUARTZ_ORE_MINE = BLOCKS.register("quartz_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 3.0F).sounds(BlockSoundGroup.NETHER_ORE), Blocks.NETHER_QUARTZ_ORE));
    public static final Block ANCIENT_DEBRIS_MINE = BLOCKS.register("ancient_debris_mine", () -> new BaseFullMineBlock(prop(Material.METAL, 30.0F).sounds(BlockSoundGroup.ANCIENT_DEBRIS), Blocks.ANCIENT_DEBRIS));
    @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block GILDED_BLACKSTONE_MINE = BLOCKS.register("gilded_blackstone_mine", () -> new BaseFullMineBlock(prop(Material.STONE, 1.5F).sounds(BlockSoundGroup.GILDED_BLACKSTONE), Blocks.GILDED_BLACKSTONE));
    @HasManualPage @OwnableTE @RegisterItemBlock(SCItemGroup.EXPLOSIVES) public static final Block FURNACE_MINE = BLOCKS.register("furnace_mine", () -> new FurnaceMineBlock(prop(Material.STONE, 3.5F)));

    //reinforced blocks (ordered by vanilla building blocks creative tab order)
    @HasManualPage(specialInfoKey="help.securitycraft:reinforced.info") @OwnableTE @Reinforced public static Block REINFORCED_STONE;
    @OwnableTE @Reinforced public static Block REINFORCED_GRANITE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_GRANITE;
    @OwnableTE @Reinforced public static Block REINFORCED_DIORITE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_DIORITE;
    @OwnableTE @Reinforced public static Block REINFORCED_ANDESITE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_ANDESITE;
    @OwnableTE @Reinforced public static Block REINFORCED_GRASS_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_DIRT;
    @OwnableTE @Reinforced public static Block REINFORCED_COARSE_DIRT;
    @OwnableTE @Reinforced public static Block REINFORCED_PODZOL;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_NYLIUM;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_NYLIUM;
    @OwnableTE @Reinforced public static Block REINFORCED_COBBLESTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_OAK_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_SPRUCE_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_BIRCH_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_JUNGLE_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_ACACIA_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_OAK_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_PLANKS;
    @OwnableTE @Reinforced public static Block REINFORCED_SAND;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_SAND;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAVEL;
    @OwnableTE @Reinforced public static Block REINFORCED_OAK_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_SPRUCE_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_BIRCH_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_JUNGLE_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_ACACIA_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_OAK_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_STEM;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_STEM;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_OAK_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_SPRUCE_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_BIRCH_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_JUNGLE_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_ACACIA_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_DARK_OAK_LOG;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_CRIMSON_STEM;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_WARPED_STEM;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_OAK_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_SPRUCE_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_BIRCH_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_JUNGLE_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_ACACIA_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_DARK_OAK_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_CRIMSON_HYPHAE;
    @OwnableTE @Reinforced public static Block REINFORCED_STRIPPED_WARPED_HYPHAE;
    @OwnableTE @Reinforced public static Block REINFORCED_OAK_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_SPRUCE_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_BIRCH_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_JUNGLE_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_ACACIA_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_OAK_WOOD;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_HYPHAE;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_HYPHAE;
    @OwnableTE @Reinforced public static Block REINFORCED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_LAPIS_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_CUT_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_WHITE_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_ORANGE_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_MAGENTA_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_BLUE_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_YELLOW_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_LIME_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_PINK_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAY_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_GRAY_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_CYAN_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPLE_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_BROWN_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_GREEN_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACK_WOOL;
    @OwnableTE @Reinforced public static Block REINFORCED_GOLD_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_IRON_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_OAK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SPRUCE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_BIRCH_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_JUNGLE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_ACACIA_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_OAK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_NORMAL_STONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_STONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_CUT_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_COBBLESTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_STONE_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHER_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_QUARTZ_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_CUT_RED_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPUR_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_PRISMARINE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_QUARTZ;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_RED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_STONE;
    @OwnableTE @Reinforced public static Block REINFORCED_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_BOOKSHELF;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_COBBLESTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_OBSIDIAN;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPUR_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPUR_PILLAR;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPUR_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_OAK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_DIAMOND_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_COBBLESTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_ICE;
    @OwnableTE @Reinforced public static Block REINFORCED_SNOW_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_CLAY;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHERRACK;
    @OwnableTE @Reinforced public static Block REINFORCED_SOUL_SOIL;
    @OwnableTE @Reinforced public static Block REINFORCED_BASALT;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BASALT;
    @OwnableTE @Reinforced public static Block REINFORCED_GLOWSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_STONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_STONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_CRACKED_STONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_STONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_STONE_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_MYCELIUM;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHER_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_CRACKED_NETHER_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_NETHER_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHER_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_END_STONE;
    @OwnableTE @Reinforced public static Block REINFORCED_END_STONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_SANDSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_EMERALD_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_SPRUCE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_BIRCH_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_JUNGLE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_CRIMSON_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_QUARTZ;
    @OwnableTE @Reinforced public static Block REINFORCED_QUARTZ;
    @OwnableTE @Reinforced public static Block REINFORCED_QUARTZ_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_QUARTZ_PILLAR;
    @OwnableTE @Reinforced public static Block REINFORCED_QUARTZ_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_WHITE_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_ORANGE_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_MAGENTA_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_BLUE_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_YELLOW_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_LIME_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_PINK_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAY_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_GRAY_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_CYAN_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPLE_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_BROWN_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_GREEN_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACK_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_TERRACOTTA;
    @OwnableTE @Reinforced public static Block REINFORCED_COAL_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_PACKED_ICE;
    @OwnableTE @Reinforced public static Block REINFORCED_ACACIA_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_OAK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_WHITE_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_ORANGE_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_MAGENTA_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_BLUE_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_YELLOW_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_LIME_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_PINK_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAY_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_GRAY_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_CYAN_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPLE_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_BROWN_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_GREEN_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACK_STAINED_GLASS;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_PRISMARINE;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_DARK_PRISMARINE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_SEA_LANTERN;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_RED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_CUT_RED_SANDSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_SANDSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHER_WART_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_WARPED_WART_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_NETHER_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_BONE_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_WHITE_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_ORANGE_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_MAGENTA_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_BLUE_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_YELLOW_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_LIME_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_PINK_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAY_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_GRAY_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_CYAN_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPLE_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_BROWN_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_GREEN_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACK_CONCRETE;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_ICE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_GRANITE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_STONE_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_END_STONE_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_STONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_SANDSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_QUARTZ_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_GRANITE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_ANDESITE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_NETHER_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_ANDESITE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_DIORITE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_GRANITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_RED_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_STONE_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_DIORITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_COBBLESTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_END_STONE_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_SANDSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_SMOOTH_QUARTZ_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_GRANITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_ANDESITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_NETHER_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_ANDESITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_DIORITE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHERITE_BLOCK;
    @OwnableTE @Reinforced public static Block REINFORCED_CRYING_OBSIDIAN;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACKSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACKSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACKSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_PLOISHED_BLACKSTONE_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_CHISELED_POLISHED_BLACKSTONE;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_BRICKS;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS;
    @OwnableTE @Reinforced public static Block REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS;
    //ordered by vanilla decoration blocks creative tab order
    @OwnableTE @Reinforced public static Block REINFORCED_COBWEB;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_IRON_BARS;
    @OwnableTE @Reinforced public static Block REINFORCED_CHAIN;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_REINFORCED_GLASS_PANE;
    @OwnableTE @Reinforced public static Block REINFORCED_COBBLESTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_COBBLESTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_PRISMARINE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_SANDSTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_MOSSY_STONE_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_GRANITE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_STONE_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_NETHER_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_ANDESITE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_NETHER_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_SANDSTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_END_STONE_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_DIORITE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACKSTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL;
    @OwnableTE @Reinforced public static Block REINFORCED_WHITE_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_ORANGE_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_MAGENTA_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_BLUE_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_YELLOW_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_LIME_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_PINK_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_GRAY_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_LIGHT_GRAY_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_CYAN_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_PURPLE_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_BLUE_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_BROWN_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_GREEN_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_RED_CARPET;
    @OwnableTE @Reinforced public static Block REINFORCED_BLACK_CARPET;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_WHITE_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_ORANGE_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_MAGENTA_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_YELLOW_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_LIME_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_PINK_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_GRAY_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_CYAN_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_PURPLE_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_BLUE_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_BROWN_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_GREEN_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_RED_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_BLACK_STAINED_GLASS_PANE;
    @OwnableTE @Reinforced public static Block REINFORCED_LANTERN;
    @OwnableTE @Reinforced public static Block REINFORCED_SOUL_LANTERN;
    @OwnableTE @Reinforced public static Block REINFORCED_SHROOMLIGHT;

    //ordered by vanilla redstone tab order
    @HasManualPage @Reinforced public static Block REINFORCED_LEVER;
    @HasManualPage @Reinforced public static Block REINFORCED_STONE_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_OAK_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_SPRUCE_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_BIRCH_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_JUNGLE_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_ACACIA_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_DARK_OAK_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_CRIMSON_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_WARPED_PRESSURE_PLATE;
    @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE;
    @OwnableTE @Reinforced public static Block REINFORCED_REDSTONE_LAMP;
    @HasManualPage @Reinforced public static Block REINFORCED_STONE_BUTTON;
    @Reinforced public static Block REINFORCED_OAK_BUTTON;
    @Reinforced public static Block REINFORCED_SPRUCE_BUTTON;
    @Reinforced public static Block REINFORCED_BIRCH_BUTTON;
    @Reinforced public static Block REINFORCED_JUNGLE_BUTTON;
    @Reinforced public static Block REINFORCED_ACACIA_BUTTON;
    @Reinforced public static Block REINFORCED_DARK_OAK_BUTTON;
    @Reinforced public static Block REINFORCED_CRIMSON_BUTTON;
    @Reinforced public static Block REINFORCED_WARPED_BUTTON;
    @Reinforced public static Block REINFORCED_POLISHED_BLACKSTONE_BUTTON;
    @OwnableTE @Reinforced public static Block REINFORCED_REDSTONE_BLOCK;
    @HasManualPage @Reinforced public static Block REINFORCED_HOPPER;
    @HasManualPage @OwnableTE @Reinforced(hasTint = false) public static Block REINFORCED_IRON_TRAPDOOR;
    @OwnableTE @Reinforced public static Block REINFORCED_OBSERVER;

    //misc
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CHISELED_CRYSTAL_QUARTZ;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ_PILLAR;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ_SLAB;
    @Reinforced(tint=0x0E7063) public static Block REINFORCED_CRYSTAL_QUARTZ_STAIRS;
    @OwnableTE public static Block HORIZONTAL_REINFORCED_IRON_BARS;
    @OwnableTE @Reinforced public static Block REINFORCED_GRASS_PATH;

    //items
    @HasManualPage public static Item ADMIN_TOOL;
    public static Item ACIENT_DEBRIS_MINE_ITEM;
    @HasManualPage public static Item BRIEFCASE;
    @HasManualPage public static Item CAMERA_MONITOR;
    @HasManualPage public static Item CODEBREAKER;
    @HasManualPage public static Item CRYSTAL_QUARTZ_ITEM;
    @HasManualPage public static Item FAKE_LAVA_BUCKET;
    @HasManualPage public static Item FAKE_WATER_BUCKET;
    @HasManualPage public static Item KEYCARD_LVL_1;
    @HasManualPage public static Item KEYCARD_LVL_2;
    @HasManualPage public static Item KEYCARD_LVL_3;
    @HasManualPage public static Item KEYCARD_LVL_4;
    @HasManualPage public static Item KEYCARD_LVL_5;
    @HasManualPage public static Item KEY_PANEL;
    public static Item KEYPAD_CHEST_ITEM;
    @HasManualPage public static Item LIMITED_USE_KEYCARD;
    @HasManualPage public static Item REINFORCED_DOOR_ITEM;
    @HasManualPage public static Item REMOTE_ACCESS_MINE;
    @HasManualPage public static Item REMOTE_ACCESS_SENTRY;
    @HasManualPage public static Item SCANNER_DOOR_ITEM;
    @HasManualPage public static Item SC_MANUAL;
    @HasManualPage public static Item SECRET_OAK_SIGN_ITEM;
    public static Item SECRET_SPRUCE_SIGN_ITEM;
    public static Item SECRET_BIRCH_SIGN_ITEM;
    public static Item SECRET_JUNGLE_SIGN_ITEM;
    public static Item SECRET_ACACIA_SIGN_ITEM;
    public static Item SECRET_DARK_OAK_SIGN_ITEM;
    public static Item SECRET_CRIMSON_SIGN_ITEM;
    public static Item SECRET_WARPED_SIGN_ITEM;
    @HasManualPage(designedBy="Henzoid") public static Item SENTRY;
    @HasManualPage public static Item TASER;
    public static Item TASER_POWERED;
    @HasManualPage public static Item UNIVERSAL_BLOCK_MODIFIER;
    @HasManualPage public static Item UNIVERSAL_BLOCK_REINFORCER_LVL_1;
    @HasManualPage public static Item UNIVERSAL_BLOCK_REINFORCER_LVL_2;
    @HasManualPage public static Item UNIVERSAL_BLOCK_REINFORCER_LVL_3;
    @HasManualPage public static Item UNIVERSAL_BLOCK_REMOVER;
    @HasManualPage public static Item UNIVERSAL_KEY_CHANGER;
    @HasManualPage public static Item UNIVERSAL_OWNER_CHANGER;
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
    @ObjectHolder(SecurityCraft.MODID + ":ownable")
    public static BlockEntityType<OwnableTileEntity> teTypeOwnable;
    @ObjectHolder(SecurityCraft.MODID + ":abstract")
    public static BlockEntityType<SecurityCraftTileEntity> teTypeAbstract;
    @ObjectHolder(SecurityCraft.MODID + ":keypad")
    public static BlockEntityType<KeypadTileEntity> teTypeKeypad;
    @ObjectHolder(SecurityCraft.MODID + ":laser_block")
    public static BlockEntityType<LaserBlockTileEntity> teTypeLaserBlock;
    @ObjectHolder(SecurityCraft.MODID + ":cage_trap")
    public static BlockEntityType<CageTrapTileEntity> teTypeCageTrap;
    @ObjectHolder(SecurityCraft.MODID + ":keycard_reader")
    public static BlockEntityType<KeycardReaderTileEntity> teTypeKeycardReader;
    @ObjectHolder(SecurityCraft.MODID + ":inventory_scanner")
    public static BlockEntityType<InventoryScannerTileEntity> teTypeInventoryScanner;
    @ObjectHolder(SecurityCraft.MODID + ":portable_radar")
    public static BlockEntityType<PortableRadarTileEntity> teTypePortableRadar;
    @ObjectHolder(SecurityCraft.MODID + ":security_camera")
    public static BlockEntityType<SecurityCameraTileEntity> teTypeSecurityCamera;
    @ObjectHolder(SecurityCraft.MODID + ":username_logger")
    public static BlockEntityType<UsernameLoggerTileEntity> teTypeUsernameLogger;
    @ObjectHolder(SecurityCraft.MODID + ":retinal_scanner")
    public static BlockEntityType<RetinalScannerTileEntity> teTypeRetinalScanner;
    @ObjectHolder(SecurityCraft.MODID + ":keypad_chest")
    public static BlockEntityType<KeypadChestTileEntity> teTypeKeypadChest;
    @ObjectHolder(SecurityCraft.MODID + ":alarm")
    public static BlockEntityType<AlarmTileEntity> teTypeAlarm;
    @ObjectHolder(SecurityCraft.MODID + ":claymore")
    public static BlockEntityType<ClaymoreTileEntity> teTypeClaymore;
    @ObjectHolder(SecurityCraft.MODID + ":keypad_furnace")
    public static BlockEntityType<KeypadFurnaceTileEntity> teTypeKeypadFurnace;
    @ObjectHolder(SecurityCraft.MODID + ":ims")
    public static BlockEntityType<IMSTileEntity> teTypeIms;
    @ObjectHolder(SecurityCraft.MODID + ":protecto")
    public static BlockEntityType<ProtectoTileEntity> teTypeProtecto;
    @ObjectHolder(SecurityCraft.MODID + ":scanner_door")
    public static BlockEntityType<ScannerDoorTileEntity> teTypeScannerDoor;
    @ObjectHolder(SecurityCraft.MODID + ":secret_sign")
    public static BlockEntityType<SecretSignTileEntity> teTypeSecretSign;
    @ObjectHolder(SecurityCraft.MODID + ":motion_light")
    public static BlockEntityType<MotionActivatedLightTileEntity> teTypeMotionLight;
    @ObjectHolder(SecurityCraft.MODID + ":track_mine")
    public static BlockEntityType<TrackMineTileEntity> teTypeTrackMine;
    @ObjectHolder(SecurityCraft.MODID + ":trophy_system")
    public static BlockEntityType<TrophySystemTileEntity> teTypeTrophySystem;
    @ObjectHolder(SecurityCraft.MODID + ":block_pocket_manager")
    public static BlockEntityType<BlockPocketManagerTileEntity> teTypeBlockPocketManager;
    @ObjectHolder(SecurityCraft.MODID + ":block_pocket")
    public static BlockEntityType<BlockPocketTileEntity> teTypeBlockPocket;
    @ObjectHolder(SecurityCraft.MODID + ":reinforced_pressure_plate")
    public static BlockEntityType<WhitelistOnlyTileEntity> teTypeWhitelistOnly;
    @ObjectHolder(SecurityCraft.MODID + ":reinforced_hopper")
    public static BlockEntityType<ReinforcedHopperTileEntity> teTypeReinforcedHopper;
    @ObjectHolder(SecurityCraft.MODID + ":projector")
    public static BlockEntityType<ProjectorTileEntity> teTypeProjector;

    //entity types
    public static EntityType<BouncingBettyEntity> eTypeBouncingBetty;
    public static EntityType<IMSBombEntity> eTypeImsBomb;
    public static EntityType<SecurityCameraEntity> eTypeSecurityCamera;
    public static EntityType<SentryEntity> eTypeSentry;
    public static EntityType<BulletEntity> eTypeBullet;

    //container types
    public static ScreenHandlerType<BlockReinforcerContainer> cTypeBlockReinforcer;
    public static ScreenHandlerType<GenericContainer> cTypeBriefcase;
    public static ScreenHandlerType<BriefcaseContainer> cTypeBriefcaseInventory;
    public static ScreenHandlerType<GenericContainer> cTypeBriefcaseSetup;
    public static ScreenHandlerType<CustomizeBlockContainer> cTypeCustomizeBlock;
    public static ScreenHandlerType<DisguiseModuleContainer> cTypeDisguiseModule;
    public static ScreenHandlerType<InventoryScannerContainer> cTypeInventoryScanner;
    public static ScreenHandlerType<KeypadFurnaceContainer> cTypeKeypadFurnace;
    public static ScreenHandlerType<ProjectorContainer> cTypeProjector;
    public static ScreenHandlerType<GenericTEContainer> cTypeCheckPassword;
    public static ScreenHandlerType<GenericTEContainer> cTypeSetPassword;
    public static ScreenHandlerType<GenericTEContainer> cTypeUsernameLogger;
    public static ScreenHandlerType<GenericTEContainer> cTypeIMS;
    public static ScreenHandlerType<GenericTEContainer> cTypeKeycardSetup;
    public static ScreenHandlerType<GenericTEContainer> cTypeKeyChanger;
    public static ScreenHandlerType<GenericTEContainer> cTypeBlockPocketManager;

    private static final AbstractBlock.Settings prop() {
        return prop(Material.STONE);
    }

    private static final AbstractBlock.Settings prop(Material mat) {
        return FabricBlockSettings.of(mat).strength(-1.0F, 6000000.0F);
    }

    private static final AbstractBlock.Settings prop(Material mat, float hardness) {
        return FabricBlockSettings.of(mat).strength(hardness, 6000000.0F);
    }

    private static final AbstractBlock.Settings prop(Material mat, MaterialColor color) {
        return FabricBlockSettings.of(mat, color).strength(-1.0F, 6000000.0F);
    }

    private static final AbstractBlock.Settings propDisguisable() {
        return propDisguisable(Material.STONE);
    }

    private static final AbstractBlock.Settings propDisguisable(Material mat) {
        return prop(mat).nonOpaque().solidBlock(DisguisableBlock::isNormalCube).suffocates(DisguisableBlock::isSuffocating);
    }

    private static final Item.Settings itemProp(ItemGroup itemGroup) {
        return new FabricItemSettings().group(itemGroup);
    }
}
