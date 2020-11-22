package net.geforcemods.securitycraft;

//import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
//import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
//import net.geforcemods.securitycraft.containers.*;
//import net.geforcemods.securitycraft.entity.*;
//import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
//import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.compat.fabric.ObjectHolder;
import net.geforcemods.securitycraft.misc.SCSounds;
//import net.geforcemods.securitycraft.misc.conditions.*;
//import net.geforcemods.securitycraft.network.client.*;
//import net.geforcemods.securitycraft.network.server.*;
import net.geforcemods.securitycraft.tileentity.*;
import net.geforcemods.securitycraft.util.OwnableTE;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.SpawnGroup;
//import net.minecraft.entity.data.TrackedData;
//import net.minecraft.entity.data.TrackedDataHandler;
//import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.recipe.RecipeSerializer;
//import net.minecraft.screen.ScreenHandlerType;
//import net.minecraft.sound.SoundEvent;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.extensions.IForgeContainerType;
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.RegistryObject;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
//import net.minecraftforge.registries.DataSerializerEntry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class RegistrationHandler
{
//	@SubscribeEvent
	public static void registerItems(/*RegistryEvent.Register<Item> event*/)
	{
		int fieldIndex = 0;
		//register item blocks from annotated fields
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
				{
					Block block = (Block) field.get(null);

//					Registry.register(Registry.ITEM, new Identifier(SecurityCraft.MODID, String.format("item_%d", fieldIndex)), new BlockItem(block, new Item.Settings().group(SecurityCraft.groupSCDecoration).fireproof())); // Right now there isn't any @Reinforced field defined
					++fieldIndex;
				}
				else if(field.isAnnotationPresent(RegisterItemBlock.class))
				{
					int tab = field.getAnnotation(RegisterItemBlock.class).value().ordinal();
					Block block = (Block)field.get(null);

//					Registry.register(Registry.ITEM, new Identifier(SecurityCraft.MODID, String.format("item_%d", fieldIndex)), new BlockItem(block, new Item.Settings().group(tab == 0 ? SecurityCraft.groupSCTechnical : (tab == 1 ? SecurityCraft.groupSCMine : SecurityCraft.groupSCDecoration)))); // Most of the @Reinforced fields aren't defined right now
					++fieldIndex;
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

//	@SubscribeEvent
	public static void registerTileEntities(/*RegistryEvent.Register<BlockEntityType<?>> event*/)
	{
		List<Block> teOwnableBlocks = new ArrayList<>();

		//find all blocks whose tile entity is TileEntityOwnable
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(OwnableTE.class) &&
						field.get(null) != null // Just to avoid exceptions because of undefined fields
				)
					teOwnableBlocks.add((Block)field.get(null));

			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "ownable"), BlockEntityType.Builder.create(OwnableTileEntity::new, teOwnableBlocks.toArray(new Block[teOwnableBlocks.size()])).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "abstract"), BlockEntityType.Builder.create(SecurityCraftTileEntity::new, SCContent.LASER_FIELD, SCContent.INVENTORY_SCANNER_FIELD, SCContent.IRON_FENCE, SCContent.COBBLESTONE_MINE, SCContent.DIAMOND_ORE_MINE, SCContent.DIRT_MINE, SCContent.FURNACE_MINE, SCContent.GRAVEL_MINE, SCContent.SAND_MINE, SCContent.STONE_MINE, SCContent.BOUNCING_BETTY, SCContent.REINFORCED_FENCEGATE, SCContent.ANCIENT_DEBRIS_MINE, SCContent.COAL_ORE_MINE, SCContent.EMERALD_ORE_MINE, SCContent.GOLD_ORE_MINE, SCContent.GILDED_BLACKSTONE_MINE, SCContent.IRON_ORE_MINE, SCContent.LAPIS_ORE_MINE, SCContent.NETHER_GOLD_ORE_MINE, SCContent.QUARTZ_ORE_MINE, SCContent.REDSTONE_ORE_MINE).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "keypad"), BlockEntityType.Builder.create(KeypadTileEntity::new, SCContent.KEYPAD).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "laser_block"), BlockEntityType.Builder.create(LaserBlockTileEntity::new, SCContent.LASER_BLOCK).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "cage_trap"), BlockEntityType.Builder.create(CageTrapTileEntity::new, SCContent.CAGE_TRAP).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "keycard_reader"), BlockEntityType.Builder.create(KeycardReaderTileEntity::new, SCContent.KEYCARD_READER).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "inventory_scanner"), BlockEntityType.Builder.create(InventoryScannerTileEntity::new, SCContent.INVENTORY_SCANNER).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "portable_radar"), BlockEntityType.Builder.create(PortableRadarTileEntity::new, SCContent.PORTABLE_RADAR).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "security_camera"), BlockEntityType.Builder.create(SecurityCameraTileEntity::new, SCContent.SECURITY_CAMERA).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "username_logger"), BlockEntityType.Builder.create(UsernameLoggerTileEntity::new, SCContent.USERNAME_LOGGER).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "retinal_scanner"), BlockEntityType.Builder.create(RetinalScannerTileEntity::new, SCContent.RETINAL_SCANNER).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "keypad_chest"), BlockEntityType.Builder.create(KeypadChestTileEntity::new, SCContent.KEYPAD_CHEST).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "alarm"), BlockEntityType.Builder.create(AlarmTileEntity::new, SCContent.ALARM).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "claymore"), BlockEntityType.Builder.create(ClaymoreTileEntity::new, SCContent.CLAYMORE).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "keypad_furnace"), BlockEntityType.Builder.create(KeypadFurnaceTileEntity::new, SCContent.KEYPAD_FURNACE).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "ims"), BlockEntityType.Builder.create(IMSTileEntity::new, SCContent.IMS).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "protecto"), BlockEntityType.Builder.create(ProtectoTileEntity::new, SCContent.PROTECTO).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "scanner_door"), BlockEntityType.Builder.create(ScannerDoorTileEntity::new, SCContent.SCANNER_DOOR).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "secret_sign"), BlockEntityType.Builder.create(SecretSignTileEntity::new, SCContent.SECRET_OAK_SIGN, SCContent.SECRET_OAK_WALL_SIGN, SCContent.SECRET_SPRUCE_SIGN, SCContent.SECRET_SPRUCE_WALL_SIGN, SCContent.SECRET_BIRCH_SIGN, SCContent.SECRET_BIRCH_WALL_SIGN, SCContent.SECRET_JUNGLE_SIGN, SCContent.SECRET_JUNGLE_WALL_SIGN, SCContent.SECRET_ACACIA_SIGN, SCContent.SECRET_ACACIA_WALL_SIGN, SCContent.SECRET_DARK_OAK_SIGN, SCContent.SECRET_DARK_OAK_WALL_SIGN, SCContent.SECRET_CRIMSON_SIGN, SCContent.SECRET_CRIMSON_WALL_SIGN, SCContent.SECRET_WARPED_SIGN, SCContent.SECRET_WARPED_WALL_SIGN).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "motion_light"), BlockEntityType.Builder.create(MotionActivatedLightTileEntity::new, SCContent.MOTION_ACTIVATED_LIGHT).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "track_mine"), BlockEntityType.Builder.create(TrackMineTileEntity::new, SCContent.TRACK_MINE).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "trophy_system"), BlockEntityType.Builder.create(TrophySystemTileEntity::new, SCContent.TROPHY_SYSTEM).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "block_pocket_manager"), BlockEntityType.Builder.create(BlockPocketManagerTileEntity::new, SCContent.BLOCK_POCKET_MANAGER).build(null));
		// The following fields are undefined right now
//		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "block_pocket"), BlockEntityType.Builder.create(BlockPocketTileEntity::new, SCContent.BLOCK_POCKET_WALL, SCContent.REINFORCED_CRYSTAL_QUARTZ, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR).build(null));
//		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "reinforced_pressure_plate"), BlockEntityType.Builder.create(WhitelistOnlyTileEntity::new, SCContent.REINFORCED_STONE_PRESSURE_PLATE, SCContent.REINFORCED_ACACIA_PRESSURE_PLATE, SCContent.REINFORCED_BIRCH_PRESSURE_PLATE, SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE, SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE, SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE, SCContent.REINFORCED_OAK_PRESSURE_PLATE, SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE, SCContent.REINFORCED_WARPED_PRESSURE_PLATE, SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE, SCContent.REINFORCED_STONE_BUTTON, SCContent.REINFORCED_ACACIA_BUTTON, SCContent.REINFORCED_BIRCH_BUTTON, SCContent.REINFORCED_CRIMSON_BUTTON, SCContent.REINFORCED_DARK_OAK_BUTTON, SCContent.REINFORCED_JUNGLE_BUTTON, SCContent.REINFORCED_OAK_BUTTON, SCContent.REINFORCED_SPRUCE_BUTTON, SCContent.REINFORCED_WARPED_BUTTON, SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON, SCContent.REINFORCED_LEVER).build(null));
//		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "reinforced_hopper"), BlockEntityType.Builder.create(ReinforcedHopperTileEntity::new, SCContent.REINFORCED_HOPPER).build(null));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "projector"), BlockEntityType.Builder.create(ProjectorTileEntity::new, SCContent.PROJECTOR).build(null));
	}

//	@SubscribeEvent
	public static void registerEntities(/*RegistryEvent.Register<EntityType<?>> event*/)
	{ // TODO
//		EntityType<SentryEntity> sentry = (EntityType<SentryEntity>)EntityType.Builder.<SentryEntity>create(SentryEntity::new, SpawnGroup.MISC)
//				.setDimensions(1.0F, 2.0F)
//				.setTrackingRange(256)
//				.setUpdateInterval(1)
//				.setShouldReceiveVelocityUpdates(true)
//				.setCustomClientFactory((spawnEntity, world) -> new SentryEntity(SCContent.eTypeSentry, world))
//				.build(SecurityCraft.MODID + ":sentry")
//				.setRegistryName(new Identifier(SecurityCraft.MODID, "sentry"));
//
//		Registry.register(Registry.ENTITY_TYPE, EntityType.Builder.<BouncingBettyEntity>create(BouncingBettyEntity::new, SpawnGroup.MISC)
//				.setDimensions(0.5F, 0.2F)
//				.setTrackingRange(128)
//				.setUpdateInterval(1)
//				.setShouldReceiveVelocityUpdates(true)
//				.setCustomClientFactory((spawnEntity, world) -> new BouncingBettyEntity(SCContent.eTypeBouncingBetty, world))
//				.build(SecurityCraft.MODID + ":bouncingbetty")
//				.setRegistryName(new Identifier(SecurityCraft.MODID, "bouncingbetty")));
//		Registry.register(Registry.ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "imsbomb"), EntityType.Builder.<IMSBombEntity>create(IMSBombEntity::new, SpawnGroup.MISC)
//				.setDimensions(0.25F, 0.3F)
//				.setTrackingRange(256)
//				.setUpdateInterval(1)
//				.setShouldReceiveVelocityUpdates(true)
//				.setCustomClientFactory((spawnEntity, world) -> new IMSBombEntity(SCContent.eTypeImsBomb, world))
//				.build(SecurityCraft.MODID + ":imsbomb"));
//		Registry.register(Registry.ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "securitycamera"), EntityType.Builder.<SecurityCameraEntity>create(SecurityCameraEntity::new, SpawnGroup.MISC)
//				.setDimensions(0.0001F, 0.0001F)
//				.setTrackingRange(256)
//				.setUpdateInterval(20)
//				.setShouldReceiveVelocityUpdates(true)
//				.setCustomClientFactory((spawnEntity, world) -> new SecurityCameraEntity(SCContent.eTypeSecurityCamera, world))
//				.build(SecurityCraft.MODID + ":securitycamera"));
//		Registry.register(Registry.ENTITY_TYPE, sentry);
//		Registry.register(Registry.ENTITY_TYPE, new Identifier(SecurityCraft.MODID, "bullet"), EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
//				.setDimensions(0.15F, 0.1F)
//				.setTrackingRange(256)
//				.setUpdateInterval(1)
//				.setShouldReceiveVelocityUpdates(true)
//				.setCustomClientFactory((spawnEntity, world) -> new BulletEntity(SCContent.eTypeBullet, world))
//				.build(SecurityCraft.MODID + ":bullet"));
//		FabricDefaultAttributeRegistry.register(sentry, MobEntity.createMobAttributes());
	}

//	@SubscribeEvent
	public static void registerContainers(/*RegistryEvent.Register<ScreenHandlerType<?>> event*/)
	{ // TODO
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new BlockReinforcerContainer(windowId, inv, data.readBoolean())).setRegistryName(new Identifier(SecurityCraft.MODID, "block_reinforcer")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericContainer(SCContent.cTypeBriefcase, windowId)).setRegistryName(new Identifier(SecurityCraft.MODID, "briefcase")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new BriefcaseContainer(windowId, inv, new BriefcaseInventory(inv.getMainHandStack()))).setRegistryName(new Identifier(SecurityCraft.MODID, "briefcase_inventory")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericContainer(SCContent.cTypeBriefcaseSetup, windowId)).setRegistryName(new Identifier(SecurityCraft.MODID, "briefcase_setup")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new CustomizeBlockContainer(windowId, inv.player.world, data.readBlockPos(), inv)).setRegistryName(new Identifier(SecurityCraft.MODID, "customize_block")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new DisguiseModuleContainer(windowId, inv, new ModuleItemInventory(inv.getMainHandStack()))).setRegistryName(new Identifier(SecurityCraft.MODID, "disguise_module")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new InventoryScannerContainer(windowId, inv.player.world, data.readBlockPos(), inv)).setRegistryName(new Identifier(SecurityCraft.MODID, "inventory_scanner")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new KeypadFurnaceContainer(windowId, inv.player.world, data.readBlockPos(), inv)).setRegistryName(new Identifier(SecurityCraft.MODID, "keypad_furnace")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new ProjectorContainer(windowId, inv.player.world, data.readBlockPos(), inv)).setRegistryName(new Identifier(SecurityCraft.MODID, "projector")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "check_password")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeSetPassword, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "set_password")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeUsernameLogger, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "username_logger")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeIMS, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "ims")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeKeycardSetup, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "keycard_setup")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeKeyChanger, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "key_changer")));
//		Registry.register(IForgeContainerType.create((windowId, inv, data) -> new GenericTEContainer(SCContent.cTypeBlockPocketManager, windowId, inv.player.world, data.readBlockPos())).setRegistryName(new Identifier(SecurityCraft.MODID, "block_pocket_manager")));
	}

	public static void registerPackets()
	{ // TODO
//		int index = 0;
//
//		SecurityCraft.channel.registerMessage(index++, SetKeycardLevel.class, SetKeycardLevel::encode, SetKeycardLevel::decode, SetKeycardLevel::onMessage);
//		SecurityCraft.channel.registerMessage(index++, UpdateLogger.class, UpdateLogger::encode, UpdateLogger::decode, UpdateLogger::onMessage);
//		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnClient.class, UpdateNBTTagOnClient::encode, UpdateNBTTagOnClient::decode, UpdateNBTTagOnClient::onMessage);
//		SecurityCraft.channel.registerMessage(index++, PlaySoundAtPos.class, PlaySoundAtPos::encode, PlaySoundAtPos::decode, PlaySoundAtPos::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetExplosiveState.class, SetExplosiveState::encode, SetExplosiveState::decode, SetExplosiveState::onMessage);
//		SecurityCraft.channel.registerMessage(index++, GivePotionEffect.class, GivePotionEffect::encode, GivePotionEffect::decode, GivePotionEffect::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetPassword.class, SetPassword::encode, SetPassword::decode, SetPassword::onMessage);
//		SecurityCraft.channel.registerMessage(index++, CheckPassword.class, CheckPassword::encode, CheckPassword::decode, CheckPassword::onMessage);
//		SecurityCraft.channel.registerMessage(index++, MountCamera.class, MountCamera::encode, MountCamera::decode, MountCamera::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetCameraRotation.class, SetCameraRotation::encode, SetCameraRotation::decode, SetCameraRotation::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetPlayerPositionAndRotation.class, SetPlayerPositionAndRotation::encode, SetPlayerPositionAndRotation::decode, SetPlayerPositionAndRotation::onMessage);
//		SecurityCraft.channel.registerMessage(index++, OpenGui.class, OpenGui::encode, OpenGui::decode, OpenGui::onMessage);
//		SecurityCraft.channel.registerMessage(index++, ToggleOption.class, ToggleOption::encode, ToggleOption::decode, ToggleOption::onMessage);
//		SecurityCraft.channel.registerMessage(index++, RequestTEOwnableUpdate.class, RequestTEOwnableUpdate::encode, RequestTEOwnableUpdate::decode, RequestTEOwnableUpdate::onMessage);
//		SecurityCraft.channel.registerMessage(index++, UpdateTEOwnable.class, UpdateTEOwnable::encode, UpdateTEOwnable::decode, UpdateTEOwnable::onMessage);
//		SecurityCraft.channel.registerMessage(index++, UpdateSliderValue.class, UpdateSliderValue::encode, UpdateSliderValue::decode, UpdateSliderValue::onMessage);
//		SecurityCraft.channel.registerMessage(index++, RemoveCameraTag.class, RemoveCameraTag::encode, RemoveCameraTag::decode, RemoveCameraTag::onMessage);
//		SecurityCraft.channel.registerMessage(index++, InitSentryAnimation.class, InitSentryAnimation::encode, InitSentryAnimation::decode, InitSentryAnimation::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetCameraPowered.class, SetCameraPowered::encode, SetCameraPowered::decode, SetCameraPowered::onMessage);
//		SecurityCraft.channel.registerMessage(index++, UpdateNBTTagOnServer.class, UpdateNBTTagOnServer::encode, UpdateNBTTagOnServer::decode, UpdateNBTTagOnServer::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SyncTENBTTag.class, SyncTENBTTag::encode, SyncTENBTTag::decode, SyncTENBTTag::onMessage);
//		SecurityCraft.channel.registerMessage(index++, ToggleBlockPocketManager.class, ToggleBlockPocketManager::encode, ToggleBlockPocketManager::decode, ToggleBlockPocketManager::onMessage);
//		SecurityCraft.channel.registerMessage(index++, ClearLoggerServer.class, ClearLoggerServer::encode, ClearLoggerServer::decode, ClearLoggerServer::onMessage);
//		SecurityCraft.channel.registerMessage(index++, ClearLoggerClient.class, ClearLoggerClient::encode, ClearLoggerClient::decode, ClearLoggerClient::onMessage);
//		SecurityCraft.channel.registerMessage(index++, RefreshDisguisableModel.class, RefreshDisguisableModel::encode, RefreshDisguisableModel::decode, RefreshDisguisableModel::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SetSentryMode.class, SetSentryMode::encode, SetSentryMode::decode, SetSentryMode::onMessage);
//		SecurityCraft.channel.registerMessage(index++, OpenSRATGui.class, OpenSRATGui::encode, OpenSRATGui::decode, OpenSRATGui::onMessage);
//		SecurityCraft.channel.registerMessage(index++, SyncProjector.class, SyncProjector::encode, SyncProjector::decode, SyncProjector::onMessage);
//		SecurityCraft.channel.registerMessage(index++, AssembleBlockPocket.class, AssembleBlockPocket::encode, AssembleBlockPocket::decode, AssembleBlockPocket::onMessage);
	}

//	@SubscribeEvent
	public static void registerSounds(/*RegistryEvent.Register<SoundEvent> event*/)
	{
		for(int i = 0; i < SCSounds.values().length; i++)
		{
			Registry.register(Registry.SOUND_EVENT, SCSounds.values()[i].location, SCSounds.values()[i].event);
		}
	}

//	@SubscribeEvent
	public static void registerRecipeSerializers(/*RegistryEvent.Register<RecipeSerializer<?>> event*/)
	{ // TODO
//		CraftingHelper.register(ToggleKeycard1Condition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleKeycard2Condition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleKeycard3Condition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleKeycard4Condition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleKeycard5Condition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleLimitedUseKeycardCondition.Serializer.INSTANCE);
//		CraftingHelper.register(ToggleMinesCondition.Serializer.INSTANCE);
	}

//	@SubscribeEvent
	public static void registerDataSerializerEntries(/*RegistryEvent.Register<DataSerializerEntry> event*/)
	{ // TODO
//		Registry.register(Registry, new Identifier(SecurityCraft.MODID, "owner"), new DataSerializerEntry(new TrackedDataHandler<Owner>() {
//			@Override
//			public void write(PacketByteBuf buf, Owner value)
//			{
//				buf.writeString(value.getName());
//				buf.writeString(value.getUUID());
//			}
//
//			@Override
//			public Owner read(PacketByteBuf buf)
//			{
//				String name = buf.readString(Integer.MAX_VALUE / 4);
//				String uuid = buf.readString(Integer.MAX_VALUE / 4);
//
//				return new Owner(name, uuid);
//			}
//
//			@Override
//			public TrackedData<Owner> create(int id)
//			{
//				return new TrackedData<>(id, this);
//			}
//
//			@Override
//			public Owner copy(Owner value)
//			{
//				return new Owner(value.getName(), value.getUUID());
//			}
//		}));
	}

	// Cursed code
	public static void registerObjectHolders() throws IllegalAccessException {
		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(ObjectHolder.class)) {
				String value = field.getAnnotation(ObjectHolder.class).value();
				Registry<?> registry;
				if (field.getType() == BlockEntityType.class) {
					registry = Registry.BLOCK_ENTITY_TYPE;
				} else if (field.getType() == EntityType.class) {
					registry = Registry.ENTITY_TYPE;
				} else if (field.getType() == ScreenHandlerType.class) {
					registry = Registry.SCREEN_HANDLER;
				} else {
					continue;
				}

				if (registry.containsId(new Identifier(value))) {
					field.setAccessible(true);
					field.set(null, registry.get(new Identifier(value)));
				}
			}
		}
	}
}
