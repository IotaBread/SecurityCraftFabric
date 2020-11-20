package net.geforcemods.securitycraft.compat.fabric;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredRegister<R> { // Just a class to dynamically register things. Replacement for the forge class with same name
    private Registry<R> registry;
    private final String modId;
    private Map<String, Supplier<R>> entries = new LinkedHashMap<>();

    public DeferredRegister(Registry<R> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public R register(String name, Supplier<R> supplier) { // This doesn't return a RegistryObject because I'm too lazy to replace everything on the SCContent
        entries.put(name, supplier);
        return supplier.get();
    }

    public void register() {
        entries.forEach((name, supplier) -> Registry.register(this.registry, new Identifier(this.modId, name), supplier.get()));
    }
}
