package cn.apisium.nekocommander;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxiedCommandSender {
    public final Object origin;
    @Nullable
    public final Object entity;
    public final boolean isPlayer;
    public final double x, y, z;
    @Nullable
    public final String world, name;
    public ProxiedCommandSender(@NotNull final Object obj) {
        origin = obj;
        if (Utils.IS_BUKKIT) {
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
        } else {
            final net.minecraft.server.command.ServerCommandSource sss = (net.minecraft.server.command.ServerCommandSource) obj;
            name = sss.getName();
            x = sss.getPosition().x;
            y = sss.getPosition().y;
            z = sss.getPosition().z;
            world = sss.getWorld() == null ? null : sss.getWorld().getRegistryKey().getValue().toString();
            entity = sss.getEntity();
            isPlayer = entity instanceof net.minecraft.server.network.ServerPlayerEntity;
        }
    }
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public ProxiedCommandSender sendMessage(@NotNull final String ...messages) {
        if (Utils.IS_BUKKIT) ((org.bukkit.command.CommandSender) origin).sendMessage(messages);
        else ((net.minecraft.server.command.ServerCommandSource) origin)
            .sendFeedback(new net.minecraft.text.LiteralText(String.join("", messages)), false);
        return this;
    }
    @NotNull
    @SuppressWarnings("unused")
    public ProxiedCommandSender sendMessage(@NotNull final BaseComponent ...components) {
        if (Utils.IS_BUKKIT) ((org.bukkit.command.CommandSender) origin).spigot().sendMessage(components);
        else ((net.minecraft.server.command.ServerCommandSource) origin).sendFeedback(components.length == 0
            ? net.minecraft.text.LiteralText.EMPTY
            : net.minecraft.text.Text.Serializer.fromJson(ComponentSerializer.toString(components.length == 1 ? components[0] : new TextComponent(components)))
        , false);
        return this;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasPermission(@NotNull final String permission) {
        return Utils.IS_BUKKIT
            ? ((org.bukkit.command.CommandSender) origin).hasPermission(permission)
            : ((net.minecraft.server.command.ServerCommandSource) origin).hasPermissionLevel(4);
    }
}
