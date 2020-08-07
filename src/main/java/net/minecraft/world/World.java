package net.minecraft.world;

import net.minecraft.util.registry.RegistryKey;

public abstract class World {
    public RegistryKey<World> getRegistryKey() {
        return null;
    }
}
