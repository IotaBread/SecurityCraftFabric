package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.AlarmBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

public class AlarmTileEntity extends CustomizableTileEntity {

	private IntConfigOption range = new IntConfigOption(this, "range", 17, 0, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	public AlarmTileEntity()
	{
		super(SCContent.teTypeAlarm);
	}

	@Override
	public void tick(){
		if(!world.isClient)
		{
			if(cooldown > 0)
				cooldown--;

			if(isPowered && cooldown == 0)
			{
				AlarmTileEntity te = (AlarmTileEntity) world.getBlockEntity(pos);

				for(ServerPlayerEntity player : ((ServerWorld)world).getPlayers(p -> p.getBlockPos().getSquaredDistance(pos) <= Math.pow(range.get(), 2)))
				{
					player.playSound(SCSounds.ALARM.event, SoundCategory.BLOCKS, ((float) ConfigHandler.CONFIG.alarmSoundVolume), 1.0F);
				}

				te.setCooldown((ConfigHandler.CONFIG.alarmTickDelay * 20));
				world.setBlockState(pos, world.getBlockState(pos).with(AlarmBlock.FACING, world.getBlockState(pos).get(AlarmBlock.FACING)), 2);
				world.setBlockEntity(pos, te);
			}
		}

//		requestModelDataUpdate(); //TODO
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);
		tag.putInt("cooldown", cooldown);
		tag.putBoolean("isPowered", isPowered);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		if (tag.contains("cooldown"))
			cooldown = tag.getInt("cooldown");

		if (tag.contains("isPowered"))
			isPowered = tag.getBoolean("isPowered");

	}

	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ range };
	}

	private static class IntConfigOption extends IntOption
	{
		public IntConfigOption(CustomizableTileEntity te, String optionName, Integer value, Integer min, Integer increment, boolean s)
		{
			super(te, optionName, value, min, 0, increment, s);
		}

		@Override
		public Integer getMax()
		{
			return ConfigHandler.CONFIG.maxAlarmRange;
		}
	}
}
