package cn.apisium.nekocommander.impl;

import cn.apisium.nekocommander.ProxiedCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class BukkitProxiedCommandSender extends ProxiedCommandSender {
    public BukkitProxiedCommandSender(@NotNull final Object obj) {
        super(obj);
        name = ((org.bukkit.command.CommandSender) obj).getName();
        if (obj instanceof org.bukkit.entity.Entity) {
            entity = obj;
            final org.bukkit.entity.Entity e = (org.bukkit.entity.Entity) obj;
            final org.bukkit.Location loc = e.getLocation();
            x = loc.getX();
            y = loc.getY();
            z = loc.getZ();
            world = loc.getWorld() == null ? null : loc.getWorld().getName();
            isPlayer = obj instanceof org.bukkit.entity.Player;
        } else {
            entity = null;
            x = y = z = 0D;
            world = null;
            isPlayer = false;
        }
    }
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public BukkitProxiedCommandSender sendMessage(@NotNull final String ...messages) {
        ((org.bukkit.command.CommandSender) origin).sendMessage(messages);
        return this;
    }
    @NotNull
    @SuppressWarnings("unused")
    public BukkitProxiedCommandSender sendMessage(@NotNull final BaseComponent ...components) {
        ((org.bukkit.command.CommandSender) origin).spigot().sendMessage(components);
        return this;
    }

    public boolean hasPermission(@NotNull final String permission) {
        return ((org.bukkit.command.CommandSender) origin).hasPermission(permission);
    }
}
