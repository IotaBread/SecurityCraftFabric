package net.geforcemods.securitycraft.misc;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public enum SCSounds {

	ALARM("securitycraft:alarm"),
	CAMERAZOOMIN("securitycraft:camerazoomin"),
	CAMERASNAP("securitycraft:camerasnap"),
	TASERFIRED("securitycraft:taserfire"),
	ELECTRIFIED("securitycraft:electrified"),
	LOCK("securitycraft:lock");

	public final String path;
	public final Identifier location;
	public final SoundEvent event;

	private SCSounds(String path){
		this.path = path;
		location = new Identifier(path);
		event = new SoundEvent(location);
		// TODO: Register
	}
}
