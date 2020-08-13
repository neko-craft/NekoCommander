package cn.apisium.nekocommander;

import cn.apisium.nekocommander.impl.BukkitProxiedCommandSender;
import cn.apisium.nekocommander.impl.FabricProxiedCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

public abstract class ProxiedCommandSender {
    public final Object origin;
    @Nullable
    public Object entity;
    public boolean isPlayer;
    public double x, y, z;
    @Nullable
    public String world, name;
    private static final WeakHashMap<Object, ProxiedCommandSender> cache = new WeakHashMap<>();
    protected ProxiedCommandSender(@NotNull final Object obj) {
        origin = obj;
    }
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public abstract ProxiedCommandSender sendMessage(@NotNull final String ...messages);
    @NotNull
    @SuppressWarnings("unused")
    public abstract ProxiedCommandSender sendMessage(@NotNull final BaseComponent ...components);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean hasPermission(@NotNull final String permission);

    @NotNull
    @SuppressWarnings("unused")
    public static ProxiedCommandSender getInstance(@NotNull final Object obj) {
        return cache.computeIfAbsent(obj, it -> newInstance(obj));
    }

    @NotNull
    public static ProxiedCommandSender newInstance(@NotNull final Object obj) {
        return Utils.IS_BUKKIT ? new BukkitProxiedCommandSender(obj) : new FabricProxiedCommandSender(obj);
    }

    @Override
    public int hashCode() {
        return origin.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProxiedCommandSender that = (ProxiedCommandSender) o;
        return origin == that.origin || origin.equals(that.origin);
    }
}
