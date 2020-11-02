package net.geforcemods.securitycraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.util.NbtType;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.compat.fabric.FabricMisc;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
//import net.minecraftforge.common.util.Constants;
//import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class RetinalScannerTileEntity extends DisguisableTileEntity {

	private static final Logger LOGGER = LogManager.getLogger();
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private GameProfile ownerProfile;
	private static UserCache profileCache;
	private static MinecraftSessionService sessionService;

	public RetinalScannerTileEntity()
	{
		super(SCContent.teTypeRetinalScanner);
	}

	@Override
	public void entityViewed(LivingEntity entity){
		if(!world.isClient && !BlockUtils.getBlockProperty(world, pos, RetinalScannerBlock.POWERED) && !EntityUtils.isInvisible(entity)){
			if(!(entity instanceof PlayerEntity) && !activatedByEntities.get())
				return;

			if(entity instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof PlayerEntity && !getOwner().isOwner((PlayerEntity) entity) && !ModuleUtils.checkForModule(world, pos, (PlayerEntity)entity, ModuleType.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.RETINAL_SCANNER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner", getOwner().getName()), Formatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, RetinalScannerBlock.POWERED, true);
			world.getBlockTickScheduler().schedule(new BlockPos(pos), SCContent.RETINAL_SCANNER, 60);

			if(entity instanceof PlayerEntity && sendMessage.get())
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.RETINAL_SCANNER.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello", entity.getName()), Formatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown() {
		return 30;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities, sendMessage };
	}

	public static void setProfileCache(UserCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if(!ChatUtil.isEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner")))
		{
			if(ownerProfile == null || !getOwner().getName().equals(ownerProfile.getName()))
				setPlayerProfile(new GameProfile((UUID)null, getOwner().getName()));

			updatePlayerProfile();
			CompoundTag ownerProfileTag = new CompoundTag();
			NbtHelper.fromGameProfile(ownerProfileTag, ownerProfile);
			tag.put("ownerProfile", ownerProfileTag);
			return tag;
		}
		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if(tag.contains("ownerProfile", NbtType.COMPOUND))
			ownerProfile = NbtHelper.toGameProfile(tag.getCompound("ownerProfile"));
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return ownerProfile;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		ownerProfile = profile;
	}

	public void updatePlayerProfile() {
		if (profileCache == null && FabricMisc.getServer() != null)
			setProfileCache(FabricMisc.getServer().getUserCache());
		if(sessionService == null && FabricMisc.getServer() != null)
			setSessionService(FabricMisc.getServer().getSessionService());

		ownerProfile = updateGameProfile(ownerProfile);
	}

	private GameProfile updateGameProfile(GameProfile input) {
		if (ConfigHandler.CONFIG.retinalScannerFace && input != null && !ChatUtil.isEmpty(input.getName())) {
			if (input.isComplete() && input.getProperties().containsKey("textures"))
				return input;
			else if (profileCache != null && sessionService != null) {
				GameProfile gameprofile = profileCache.findByName(input.getName());
				if (gameprofile == null)
					return input;
				else {
					Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
					if (property == null) {
						try {
							gameprofile = sessionService.fillProfileProperties(gameprofile, true);
						}
						catch(IllegalArgumentException e) { //this seems to only happen on offline servers. log the exception nonetheless, just in case
							LOGGER.warn("========= WARNING =========");
							LOGGER.warn("The following error is likely caused by using an offline server. If you are not using an offline server (online-mode=true in the server.properties), please reach out to the SecurityCraft devs in their Discord #help channel: https://discord.gg/U8DvBAW");
							LOGGER.warn("To mitigate this error, you can set the configuration option \"retinalScannerFace\" to false, in order to disable rendering the owner's face on retinal scanners.");
							LOGGER.error("The exception's stacktrace is as follows:", e);
						}
					}
					return gameprofile;
				}
			} else return input;
		} else return input;
	}
}
