package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

public class PortableRadarTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", ConfigHandler.CONFIG.portableRadarSearchRadius, 5.0D, 50.0D, 5.0D);
	private IntOption searchDelayOption = new IntOption("searchDelay", ConfigHandler.CONFIG.portableRadarDelay, 4, 10, 1);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private BooleanOption enabledOption = new BooleanOption("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";
	private int ticksUntilNextSearch = getSearchDelay();

	public PortableRadarTileEntity()
	{
		super(SCContent.teTypePortableRadar);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!world.isClient && enabledOption.get() && ticksUntilNextSearch-- <= 0)
		{
			ticksUntilNextSearch = getSearchDelay();

			ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(getOwner().getName());
			Box area = new Box(pos).expand(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<PlayerEntity> entities = world.getEntitiesByClass(PlayerEntity.class, area, e -> {
				boolean isNotWhitelisted = true;

				if(hasModule(ModuleType.WHITELIST))
					isNotWhitelisted = !ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(e.getName().getString().toLowerCase());

				return e != owner && isNotWhitelisted;
			});

			if(hasModule(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, !entities.isEmpty());

			if(owner != null)
			{
				for(PlayerEntity e : entities)
				{
					if(shouldSendMessage(e))
					{
						MutableText attackedName = e.getName().copy().formatted(Formatting.ITALIC);
						MutableText text;

						if(hasCustomSCName())
							text = ClientUtils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomSCName().copy().formatted(Formatting.ITALIC));
						else
							text = ClientUtils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, Utils.getFormattedCoordinates(pos));

						PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.PORTABLE_RADAR.getTranslationKey()), text, Formatting.BLUE);
						setSentMessage();
					}
				}
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.REDSTONE)
			PortableRadarBlock.togglePowerOutput(world, pos, false);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		tag.putBoolean("shouldSendNewMessage", shouldSendNewMessage);
		tag.putString("lastPlayerName", lastPlayerName);
		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if (tag.contains("shouldSendNewMessage"))
			shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");

		if (tag.contains("lastPlayerName"))
			lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(PlayerEntity player) {
		if(!player.getName().getString().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName().getString();
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !player.getName().getString().equals(getOwner().getName());
	}

	public void setSentMessage() {
		shouldSendNewMessage = false;
	}

	public double getSearchRadius() {
		return searchRadiusOption.get();
	}

	public int getSearchDelay() {
		return searchDelayOption.get() * 20;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.REDSTONE, ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
