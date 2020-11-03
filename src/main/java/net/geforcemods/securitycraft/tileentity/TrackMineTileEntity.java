package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;

public class TrackMineTileEntity extends OwnableTileEntity
{
	private boolean active = true;

	public TrackMineTileEntity()
	{
		super(SCContent.teTypeTrackMine);
	}

	public void activate()
	{
		if(!active)
		{
			active = true;
			markDirty();
		}
	}

	public void deactivate()
	{
		if(active)
		{
			active = false;
			markDirty();
		}
	}

	public boolean isActive()
	{
		return active;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		tag.putBoolean("TrackMineEnabled", active);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);
		active = tag.getBoolean("TrackMineEnabled");
	}
}
