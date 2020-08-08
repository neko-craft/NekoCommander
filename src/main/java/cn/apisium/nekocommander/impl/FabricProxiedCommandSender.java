package cn.apisium.nekocommander.impl;

import cn.apisium.nekocommander.ProxiedCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricProxiedCommandSender extends ProxiedCommandSender {
    @Nullable
    public final Object entity;
    public final boolean isPlayer;
    public final double x, y, z;
    @Nullable
    public final String world, name;
    public FabricProxiedCommandSender(@NotNull final Object obj) {
        super(obj);
        final net.minecraft.server.command.ServerCommandSource sss = (net.minecraft.server.command.ServerCommandSource) obj;
        name = sss.getName();
        x = sss.getPosition().x;
        y = sss.getPosition().y;
        z = sss.getPosition().z;
        world = sss.getWorld() == null ? null : sss.getWorld().getRegistryKey().getValue().toString();
        entity = sss.getEntity();
        isPlayer = entity instanceof net.minecraft.server.network.ServerPlayerEntity;
    }
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public FabricProxiedCommandSender sendMessage(@NotNull final String ...messages) {
        ((net.minecraft.server.command.ServerCommandSource) origin)
            .sendFeedback(new net.minecraft.text.LiteralText(String.join("", messages)), false);
        return this;
    }
    @NotNull
    @SuppressWarnings("unused")
    public FabricProxiedCommandSender sendMessage(@NotNull final BaseComponent ...components) {
        ((net.minecraft.server.command.ServerCommandSource) origin).sendFeedback(components.length == 0
            ? net.minecraft.text.LiteralText.EMPTY
            : net.minecraft.text.Text.Serializer.fromJson(ComponentSerializer.toString(components.length == 1 ? components[0] : new TextComponent(components)))
        , false);
        return this;
    }

    public boolean hasPermission(@NotNull final String permission) {
        return ((net.minecraft.server.command.ServerCommandSource) origin).hasPermissionLevel(4);
    }
}
