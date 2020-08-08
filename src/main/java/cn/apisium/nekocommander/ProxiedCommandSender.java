package cn.apisium.nekocommander;

import cn.apisium.nekocommander.impl.BukkitProxiedCommandSender;
import cn.apisium.nekocommander.impl.FabricProxiedCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ProxiedCommandSender {
    public final Object origin;
    @Nullable
    public final Object entity = null;
    public final boolean isPlayer = false;
    @SuppressWarnings("unused")
    public final double x = 0, y = 0, z = 0;
    @Nullable
    @SuppressWarnings("unused")
    public final String world = null, name = null;
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
    public static ProxiedCommandSender newInstance(@NotNull final Object obj) {
        return Utils.IS_BUKKIT ? new BukkitProxiedCommandSender(obj) : new FabricProxiedCommandSender(obj);
    }
}
