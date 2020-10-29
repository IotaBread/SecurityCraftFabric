package net.geforcemods.securitycraft.compat.fabric;

import net.minecraft.entity.damage.DamageSource;

public class FabricDamageSource extends DamageSource {
    public FabricDamageSource(String name) {
        super(name);
    }

    @Override
    public FabricDamageSource setProjectile() {
        super.setProjectile();
        return this;
    }

    @Override
    public FabricDamageSource setExplosive() {
        super.setExplosive();
        return this;
    }

    @Override
    public FabricDamageSource setBypassesArmor() {
        super.setBypassesArmor();
        return this;
    }

    @Override
    public FabricDamageSource setOutOfWorld() {
        super.setOutOfWorld();
        return this;
    }

    @Override
    public FabricDamageSource setUnblockable() {
        super.setUnblockable();
        return this;
    }

    @Override
    public FabricDamageSource setFire() {
        super.setFire();
        return this;
    }

    @Override
    public FabricDamageSource setScaledWithDifficulty() {
        super.setScaledWithDifficulty();
        return this;
    }

    @Override
    public FabricDamageSource setUsesMagic() {
        super.setUsesMagic();
        return this;
    }
}
