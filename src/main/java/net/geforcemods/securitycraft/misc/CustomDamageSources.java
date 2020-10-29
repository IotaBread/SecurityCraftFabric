package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.compat.fabric.FabricDamageSource;
import net.minecraft.entity.damage.DamageSource;

public class CustomDamageSources {
    public static final DamageSource LASER = new FabricDamageSource("securitycraft.laser");
    public static final DamageSource FAKE_WATER = new FabricDamageSource("securitycraft.fakeWater").setBypassesArmor();
    public static final DamageSource ELECTRICITY = new FabricDamageSource("securitycraft.electricity").setBypassesArmor();
    public static final DamageSource TASER = new FabricDamageSource("securitycraft.taser");
}
